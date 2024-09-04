package edu.bluejack24_1.mysticvine.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack24_1.mysticvine.model.FlashCard
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.utils.Utils
import java.time.LocalDate

class FlashCardRepository {

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createFlashCard(flashCard: FlashCard, callback: (String) -> Unit) {
        val flashRef = db.getReference("flashcards").child(auth.currentUser!!.uid).child(flashCard.id)
        flashRef.setValue(flashCard).addOnCompleteListener {
            if (it.isSuccessful) {
                callback("Flashcard created")
            } else {
                callback(it.exception?.message ?: "Failed to create flashcard")
            }
        }
    }


    fun getFlashCards(flashCardList : MutableLiveData<List<FlashCard>>) {
        val flashRef = db.getReference("flashcards").child(auth.currentUser!!.uid)

        flashRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val flashCardDB : List<FlashCard> = snapshot.children.map { it.getValue(FlashCard::class.java)!! }
                    flashCardList.postValue(flashCardDB)
                }catch (e: Exception){
                    Log.e("FlashCard Repository", "Error parsing flashcard data")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FlashCard Repository", error.message)
            }

        })
    }

    fun getDailyFlashcards(flashCardList : MutableLiveData<List<FlashCard>>) {
        val flashRef = db.getReference("flashcards").child(auth.currentUser!!.uid).orderByChild("appearsDate")

        flashRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val flashCardDB : List<FlashCard> = snapshot.children.map { it.getValue(FlashCard::class.java)!! }
                    flashCardList.postValue(flashCardDB.filter { Utils.getLocalDate(it.appearsDate).isEqual(LocalDate.now()) || Utils.getLocalDate(it.appearsDate).isBefore(LocalDate.now()) })
                }catch (e: Exception){
                    Log.e("FlashCard Repository", "Error parsing flashcard data")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FlashCard Repository", error.message)
            }
        })
    }

    fun updateDaily () {
        val flashRef = db.getReference("flashcards").child(auth.currentUser!!.uid)
        flashRef.get().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val flashCardDB : List<FlashCard> = it.result!!.children.map { it.getValue(FlashCard::class.java)!! }
                flashCardDB.forEach { card ->
                    if (card.status == "Remember") {
                        card.appearsDate = LocalDate.now().plusDays(3).toString()
                        card.status = "Incomplete"
                        flashRef.child(card.id).setValue(card)
                    }
                    if (card.status == "Forgot") {
                        card.appearsDate = LocalDate.now().plusDays(1).toString()
                        card.status = "Incomplete"
                        flashRef.child(card.id).setValue(card)
                    }
                }
            }
        }
    }

    fun updateRememberFlashCard(flashCard: FlashCard, remember : Boolean, callback: (String) -> Unit) {
        val flashCard = flashCard.copy(status = if (remember) "Remember" else "Forgot")
        val flashRef = db.getReference("flashcards").child(auth.currentUser!!.uid).child(flashCard.id)
        flashRef.setValue(flashCard).addOnCompleteListener {
            if (it.isSuccessful) {
                callback("Flashcard updated")
            } else {
                callback(it.exception?.message ?: "Failed to update flashcard")
            }
        }
    }

    fun deleteFlashCard(flashCard: FlashCard, callback: (String) -> Unit) {
        val flashRef = db.getReference("flashcards").child(auth.currentUser!!.uid).child(flashCard.id)
        flashRef.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback("Flashcard deleted")
            } else {
                callback(it.exception?.message ?: "Failed to delete flashcard")
            }
        }
    }
}