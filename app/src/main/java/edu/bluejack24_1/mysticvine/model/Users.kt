package edu.bluejack24_1.mysticvine.model

data class Users(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val profilePicture: String = "",
    val level: Int = 1,
    val exp: Int = 0,
    val coin: Int = 0,
    val score: Int = 0,
    val coinBooster: Int = 0,
    val expBooster:Int = 0,
    val shieldBooster:Int = 0,
)

