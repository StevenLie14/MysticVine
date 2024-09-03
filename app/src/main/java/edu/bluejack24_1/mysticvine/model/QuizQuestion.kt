package edu.bluejack24_1.mysticvine.model

data class QuizQuestion(
    val quizId : String,
    val questionId : String,
    val question : String,
    val answer : String,
    val choices : List<String>
)
