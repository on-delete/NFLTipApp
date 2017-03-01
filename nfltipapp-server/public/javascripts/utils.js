var mysql = require('mysql');

var exports = module.exports;

exports.pool = mysql.createPool({
    host: 'localhost',
    user: 'andredb',
    password: 'database123',
    database: 'nfltipappdb'
});

exports.sendResponse = function sendResponse(res, resp, connection, statusCode) {
    res.status(statusCode).end(JSON.stringify(resp));
    if (connection !== null) {
        connection.destroy();
    }
};

exports.sendError = function (resp, errMsg, res, connection) {
    resp.result = "failed";
    resp.message = errMsg;
    sendResponse(res, resp, connection, 500);
};
