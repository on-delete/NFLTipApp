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
    user: 'andre',
    password: 'Kartoffelecke',
    database: 'andre'
});

var pre_saison_weeks = [1, 2, 3, 4, 5];
var reg_saison_weeks = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17];
var post_saison_weeks = [1, 2, 3, 4];
var saison_parts = ['PRE', 'REG', 'POST'];
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
                    resp.result = "success"
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
                case 'PRE' :
                    weeks = pre_saison_weeks;
                    break;
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
            var sql = "UPDATE games SET game_finished=?, home_team_score=?, away_team_score=? WHERE game_id=?";
            var inserts = [true, game.$.hs, game.$.vs, game.$.eid];
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
            var sql = "INSERT INTO games (game_id, game_finished, home_team_score, away_team_score, week, season_type, home_team_id, away_team_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, (SELECT team_id FROM teams WHERE team_prefix=?), (SELECT team_id FROM teams WHERE team_prefix=?));";
            var inserts = [game.$.eid, (game.$.q !== 'P'), game.$.hs, game.$.vs, week, game.$.gt, game.$.h, game.$.v];
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

app.post('/getData', function (req, res, next) {
    //calculateRanking(res, req.body.user.uuid);
    calculateRanking(res, "10");
});

function calculateRanking(res, uuid){
    pool.getConnection(function (err, connection) {
        if (err) {
            winston.info("error in database connection");
        }
        else {
            var sql = "SELECT user_name, user_id FROM user;";
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
                                                    rankingList.push({"name": user_name, "points": score});
                                                    calculateForEveryUser(rankingList);
                                                }
                                                else{
                                                    var home_team_score = rows2[j].home_team_score;
                                                    var away_team_score = rows2[j].away_team_score;
                                                    var home_team_predicted = rows2[j].home_team_predicted;
                                                    if ((home_team_score > away_team_score && home_team_predicted === 1) || (home_team_score < away_team_score && home_team_predicted === 0)) {
                                                        score += 3;
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
                        getPredictions([]);
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
            var sql = "SELECT predictions.game_id as game_id, predictions.predicted as predicted, predictions.home_team_predicted as home_team_predicted, games.game_finished as game_finished, games.home_team_score as home_team_score, games.away_team_score as away_team_score, games.season_type as season_type, games.week as week, teams_home.team_prefix as home_team_prefix, teams_away.team_prefix as away_team_prefix " +
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
                                tempItem.games.push({"gameid": actualRow.game_id, "hometeam": actualRow.home_team_prefix, "awayteam": actualRow.away_team_prefix, "homepoints": actualRow.home_team_score, "awaypoints": actualRow.away_team_score, "isfinished": actualRow.game_finished, "haspredicted": actualRow.predicted, "predictedhometeam": actualRow.home_team_predicted});
                                predictionsList.push(tempItem);
                            }
                            else{
                                predictionListItem[0].games.push({"gameid": actualRow.game_id, "hometeam": actualRow.home_team_prefix, "awayteam": actualRow.away_team_prefix, "homepoints": actualRow.home_team_score, "awaypoints": actualRow.away_team_score, "isfinished": actualRow.game_finished, "haspredicted": actualRow.predicted, "predictedhometeam": actualRow.home_team_predicted});
                            }
                        }
                        sendDataResponse(rankingList, predictionsList, res);
                    }
                }
                connection.release();
            });
        }
    });
}

function sendDataResponse(rankingList, predictionsList, res){
    var data = {"ranking" : rankingList,
        "predictions": predictionsList};

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
