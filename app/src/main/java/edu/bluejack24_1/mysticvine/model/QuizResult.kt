package edu.bluejack24_1.mysticvine.model

data class QuizResult(
    val resultId : String = "",
    val quizId: String = "",
    val userId: String = "",
    val score: Int = 0,
    val correct : Int = 0,
    val wrong : Int = 0,
    val coin : Int = 0,
    val extraExp : Int = 0,
    val extraCoin : Int = 0,
)
