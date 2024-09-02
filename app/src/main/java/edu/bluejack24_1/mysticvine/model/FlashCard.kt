package edu.bluejack24_1.mysticvine.model

import com.google.firebase.database.ServerValue
import java.sql.Time

data class FlashCard(
    val id: String = "",
    val question: String = "",
    val answer: String = "",
    val appearsTime: Long = System.currentTimeMillis(),
    val userId: String = "",
)
