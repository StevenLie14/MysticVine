package edu.bluejack24_1.mysticvine.repositories

import androidx.appcompat.view.ActionMode.Callback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack24_1.mysticvine.model.Quizzes

class QuizRepository {

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    fun createQuiz(quiz: Quizzes, callback: (String) -> Unit) {
        val quizRef = db.getReference("quizzes").child(auth.currentUser!!.uid).child(quiz.id)
        quizRef.setValue(quiz).addOnCompleteListener {
            if (it.isSuccessful) {
                callback("Quiz created")
            } else {
                callback(it.exception?.message ?: "Failed to create quiz")
            }
        }
    }


}