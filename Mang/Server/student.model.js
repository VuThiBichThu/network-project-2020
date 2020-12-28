const mongo = require("mongoose");

const Schema = mongo.Schema;
const StudentSchema = new Schema({
idStudent: {
    type: String,
    require: true
},
pass: {
    type: String,
    require: true
},
name: {
    type: String,
    require: true
},
grade: {
    type: String,
    require: true
},
score: {
    type: Number,
    require: true
}
});

module.exports = Student = mongo.model("student", StudentSchema);