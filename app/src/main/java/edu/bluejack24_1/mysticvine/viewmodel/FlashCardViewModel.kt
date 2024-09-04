package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.bluejack24_1.mysticvine.model.FlashCard
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.repositories.FlashCardRepository
import java.time.LocalDate
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

        val flashCard = FlashCard(UUID.randomUUID().toString(), question, answer, LocalDate.now().toString() ,userId)
        flashCardRepository.createFlashCard(flashCard) { result ->
            _flashCardsResult.value = result
        }
    }

    private val _updateFlashCardResult = MutableLiveData<String>()
    val updateFlashCardResult: LiveData<String> = _updateFlashCardResult

    fun updateFlashCardResult(flashCard: FlashCard, result: Boolean) {
        flashCardRepository.updateRememberFlashCard(flashCard, result) { result ->
            _updateFlashCardResult.value = result
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

    private val _daily = MutableLiveData<List<FlashCard>>()
    val daily: LiveData<List<FlashCard>> = _daily
    init {
        flashCardRepository.updateDaily()

        flashCardRepository.getFlashCards(_flashcards)
        flashCardRepository.getDailyFlashcards(_daily)
    }
}