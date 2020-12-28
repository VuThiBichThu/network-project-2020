const express = require("express");
const app = express();

const Student = require("./student.model");
const Question = require("./question.model");

var server = require("http").createServer(app);
var io = require("socket.io").listen(server);
var port = process.env.PORT || 7777;

const db = require("./mongo");
const {collection_question}=require("./question.model");
const { collection_student } = require("./student.model");
const { stringify } = require("querystring");

db.connect();
console.log("connected");

server.listen( process.env.PORT || 7777, () => { console.log("app run port " + port); });



io.sockets.on('connection', function (socket) {
    console.log('Co thiet bi ket noi');
    socket.on('send-data', function (data) {
        console.log("Server nhan : " + data);
    });

    socket.on('accLogin', async (data) => {
        console.log("Server nhan : " + data);
        var isValid = false;

        const std = await Student.findOne({ idStudent: data.id }, function (err, student) {
            if (err) {
                console.log(err);
            } else if (student) {

                if (student.pass == data.pass) {
                    console.log('Found: ', student);
                    isValid = true;
                }
            } else {
                console.log('Student not found');
            }
            io.sockets.emit('check account', { status: isValid, student: student });

        });

    });
    socket.on('registerAccount', async (data) => {
        console.log("id Tai khoan dang ki: " + data);
        var isExistedAcc = false;
        const std = await Student.findOne({ idStudent: data.idStudent }, function (err, student) {
            if (err) {
                console.log(err);
            } else if (student) {
                isExistedAcc = true;
            } else {
                console.log("Not found");
            }
        });
        io.sockets.emit('check existed account', { isExisted: isExistedAcc });
    });
    socket.on('accountReg', async (data) => {
        console.log("Insert ");
        console.log(data);
        collection_student.insertOne(data, function (err, student) {
            if (err) {
                console.log(err);
            } else {
                console.log("successful");
            }
        });
    });

    socket.on("get questions", async () => {
        const arr = await Question.find({}, function (err, docs) {
            if (err) {
                console.log("Error");
            } else if (docs) {
                console.log(docs);
                console.log("xxxxxx");
                socket.emit("get questions", docs);
            } else {
                console.log("Not found");
            }
        });
    });

    socket.on('send list question', async (data) => {
        const std = await Question.findOne({ id_list_ques: data.id }, function (err, questionset) {
            if (err) {
                console.log(err);
            } else if (questionset) {
                socket.emit("get list question", questionset);
            } else {
                console.log("Not found");
            }
        }); 
    });

});