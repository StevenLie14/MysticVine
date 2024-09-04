package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.bluejack24_1.mysticvine.model.CustomQuizAnswer
import edu.bluejack24_1.mysticvine.repositories.CustomAnswerRepository
import edu.bluejack24_1.mysticvine.repositories.CustomQuestionRepository

class CustomAnswerViewModel (application: Application) : AndroidViewModel(application) {

    private val customAnswerRepository = CustomAnswerRepository()


    private val _createCustomAnswerResult = MutableLiveData<String>()
    val createCustomAnswerResult: LiveData<String> = _createCustomAnswerResult
    fun addCustomAnswer(questionId: String, answer: String, userId: String) {
        val answer = CustomQuizAnswer(questionId, answer, userId)
        customAnswerRepository.createCustomAnswer(answer) { result ->
            _createCustomAnswerResult.value = result
        }
    }
}