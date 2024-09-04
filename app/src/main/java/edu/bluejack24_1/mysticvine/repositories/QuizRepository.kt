package edu.bluejack24_1.mysticvine.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    fun getQuizzes(quizList: MutableLiveData<List<Quizzes>>) {
        val quizRef = db.getReference("quizzes").child(auth.currentUser!!.uid)

        quizRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val quizDB = snapshot.children.mapNotNull { it.getValue(Quizzes::class.java) }
                    quizList.postValue(quizDB)
                } catch (e: Exception) {
                    Log.e("QuizRepository", "Error parsing quiz data", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("QuizRepository", "Database error: ${error.message}")
            }
        })
    }
}
