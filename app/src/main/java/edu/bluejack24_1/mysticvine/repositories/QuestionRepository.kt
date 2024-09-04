package edu.bluejack24_1.mysticvine.repositories

import androidx.appcompat.view.ActionMode.Callback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack24_1.mysticvine.model.Questions

class QuestionRepository {

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createQuestion(question: Questions, callback: (String) -> Unit) {
        val questionRef = db.getReference("questions").child(question.quizId).child(question.questionId)
        questionRef.setValue(question).addOnCompleteListener {
            if (it.isSuccessful) {
                callback("Question created")
            } else {
                callback(it.exception?.message ?: "Failed to create question")
            }
        }
    }


}