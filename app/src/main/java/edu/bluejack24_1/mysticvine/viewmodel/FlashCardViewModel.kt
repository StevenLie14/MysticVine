package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.bluejack24_1.mysticvine.model.FlashCard
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.repositories.FlashCardRepository
import java.time.LocalTime
import java.util.UUID

class FlashCardViewModel (application: Application) : AndroidViewModel(application) {

    private val flashCardRepository : FlashCardRepository = FlashCardRepository()

    private val _flashCardsResult = MutableLiveData<String>()
    val flashCardsResult: LiveData<String> = _flashCardsResult

    fun createFlashCard(question : String, answer : String, userId : String) {
        if (question.isEmpty() || answer.isEmpty()) {
            _flashCardsResult.value = "Please fill all the fields"
            return
        }

        val flashCard = FlashCard(UUID.randomUUID().toString(), question, answer, System.currentTimeMillis() ,userId)
        flashCardRepository.createFlashCard(flashCard) { result ->
            _flashCardsResult.value = result
        }
    }


    private val _deleteFlashCardResult = MutableLiveData<String>()
    val deleteFlashCardResult: LiveData<String> = _deleteFlashCardResult
    fun deleteFlashCard(flashCard: FlashCard) {
        flashCardRepository.deleteFlashCard(flashCard) { result ->
            _deleteFlashCardResult.value = result
        }
    }

    private val _flashcards = MutableLiveData<List<FlashCard>>()
    val flashcards: LiveData<List<FlashCard>> = _flashcards
    init {
        flashCardRepository.getFlashCards(_flashcards)
    }
}