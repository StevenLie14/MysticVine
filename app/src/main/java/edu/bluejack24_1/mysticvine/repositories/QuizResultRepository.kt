package edu.bluejack24_1.mysticvine.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack24_1.mysticvine.model.QuizResult

class QuizResultRepository {

    private val db = FirebaseDatabase.getInstance()

    fun createQuizResult(quizResult: QuizResult, callback: (Int, String) -> Unit) {
        val quizResultRef = db.getReference("quiz results").child(quizResult.quizId).child(quizResult.userId).child(quizResult.resultId)
        quizResultRef.setValue(quizResult).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(200, quizResult.resultId)
            } else {
                callback(400, it.exception?.message ?: "Failed to create quiz result")
            }
        }
    }

    fun updateQuizResult(quizResult: QuizResult, callback: (Int, String) -> Unit) {
        val quizResultRef = db.getReference("quiz results").child(quizResult.quizId).child(quizResult.userId).child(quizResult.resultId)
        quizResultRef.setValue(quizResult).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(200,"Quiz result updated")
            } else {
                callback(400,it.exception?.message ?: "Failed to update quiz result")
            }
        }
    }

    fun getQuizResult(quizId: String, userId: String, resultId: String, result: MutableLiveData<QuizResult>) {
        val quizResultRef = db.getReference("quiz results").child(quizId).child(userId).child(resultId)
        quizResultRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val quizResultDB = snapshot.getValue(QuizResult::class.java)
                    result.postValue(quizResultDB)
                } catch (e: Exception) {
                    Log.e("QuizResultRepository", "Error parsing quiz result data", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("QuizResultRepository", "Database error: ${error.message}")
            }
        })
    }
}