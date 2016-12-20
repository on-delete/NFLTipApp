var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var mysql = require('mysql');

var index = require('./routes/index');

var app = express();

var pool = mysql.createPool({
  host: 'localhost',
  user: 'andre',
  password : 'Kartoffelecke',
  database : 'andre'
});

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', index);

app.post('/nameExisting', function(req, res, next) {
  var resp = {
    "result": "",
    "message": ""
  };

  pool.getConnection(function (err, connection) {
    if(err){
      resp.result = "failed";
      resp.message = err.message;
      sendResponse(res, resp, connection);
    }
    else {
      var sql = "SELECT user_name FROM user WHERE user_name = ?";
      var inserts = [req.body.name];
      sql = mysql.format(sql, inserts);
      connection.query(sql, function (err, rows) {
        if(err){
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

app.post('/registerUser', function(req, res, next) {
  var resp = {
    "result": "",
    "message": ""
  };

  pool.getConnection(function (err, connection) {
    if(err){
      resp.result = "failed";
      resp.message = err.message;
      sendResponse(res, resp, connection);
    }
    else {
      var sql = "INSERT INTO user (user_name, user_email, user_password) VALUES (?, ?, ?)";
      var inserts = [req.body.user.name, req.body.user.email, req.body.user.password];
      sql = mysql.format(sql, inserts);
      connection.query(sql, function (err, result) {
        if(err){
          resp.result = "failed";
          resp.message = err.message;
          sendResponse(res, resp, connection);
        }
        else {
          resp.result = "success";
          resp.message = "user registered";
          sendResponse(res, resp, connection);
        }
      });
    }
  });
});

app.post('/registerLogin', function(req, res, next) {
  var resp = {
    "result": "",
    "message": "",
    "user": {
      "name": req.body.user.name,
      "uuid": ""
    }
  };

  pool.getConnection(function (err, connection) {
    if(err){
      resp.result = "failed";
      resp.message = err.message;
      sendResponse(res, resp, connection);
    }
    else {
      var sql = "SELECT user_password, user_id FROM user WHERE user_name = ?";
      var inserts = [req.body.user.name];
      sql = mysql.format(sql, inserts);
      connection.query(sql, function (err, rows) {
        if(err){
          resp.result = "failed";
          resp.message = err.message;
          sendResponse(res, resp, connection);
        }
        else {
          var password = rows[0].user_password;
          if(password===req.body.user.password){
            resp.result = "success";
            resp.message = "login successfull";
            resp.user.uuid = rows[0].user_id;

          } else{
            resp.result = "success";
            resp.message = "login failed";
          }
          sendResponse(res, resp, connection);
        }
      });
    }
  });
});

function sendResponse(res, resp, connection) {
  console.log("ergebnis: " + resp.result + ", " + resp.message);
  res.end(JSON.stringify(resp));
  connection.release();
}

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
