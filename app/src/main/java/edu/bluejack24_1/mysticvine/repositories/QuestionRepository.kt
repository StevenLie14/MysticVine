package edu.bluejack24_1.mysticvine.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack24_1.mysticvine.model.Questions

class QuestionRepository {

    private val db = FirebaseDatabase.getInstance()

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

    fun getQuestions(questionList: MutableLiveData<List<Questions>>, quizId: String) {
        val questionRef = db.getReference("questions").child(quizId)

        questionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val questionsDB = snapshot.children.mapNotNull { it.getValue(Questions::class.java) }
                    questionList.postValue(questionsDB)
                } catch (e: Exception) {
                    Log.e("QuestionRepository", "Error parsing question data", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("QuestionRepository", "Database error: ${error.message}")
            }
        })
    }
}
