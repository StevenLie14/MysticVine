package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.bluejack24_1.mysticvine.model.CustomQuizQuestion
import edu.bluejack24_1.mysticvine.repositories.CustomQuestionRepository
import java.util.UUID

class CustomQuestionViewModel (application: Application) : AndroidViewModel(application) {

    private val customQuestionRepository = CustomQuestionRepository()

    private val _createCustomQuestionResult = MutableLiveData<String>()
    val createCustomQuestionResult: LiveData<String> = _createCustomQuestionResult
    fun createCustomQuestion(partyCode: String, question: String, userId: String) {

        val questionModel = CustomQuizQuestion(UUID.randomUUID().toString(),partyCode,
            question.ifEmpty { "No Question Inserted" }, userId)
        customQuestionRepository.createCustomQuestion(questionModel) { result ->
            _createCustomQuestionResult.value = result
        }
    }

    private val _customQuestionList = MutableLiveData<List<CustomQuizQuestion>>()
    val customQuestionList: LiveData<List<CustomQuizQuestion>> = _customQuestionList

    fun getCustomQuestions(partyCode: String) {
        customQuestionRepository.getCustomQuestions(partyCode, _customQuestionList)
    }
}