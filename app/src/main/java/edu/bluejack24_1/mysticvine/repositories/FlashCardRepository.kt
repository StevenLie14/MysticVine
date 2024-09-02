package edu.bluejack24_1.mysticvine.repositories

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack24_1.mysticvine.model.FlashCard

class FlashCardRepository {

    private val db = FirebaseDatabase.getInstance()

    fun createFlashCard(flashCard: FlashCard, callback: (String) -> Unit) {
        val flashRef = db.getReference("flashcards").child(flashCard.id)
        flashRef.setValue(flashCard).addOnCompleteListener {
            if (it.isSuccessful) {
                callback("Flashcard created")
            } else {
                callback(it.exception?.message ?: "Failed to create flashcard")
            }
        }
    }
}