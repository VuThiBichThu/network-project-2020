const mongo = require("mongoose");

const Schema = mongo.Schema; 
const QuestionSchema = new Schema(
    {
        question_set: [
            {
                "question": {
                    type: String,
                    require: true
                },
                "answers": [
                    {
                        "id": {
                            type: Number,
                            require: true
                        },
                        "text": {
                            type: String,
                            require: true
                        }
                    }
                ],
                "correct": {
                    type: Number,
                    require: true
                }
            }
        ],
        
        id: {
            type: String,
            require: true
        },
        name: {
            type: String,
            require: true
        }

    }
);

module.exports = Question = mongo.model("questions", QuestionSchema);

