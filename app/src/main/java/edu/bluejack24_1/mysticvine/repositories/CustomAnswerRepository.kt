package edu.bluejack24_1.mysticvine.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack24_1.mysticvine.model.CustomQuizAnswer
import edu.bluejack24_1.mysticvine.model.CustomQuizQuestion

class CustomAnswerRepository {

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createCustomAnswer(
        partyCode: String,
        partyQuestionId: String,
        question: CustomQuizQuestion,
        answer: CustomQuizAnswer,
        callback: (String) -> Unit
    ) {
        val answerRef = db.getReference("custom questions")
            .child(partyCode)
            .child(partyQuestionId)
            .child(question.userId)
            .child("answers")
            .child(answer.userId)

        answerRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                callback("Answer already exists")
            } else {
                answerRef.setValue(answer).addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback("Answer created")
                    } else {
                        callback(it.exception?.message ?: "Failed to create answer")
                    }
                }
            }
        }.addOnFailureListener {
            callback(it.message ?: "Error checking answer existence")
        }
    }


    fun getCustomAnswers(partyCode: String,partyQuestionId : String, question : CustomQuizQuestion, customAnswerList: MutableLiveData<List<CustomQuizAnswer>>) {
        val answerRef = db.getReference("custom questions").child(partyCode).child(partyQuestionId).child(question.userId).child("answers")

        answerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val customAnswerDB: List<CustomQuizAnswer> = snapshot.children.map { it.getValue(CustomQuizAnswer::class.java)!! }
                    customAnswerList.postValue(customAnswerDB)
                } catch (e: Exception) {
                    Log.e("CustomAnswer Repository", "Error parsing custom answer data")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("CustomAnswer Repository", error.message)
            }
        })
    }

    fun getCustomAnswersFromQuestions(partyCode: String,partyQuestionId : String, questions : List<CustomQuizQuestion>, customAnswerMap: MutableLiveData<Map<CustomQuizQuestion, List<CustomQuizAnswer>>>) {
        val answersByQuestion = mutableMapOf<CustomQuizQuestion, List<CustomQuizAnswer>>()

        for (question in questions) {
            val answerRef = db.getReference("custom questions")
                .child(partyCode)
                .child(partyQuestionId)
                .child(question.userId)
                .child("answers")

            answerRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val customAnswerDB: List<CustomQuizAnswer> = snapshot.children.mapNotNull { it.getValue(CustomQuizAnswer::class.java) }

                        answersByQuestion[question] = customAnswerDB

                        if (answersByQuestion.size == questions.size) {
                            customAnswerMap.postValue(answersByQuestion)
                        }
                    } catch (e: Exception) {
                        Log.e("CustomAnswer Repository", "Error parsing custom answer data", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CustomAnswer Repository", error.message)
                }
            })
        }
    }

    fun getNonRTCustomAnswers (partyCode: String, question : CustomQuizQuestion, callback: (List<CustomQuizAnswer>) -> Unit) {
        val answerRef = db.getReference("custom questions").child(partyCode).child(question.userId).child("answers")
        answerRef.get().addOnSuccessListener {
            if (it.exists()) {
                try {
                    val customAnswerDB: List<CustomQuizAnswer> = it.children.map { it.getValue(CustomQuizAnswer::class.java)!! }
                    Log.d("CustomAnswer Repositorys", customAnswerDB.toString())
                    callback(customAnswerDB)
                } catch (e: Exception) {
                    Log.e("CustomAnswer Repository", "Error parsing custom answer data")
                }
            } else {
                callback(emptyList())
            }
        }
    }

}