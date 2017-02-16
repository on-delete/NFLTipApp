var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var mysql = require('mysql');
var parseString = require('xml2js').parseString;
var limit = require("simple-rate-limiter");
var request = limit(require("request")).to(1).per(1000);
var requestWebsite = require("request");
var cheerio = require("cheerio");
var schedule = require('node-schedule');
var winston = require('winston');

winston.add(
    winston.transports.File, {
        filename: 'serverlog.log',
        level: 'info',
        json: true,
        timestamp: true
    }
);

var index = require('./routes/index');

var app = express();

var pool = mysql.createPool({
    host: 'localhost',
    user: 'andredb',
    password: 'database123',
    database: 'nfltipappdb'
});

var reg_saison_weeks = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17];
var post_saison_weeks = [18, 19, 20, 22];
var saison_parts = ['REG', 'POST'];
var saison_years = [2016];
var request_string = 'http://www.nfl.com/ajax/scorestrip?season=';

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', index);

startUpdateTask();

function startUpdateTask(){
    var rule = new schedule.RecurrenceRule();
    rule.dayOfWeek = [new schedule.Range(0, 6)];
    rule.hour = [1, 13];
    rule.minute = 0;

    var j = schedule.scheduleJob(rule, function(){
        var d = new Date();
        winston.info('new update is started at ' + d);
        updateSchedule();
        updateStandings();
        updatePredictionsPlus();
    });
}

app.post('/nameExisting', function (req, res, next) {
    var resp = {
        "result": "",
        "message": ""
    };

    pool.getConnection(function (err, connection) {
        if (err) {
            resp.result = "failed";
            resp.message = err.message;
            sendResponse(res, resp, connection);
        }
        else {
            var sql = "SELECT user_name FROM user WHERE user_name = ?";
            var inserts = [req.body.name];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, rows) {
                if (err) {
                    resp.result = "failed";
                    resp.message = err.message;
                    sendResponse(res, resp, connection);
                }
                else {
                    resp.result = "success";
                    if (rows.length > 0) {
                        resp.message = "username already used";
                    }
                    else {
                        resp.message = "username unused";
                    }
                    sendResponse(res, resp, connection);
                }
            });
        }
    });
});

app.post('/registerUser', function (req, res, next) {
    var resp = {
        "result": "",
        "message": ""
    };

    pool.getConnection(function (err, connection) {
        if (err) {
            resp.result = "failed";
            resp.message = err.message;
            sendResponse(res, resp, connection);
        }
        else {
            var sql = "INSERT INTO user (user_name, user_email, user_password) VALUES (?, ?, ?)";
            var inserts = [req.body.user.name, req.body.user.email, req.body.user.password];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, result) {
                if (err) {
                    resp.result = "failed";
                    resp.message = err.message;
                    sendResponse(res, resp, connection);
                }
                else {
                    resp.result = "success";
                    resp.message = "user registered";
                    initPredictionsForNewUser(result.insertId);
                    initPredictionsPlusForNewUser(result.insertId);
                    sendResponse(res, resp, connection);
                }
            });
        }
    });
});

function initPredictionsForNewUser(userId){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = "INSERT INTO predictions (game_id, predicted, home_team_predicted, user_id) select game_id, 'false', 'NULL', ? from games;";
            var inserts = [userId];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, result) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                }
            });
        }
        connection.release();
    });
}

function initPredictionsPlusForNewUser(userId){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = "INSERT INTO predictions_plus (user_id, superbowl, afc_winner, nfc_winner, best_offense, best_defense) VALUES (?, 999, 999, 999, 999, 999);";
            var inserts = [userId];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, result) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                }
            });
        }
        connection.release();
    });
}

app.post('/registerLogin', function (req, res, next) {
    var resp = {
        "result": "",
        "message": "",
        "user": {
            "name": req.body.user.name,
            "uuid": ""
        }
    };

    pool.getConnection(function (err, connection) {
        if (err) {
            resp.result = "failed";
            resp.message = err.message;
            sendResponse(res, resp, connection);
        }
        else {
            var sql = "SELECT user_password, user_id FROM user WHERE user_name = ?";
            var inserts = [req.body.user.name];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, rows) {
                if (err) {
                    resp.result = "failed";
                    resp.message = err.message;
                    sendResponse(res, resp, connection);
                }
                else {
                    if(rows[0] !== undefined) {
                        var password = rows[0].user_password;
                        if (password === req.body.user.password) {
                            resp.result = "success";
                            resp.message = "login successfull";
                            resp.user.uuid = rows[0].user_id;

                        } else {
                            resp.result = "success";
                            resp.message = "password wrong";
                        }
                    }
                    else{
                        resp.result = "success";
                        resp.message = "user not found";
                    }
                    sendResponse(res, resp, connection);
                }
            });
        }
    });
});

app.get('/updateSchedule', function (req, res, next) {
    updateSchedule();
    res.end("OK");
});

function updateSchedule() {
    saison_years.forEach(function (year) {
        saison_parts.forEach(function (spart) {
            var weeks;
            switch (spart) {
                case 'REG' :
                    weeks = reg_saison_weeks;
                    break;
                case 'POST' :
                    weeks = post_saison_weeks;
                    break;
                default :
                    break;
            }

            weeks.forEach(function (week) {
                request(replaceValuesInSring(year, spart, week), function (error, response, body) {
                    winston.info('Update schedule for week ' + week + ' in Part ' + spart + ' for year ' + year);
                    if (!error && response.statusCode == 200) {
                        parseString(body, function (err, result) {
                            if (result.ss !== '') {
                                result.ss.gms[0].g.forEach(function (game) {
                                    checkIfGameAlreadyPresent(game, result.ss.gms[0].$.w);
                                });
                            }
                        });
                    }
                    else {
                        winston.info("Server request failed. " + error.message);
                    }
                });
            })
        })
    });
}

function replaceValuesInSring(year, stype, sweek) {
    return request_string + year + '&seasonType=' + stype + '&week=' + sweek;
}

function checkIfGameAlreadyPresent(game, week){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = "SELECT * FROM games WHERE game_id = ?";
            var inserts = [game.$.eid];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, rows) {
                if (err) {
                    winston.info("error in database query checkIfGameAlreadyPresent");
                }
                else {
                    if(rows[0] !== undefined){
                        if (game.$.q !== 'P' && !rows[0].game_finished) {
                            updatePresentGame(game);
                        }
                    }
                    else{
                        insertNewGame(game, week);
                    }

                    if(week == 20){
                        updateAFCNFCWinner(game);
                    }

                    if(week == 22){
                        updateSuperBowlWinner(game);
                    }
                }
            });
        }
        connection.release();
    });
}

function updatePresentGame(game){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection updatePresentGame");
        }
        else {
            var sql = "UPDATE games SET game_finished=?, home_team_score=?, away_team_score=?, game_datetime=? WHERE game_id=?";
            var inserts = [true, game.$.hs, game.$.vs, game.$.eid, getGameDateTime(game.$.eid, game.$.t)];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, result) {
                if (err) {
                    winston.info("error in database query updatePresentGame");
                }
            });
        }
        connection.release();
    });
}

function insertNewGame(game, week){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = "INSERT INTO games (game_id, game_datetime, game_finished, home_team_score, away_team_score, week, season_type, home_team_id, away_team_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, (SELECT team_id FROM teams WHERE team_prefix=?), (SELECT team_id FROM teams WHERE team_prefix=?));";
            var inserts = [game.$.eid, getGameDateTime(game.$.eid, game.$.t), (game.$.q !== 'P'), game.$.hs, game.$.vs, week, game.$.gt, game.$.h, game.$.v];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, result) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                }
                else{
                    insertNewPrediction(game.$.eid);
                }
            });
        }
        connection.release();
    });
}

function getGameDateTime(gameId, time){
    var year = gameId.substr(0, 4);
    var month = gameId.substr(4, 2);
    var day = gameId.substr(6, 2);

    return year+"-"+month+"-"+day+" "+time+":00";
}

function insertNewPrediction(gameid){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = "INSERT INTO predictions (user_id, predicted, home_team_predicted, game_id) select user_id, 'false', 'NULL', ? from user;";
            var inserts = [gameid];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, result) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                }
            });
        }
        connection.release();
    });
}

function updateAFCNFCWinner(game) {
    if(game.$.q !== 'P'){
        var team;

        if(game.$.hs > game.$.vs){
            team = game.$.h;
        }
        else{
            team = game.$.v;
        }

        pool.getConnection(function (err, connection) {
            if (err) {
                winston.info("error in database connection");
            }
            else {
                var sql = "SELECT team_id, team_division FROM teams WHERE team_prefix = ?;";
                var inserts = [team];
                sql = mysql.format(sql, inserts);
                connection.query(sql, function (err, result) {
                    if (err) {
                        winston.info("error in database query updateAFCNFCWinner");
                        winston.info(err.message);
                    }
                    else{
                        if(result[0] !== undefined){
                            var teamId = result[0].team_id;
                            var teamDivision = result[0].team_division;
                            var inserts;

                            if(teamDivision == 'afc'){
                                inserts = ['afc_winner', teamId];
                            }
                            else{
                                inserts = ['nfc_winner', teamId];
                            }

                            sql = "UPDATE predictions_plus " +
                                "SET ?? = ? " +
                                "WHERE user_id = 3;";
                            sql = mysql.format(sql, inserts);
                            connection.query(sql, function (err) {
                                if (err) {
                                    winston.info("error in database query updateAFCNFCWinner");
                                    winston.info(err.message);
                                }
                            });
                        }
                    }
                });
            }
            connection.release();
        });
    }
}

function updateSuperBowlWinner(game) {
    if(game.$.q !== 'P'){
        var team;

        if(game.$.hs > game.$.vs){
            team = game.$.h;
        }
        else{
            team = game.$.v;
        }

        pool.getConnection(function (err, connection) {
            if (err) {
                winston.info("error in database connection");
            }
            else {
                var sql = "UPDATE predictions_plus " +
                    "JOIN teams " +
                    "ON ? = teams.team_prefix " +
                    "SET superbowl = teams.team_id " +
                    "WHERE user_id = 3;";
                var inserts = [team];
                sql = mysql.format(sql, inserts);
                connection.query(sql, function (err, result) {
                    if (err) {
                        winston.info("error in database query updateAFCNFCWinner");
                        winston.info(err.message);
                    }
                });
            }
            connection.release();
        });
    }
}

app.post('/getData', function (req, res, next) {
    calculateRanking(res, req.body.uuid);
});

function calculateRanking(res, uuid){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = "SELECT user_name, user_id FROM user WHERE user_id <> 3;";
            connection.query(sql, function (err, rows) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                }
                else{
                    if(rows !== undefined) {
                        var rankingList = [];
                        var i = -1;
                        (function calculateForEveryUser(rankingList) {
                            i++;
                            if(i >= rows.length){
                                connection.release();
                                rankingList = rankingList.sort(function (a, b) {
                                    return b.points - a.points;
                                });
                                getPredictions(rankingList, res, uuid)
                            }
                            else{
                                var user_name = rows[i].user_name;
                                var sql = "SELECT predictions.home_team_predicted as home_team_predicted, games.home_team_score as home_team_score, games.away_team_score as away_team_score, games.game_finished as finished, predictions.predicted as predicted " +
                                    "FROM predictions " +
                                    "RIGHT JOIN user " +
                                    "ON user.user_id = predictions.user_id " +
                                    "RIGHT JOIN games " +
                                    "ON predictions.game_id = games.game_id " +
                                    "WHERE predictions.user_id = ? AND games.game_finished = true AND predicted = true;";
                                var inserts = [rows[i].user_id];
                                sql = mysql.format(sql, inserts);
                                connection.query(sql, function (err, rows2) {
                                    if (err) {
                                        winston.info("error in database query insertNewGame");
                                        winston.info(err.message);
                                    }
                                    else {
                                        if (rows2 !== undefined) {
                                            var j = -1;
                                            var score = 0;
                                            (function calculateRankingForUser(score) {
                                                j++;
                                                if(j >= rows2.length){
                                                    //rankingList.push({"name": user_name, "points": score});
                                                    //calculateForEveryUser(rankingList);
                                                    (function () {
                                                                var sql = "SELECT predictions_plus.user_id as userid, superbowl_team.team_prefix as superbowl, afc_winner_team.team_prefix as afc_winner, nfc_winner_team.team_prefix as nfc_winner, best_offense_team.team_prefix as best_offense, best_defense_team.team_prefix as best_defense " +
                                                                    "FROM predictions_plus " +
                                                                    "LEFT OUTER JOIN teams as superbowl_team ON superbowl = superbowl_team.team_id " +
                                                                    "LEFT OUTER JOIN teams as afc_winner_team ON afc_winner = afc_winner_team.team_id " +
                                                                    "LEFT OUTER JOIN teams as nfc_winner_team ON nfc_winner = nfc_winner_team.team_id " +
                                                                    "LEFT OUTER JOIN teams as best_offense_team ON best_offense = best_offense_team.team_id " +
                                                                    "LEFT OUTER JOIN teams as best_defense_team ON best_defense = best_defense_team.team_id " +
                                                                    "WHERE predictions_plus.user_id = ? OR predictions_plus.user_id = 3;";
                                                                var inserts = [rows[i].user_id];
                                                                sql = mysql.format(sql, inserts);
                                                                connection.query(sql, function (err, result) {
                                                                    if (err) {
                                                                        winston.info("error in database query insertNewGame");
                                                                        winston.info(err.message);
                                                                    }
                                                                    else{
                                                                        if(result!==undefined){
                                                                            var predictionsPlus = [];
                                                                            if(result[0].userid == 3){
                                                                                defaultRow = result[0];
                                                                                userRow = result[1];
                                                                            } else {
                                                                                defaultRow = result[1];
                                                                                userRow = result[0];
                                                                            }

                                                                            if(userRow.superbowl !== null && defaultRow.superbowl !== null && userRow.superbowl === defaultRow.superbowl){
                                                                                score += 2;
                                                                            }
                                                                            if(userRow.afc_winner !== null && defaultRow.afc_winner !== null && userRow.afc_winner === defaultRow.afc_winner){
                                                                                score += 2;
                                                                            }
                                                                            if(userRow.nfc_winner !== null && defaultRow.nfc_winner !== null && userRow.nfc_winner === defaultRow.nfc_winner){
                                                                                score += 2;
                                                                            }
                                                                            if(userRow.best_offense !== null && defaultRow.best_offense !== null && userRow.best_offense === defaultRow.best_offense){
                                                                                score += 2;
                                                                            }
                                                                            if(userRow.best_defense !== null && defaultRow.best_defense !== null && userRow.best_defense === defaultRow.best_defense){
                                                                                score += 2;
                                                                            }
                                                                        }
                                                                    }
                                                                    rankingList.push({"name": user_name, "points": score});
                                                                    calculateForEveryUser(rankingList);
                                                                });
                                                    })();
                                                }
                                                else{
                                                    var home_team_score = rows2[j].home_team_score;
                                                    var away_team_score = rows2[j].away_team_score;
                                                    var home_team_predicted = rows2[j].home_team_predicted;
                                                    if ((home_team_score > away_team_score && home_team_predicted === 1) || (home_team_score < away_team_score && home_team_predicted === 0)) {
                                                        score += 1;
                                                    }
                                                    calculateRankingForUser(score);
                                                }

                                            })(score);
                                        }
                                    }
                                });
                            }
                        })(rankingList);
                    }
                    else{
                        connection.release();
                    }
                }
            });
        }
    });
}

function getPredictions(rankingList, res, uuid){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = "SELECT predictions.game_id as game_id, predictions.predicted as predicted, predictions.home_team_predicted as home_team_predicted, games.game_finished as game_finished, games.home_team_score as home_team_score, games.away_team_score as away_team_score, DATE_FORMAT(games.game_datetime, \"%Y-%m-%d %T\") as game_datetime, games.season_type as season_type, games.week as week, teams_home.team_prefix as home_team_prefix, teams_away.team_prefix as away_team_prefix " +
            "FROM predictions " +
            "RIGHT JOIN games " +
            "ON predictions.game_id = games.game_id " +
            "Right JOIN teams as teams_home " +
            "ON games.home_team_id = teams_home.team_id " +
            "RIGHT JOIN teams as teams_away " +
            "ON games.away_team_id = teams_away.team_id " +
            "WHERE predictions.user_id = ? " +
            "ORDER BY game_id;";
            var inserts = [uuid];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, rows) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                }
                else{
                    if(rows!==undefined){
                        var predictionsList = [];
                        for(var i=0; i<rows.length; i++){
                            var actualRow = rows[i];
                            var predictionListItem = getPredictionListItem(predictionsList, actualRow.week, actualRow.season_type);
                            if(predictionListItem.length === 0){
                                var tempItem = {"week": actualRow.week, "type": actualRow.season_type, "games": []};
                                tempItem.games.push({"gameid": actualRow.game_id, "gamedatetime": actualRow.game_datetime, "hometeam": actualRow.home_team_prefix, "awayteam": actualRow.away_team_prefix, "homepoints": actualRow.home_team_score, "awaypoints": actualRow.away_team_score, "isfinished": actualRow.game_finished, "haspredicted": actualRow.predicted, "predictedhometeam": actualRow.home_team_predicted});
                                predictionsList.push(tempItem);
                            }
                            else{
                                predictionListItem[0].games.push({"gameid": actualRow.game_id, "gamedatetime": actualRow.game_datetime, "hometeam": actualRow.home_team_prefix, "awayteam": actualRow.away_team_prefix, "homepoints": actualRow.home_team_score, "awaypoints": actualRow.away_team_score, "isfinished": actualRow.game_finished, "haspredicted": actualRow.predicted, "predictedhometeam": actualRow.home_team_predicted});
                            }
                        }
                        getStandings(rankingList, predictionsList, res, uuid);
                    }
                }
                connection.release();
            });
        }
    });
}

function getStandings(rankingList, predictionsList, res, uuid){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = "SELECT teams.team_prefix as team_prefix, standings.prefix as prefix, standings.games as games, standings.score as score, standings.div_games as div_games " +
                "FROM standings " +
                "RIGHT JOIN teams " +
                "ON standings.team_id = teams.team_id " +
                "ORDER BY standings.standing_id;";
            connection.query(sql, function (err, rows) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                }
                else{
                    if(rows!==undefined){
                        var standingsList = [];
                        for(var i=0; i<rows.length; i++){
                            var actualRow = rows[i];
                            var tempItem = {"teamprefix": actualRow.team_prefix, "prefix": actualRow.prefix, "games": actualRow.games, "score": actualRow.score, "divgames": actualRow.div_games};
                            standingsList.push(tempItem);
                        }
                        getPredictionPlus(rankingList, predictionsList, standingsList, res, uuid);
                    }
                    else{
                        getPredictionPlus(rankingList, predictionsList, [], res, uuid);
                    }
                }
                connection.release();
            });
        }
    });
}

function getPredictionPlus(rankingList, predictionsList, standingsList, res, uuid){
    getFirstGameDate(function (gameDate) {
        pool.getConnection(function (err, connection) {
            if (err) {
                winston.info("error in database connection");
            }
            else {
                var sql = "SELECT predictions_plus.user_id as userid, superbowl_team.team_prefix as superbowl, afc_winner_team.team_prefix as afc_winner, nfc_winner_team.team_prefix as nfc_winner, best_offense_team.team_prefix as best_offense, best_defense_team.team_prefix as best_defense " +
                            "FROM predictions_plus " +
                            "LEFT OUTER JOIN teams as superbowl_team ON superbowl = superbowl_team.team_id " +
                            "LEFT OUTER JOIN teams as afc_winner_team ON afc_winner = afc_winner_team.team_id " +
                            "LEFT OUTER JOIN teams as nfc_winner_team ON nfc_winner = nfc_winner_team.team_id " +
                            "LEFT OUTER JOIN teams as best_offense_team ON best_offense = best_offense_team.team_id " +
                            "LEFT OUTER JOIN teams as best_defense_team ON best_defense = best_defense_team.team_id " +
                            "WHERE predictions_plus.user_id = ? OR predictions_plus.user_id = 3;";
                var inserts = [uuid];
                sql = mysql.format(sql, inserts);
                connection.query(sql, function (err, rows) {
                    if (err) {
                        winston.info("error in database query insertNewGame");
                        winston.info(err.message);
                    }
                    else{
                        if(rows!==undefined){
                            var predictionsPlus = [];
                            if(rows[0].userid == 3){
                                defaultRow = rows[0];
                                userRow = rows[1];
                            } else {
                                defaultRow = rows[1];
                                userRow = rows[0];
                            }

                            predictionsPlus.push({"user": "default", "superbowl": defaultRow.superbowl === null ? "" : defaultRow.superbowl, "afcwinnerteam": defaultRow.afc_winner === null ? "" : defaultRow.afc_winner, "nfcwinnerteam": defaultRow.nfc_winner === null ? "" : defaultRow.nfc_winner, "bestoffenseteam": defaultRow.best_offense === null ? "" : defaultRow.best_offense, "bestdefenseteam": defaultRow.best_defense === null ? "" : defaultRow.best_defense, "firstgamedate": gameDate});
                            predictionsPlus.push({"user": "user", "superbowl": userRow.superbowl === null ? "" : userRow.superbowl, "afcwinnerteam": userRow.afc_winner === null ? "" : userRow.afc_winner, "nfcwinnerteam": userRow.nfc_winner === null ? "" : userRow.nfc_winner, "bestoffenseteam": userRow.best_offense === null ? "" : userRow.best_offense, "bestdefenseteam": userRow.best_defense === null ? "" : userRow.best_defense, "firstgamedate": gameDate});

                            sendDataResponse(rankingList, predictionsList, standingsList, predictionsPlus, res);
                        }
                    }
                    connection.release();
                });
            }
        });
    })
}

function getFirstGameDate(callback) {
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = "SELECT DATE_FORMAT(game_datetime, \"%Y-%m-%d %T\") as game_datetime from games where week = 1 AND season_type = \"REG\" ORDER BY game_datetime";
            connection.query(sql, function (err, rows) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                }
                else{
                    if(rows!==undefined){
                        var gameDate = rows[0].game_datetime;
                        callback(gameDate);
                    }
                    else {
                        callback("");
                    }
                }
                connection.release();
            });
        }
    });
}

function sendDataResponse(rankingList, predictionsList, standingsList, predictionsPlus, res){
    var data = {"ranking" : rankingList,
        "predictions": predictionsList,
        "predictionsplus": predictionsPlus,
        "standings": standingsList};

    var resp = {
        "result": "success",
        "message": "data successfull",
        "data" : data
    };

    sendResponse(res, resp, null);
}

function sendResponse(res, resp, connection) {
    res.end(JSON.stringify(resp));
    if(connection!==null) {
        connection.release();
    }
}

function getPredictionListItem(predictionsList, week, stype){
    var tempList = [];
        for (var i = 0; i < predictionsList.length; i++) {
            if (predictionsList[i].week === week && predictionsList[i].type === stype) {
                tempList.push(predictionsList[i]);
                break;
            }
        }
    return tempList;
}

app.post('/updatePrediction', function (req, res, next) {
    var resp = {
        "result": "",
        "message": ""
    };
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
            resp.result = "failed";
            resp.message = err.message;
            sendResponse(res, resp, connection);
        }
        else {
            var sql;
            var inserts;
            if(req.body.hasPredicted){
                sql = "UPDATE predictions SET predicted=1, home_team_predicted=? where game_id=? and user_id=?;";
                inserts = [(req.body.hasHomeTeamPredicted ? 1 : 0), req.body.gameid, req.body.uuid];
            }
            else{
                sql = "UPDATE predictions SET predicted=0, home_team_predicted=0 where game_id=? and user_id=?;";
                inserts = [req.body.gameid, req.body.uuid];
            }
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, result) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                    resp.result = "failed";
                    resp.message = err.message;
                    sendResponse(res, resp, connection);
                }
                else{
                    resp.result = "success";
                    resp.message = "prediction updated";
                    sendResponse(res, resp, connection);
                }
            });
        }
    });
});

app.post('/getAllPredictionsForGame', function (req, res, next) {
    var resp = {
        "result": "",
        "message": "",
        "predictionlist": ""
    };
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
            resp.result = "failed";
            resp.message = err.message;
            sendResponse(res, resp, connection);
        }
        else {
                var sql = "SELECT predictions.predicted as predicted, predictions.home_team_predicted as home_team_predicted, predictions.user_id as user_id, user.user_name as user_name " +
                    "FROM predictions " +
                    "RIGHT JOIN user ON predictions.user_id = user.user_id " +
                    "WHERE predictions.game_id = ? " +
                    "ORDER BY user.user_name ASC";
                var inserts = [req.body.gameid];

                sql = mysql.format(sql, inserts);
                connection.query(sql, function (err, rows) {
                    if (err) {
                        winston.info("error in database query insertNewGame");
                        winston.info(err.message);
                        resp.result = "failed";
                        resp.message = err.message;
                        sendResponse(res, resp, connection);
                    }
                    else{
                        if(rows!==undefined){
                            var predictionsList = [];
                            for(var i=0; i<rows.length; i++){
                                var actualRow = rows[i];
                                var tempObject = {"predicted": actualRow.predicted, "hometeampredicted": actualRow.home_team_predicted, "userid": actualRow.user_id, "username": actualRow.user_name};
                                predictionsList.push(tempObject);
                            }

                            resp.result = "success";
                            resp.predictionlist = predictionsList;
                            sendResponse(res, resp, connection);
                        }
                    }
                });
        }
    });
});

function updateStandings(){
    var standings = [];
    var teamStanding;

    requestWebsite('http://www.nfl.com/standings', function (error, response, html) {
        if(!error && response.statusCode == 200){
            var $ = cheerio.load(html);
            $('tr.tbdy1').each(function () {

                var prefix = "", teamname = "", games = "", score = "", div_games = "";

                var tableColumns = $(this).find('td');
                tableColumns.each(function (i, element) {
                    if(i < 5 || i == 11){
                        switch(i){
                            case 0: {
                                if($(this).text().trim().indexOf('-') != -1){
                                    prefix = $(this).text().trim().charAt(0);
                                }
                                teamname = $(this).find('a').text().trim();
                                break;
                            }
                            case 1: {
                                games += $(this).text().trim();
                                break;
                            }
                            case 2: {
                                games += "-" + $(this).text().trim();
                                break;
                            }
                            case 3: {
                                if(parseInt($(this).text().trim())>0){
                                    games += "-" + $(this).text().trim();
                                }
                                break;
                            }
                            case 4: {
                                score = $(this).text().trim();
                                break;
                            }
                            case 11: {
                                div_games = $(this).text().trim();
                                break;
                            }
                            default: break;
                        }
                    }
                });

                teamStanding = {"prefix" : prefix, "teamname": teamname, "games": games, "score": score, "div_games": div_games};
                standings.push(teamStanding);
            });

            insertIntoStandingsTable(standings);
        }
    });
}

function insertIntoStandingsTable(standings) {
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = 'DELETE FROM standings';
            connection.query(sql, function (err, result) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                }
                else {
                    var i = -1;
                    (function insertNewStanding() {
                        i++;
                        if (i < standings.length) {
                            var standing = standings[i];
                            var sql = "INSERT INTO standings (standing_id, team_id, prefix, games, score, div_games) VALUES (?, (SELECT team_id FROM teams WHERE team_name=?), ?, ?, ?, ?);";
                            var inserts = [i + 1, standing.teamname, (standing.prefix == '' ? null : standing.prefix), standing.games, standing.score, standing.div_games];
                            sql = mysql.format(sql, inserts);
                            connection.query(sql, function (err, result) {
                                if (err) {
                                    winston.info("error in database query insertNewGame");
                                    winston.info(err.message);
                                }
                                else {
                                    insertNewStanding();
                                }
                            });
                        }
                        else {
                            connection.release();
                        }
                    })();
                }
            });
        }
    });
}

function updatePredictionsPlus(){
    requestWebsite('http://www.nfl.com/stats/team?seasonId='+saison_years+'&seasonType='+saison_parts[0], function (error, response, html) {
        if(!error && response.statusCode == 200){
            var $ = cheerio.load(html);

            var bestOffenseRow = $('#r1c1_1');
            var bestOffenseTeamName = bestOffenseRow.find('a').text().trim();

            var bestDefenseRow = $('#r1c2_1');
            var bestDefenseTeamName = bestDefenseRow.find('a').text().trim();

            updatePredictionsPlusInDatabase(bestOffenseTeamName, bestDefenseTeamName);
        }
    });
}

app.post('/getAllPredictionsPlusForState', function (req, res, next) {
    var resp = {
        "result": "",
        "message": "",
        "predictionlist": ""
    };
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
            resp.result = "failed";
            resp.message = err.message;
            sendResponse(res, resp, connection);
        }
        else {
            var sql = "SELECT user.user_name as username, teams.team_prefix as teamprefix " +
                "FROM predictions_plus " +
                "JOIN user ON predictions_plus.user_id = user.user_id " +
                "LEFT JOIN teams ON predictions_plus.?? = teams.team_id " +
                "WHERE predictions_plus.user_id <> 3 " +
                "ORDER BY user.user_name ASC";
            var inserts = [req.body.state];

            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, rows) {
                if (err) {
                    winston.info("error in database query getAllPredictionsPlusForState");
                    winston.info(err.message);
                    resp.result = "failed";
                    resp.message = err.message;
                    sendResponse(res, resp, connection);
                }
                else{
                    if(rows!==undefined){
                        var predictionsList = [];
                        for(var i=0; i<rows.length; i++){
                            var actualRow = rows[i];
                            var tempObject = {"username": actualRow.username, "teamprefix": actualRow.teamprefix == null ? "" : actualRow.teamprefix};
                            predictionsList.push(tempObject);
                        }

                        resp.result = "success";
                        resp.predictionlist = predictionsList;
                        sendResponse(res, resp, connection);
                    }
                }
            });
        }
    });
});

function updatePredictionsPlusInDatabase(bestOffenseTeamName, bestDefenseTeamName){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = sql = "UPDATE predictions_plus " +
                "JOIN teams AS teams_offense " +
                "ON ? = teams_offense.team_name " +
                "JOIN teams AS teams_defense " +
                "ON ? = teams_defense.team_name " +
                "SET ?? = teams_offense.team_id, ?? =  teams_defense.team_id " +
                "WHERE user_id = 3;";
            inserts = [bestOffenseTeamName, bestDefenseTeamName, 'best_offense', 'best_defense'];
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, result) {
                if (err) {
                    winston.info("error in database query updatePredictionsPlusInDatabase");
                    winston.info(err.message);
                }
                else{
                    winston.info('Best offense and defence were updated');
                }
            });
        }
    });
}

app.post('/updatePredictionPlus', function (req, res) {
    var resp = {
        "result": "",
        "message": ""
    };
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
            resp.result = "failed";
            resp.message = err.message;
            sendResponse(res, resp, connection);
        }
        else {
            var sql;
            var inserts;
            var teamPrefix = req.body.teamprefix;

            if(teamPrefix!=="") {
                sql = "UPDATE predictions_plus " +
                    "JOIN teams " +
                    "ON ? = teams.team_prefix " +
                    "SET ?? = teams.team_id " +
                    "WHERE user_id = ?;";
                inserts = [teamPrefix, req.body.state, req.body.uuid];
            }
            else {
                sql = "UPDATE predictions_plus " +
                    "SET ?? = NULL " +
                    "WHERE user_id = ?;";
                inserts = [req.body.state, req.body.uuid];
            }
            sql = mysql.format(sql, inserts);
            connection.query(sql, function (err, result) {
                if (err) {
                    winston.info("error in database query insertNewGame");
                    winston.info(err.message);
                    resp.result = "failed";
                    resp.message = err.message;
                    sendResponse(res, resp, connection);
                }
                else{
                    resp.result = "success";
                    resp.message = "predictionplus updated";
                    sendResponse(res, resp, connection);
                }
            });
        }
    });
});

// catch 404 and forward to error handler
app.use(function (req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handler
app.use(function (err, req, res, next) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.render('error');
});

module.exports = app;
