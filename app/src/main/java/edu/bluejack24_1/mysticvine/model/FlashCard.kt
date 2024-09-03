package edu.bluejack24_1.mysticvine.model

import android.os.Parcelable
import com.google.firebase.database.ServerValue
import kotlinx.parcelize.Parcelize
import java.sql.Time
import java.time.LocalDate

data class FlashCard(
    val id: String = "",
    val question: String = "",
    val answer: String = "",
    var appearsDate: String = LocalDate.now().toString(),
    val userId: String = "",
    var status: String = "Incomplete"
)
