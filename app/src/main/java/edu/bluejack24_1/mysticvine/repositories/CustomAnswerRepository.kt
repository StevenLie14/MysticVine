package edu.bluejack24_1.mysticvine.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack24_1.mysticvine.model.CustomQuizAnswer

class CustomAnswerRepository {

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createCustomAnswer(answer: CustomQuizAnswer, callback: (String) -> Unit) {
        val answerRef = db.getReference("custom answers").child(answer.questionId).child(answer.userId)
        answerRef.setValue(answer).addOnCompleteListener {
            if (it.isSuccessful) {
                callback("Answer created")
            } else {
                callback(it.exception?.message ?: "Failed to create answer")
            }
        }
    }
}