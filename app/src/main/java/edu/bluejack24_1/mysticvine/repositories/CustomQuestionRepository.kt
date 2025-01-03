package edu.bluejack24_1.mysticvine.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack24_1.mysticvine.model.CustomQuizQuestion

class CustomQuestionRepository {

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createCustomQuestion(partyQuestionId : String, question: CustomQuizQuestion, callback: (String) -> Unit) {
        val questionRef = db.getReference("custom questions").child(question.partyCode).child(partyQuestionId).child(question.userId)
        questionRef.setValue(question).addOnCompleteListener {
            if (it.isSuccessful) {
                callback("Question created")
            } else {
                callback("Failed to create question")
            }
        }
    }

    fun getCustomQuestions(partyCode: String,partyQuestionId : String , customQuestionList: MutableLiveData<List<CustomQuizQuestion>>) {
        val questionRef = db.getReference("custom questions").child(partyCode).child(partyQuestionId)

        questionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val customQuestionDB: List<CustomQuizQuestion> = snapshot.children.map { it.getValue(CustomQuizQuestion::class.java)!! }
                    Log.d("CustomQuestion Repository", customQuestionDB.toString())
                    customQuestionList.postValue(customQuestionDB)
                } catch (e: Exception) {
                    Log.e("CustomQuestion Repository", "Error parsing custom question data")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CustomQuestion Repository", error.message)
            }

        })
    }
    fun getNotRTCustomQuestions (partyCode: String, callback: (List<CustomQuizQuestion>) -> Unit) {
        val questionRef = db.getReference("custom questions").child(partyCode)
        questionRef.get().addOnSuccessListener {
            if (it.exists()) {
                val customQuestionDB: List<CustomQuizQuestion> = it.children.map { it.getValue(CustomQuizQuestion::class.java)!! }
                callback(customQuestionDB)
            } else {
                callback(emptyList())
            }
        }
    }

    fun deleteQuestions (partyCode: String, callback: (String) -> Unit) {
        val questionRef = db.getReference("custom questions").child(partyCode)
        questionRef.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback("Questions deleted")
            } else {
                callback(it.exception?.message ?: "Failed to delete questions")
            }
        }
    }


}