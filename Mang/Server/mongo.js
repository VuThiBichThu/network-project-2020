const mongo = require("mongoose");
const url = 'mongodb://localhost:27017/multiplechoiceexam';

exports.connect = async () =>{
    try {
        await mongo.connect(url,{ useNewUrlParser: true, useUnifiedTopology: true });
        console.log("connect ok");
    } catch (error) {
        console.log(error);
    }
    return mongo;
}