package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.bluejack24_1.mysticvine.model.CustomQuizAnswer
import edu.bluejack24_1.mysticvine.model.CustomQuizQuestion
import edu.bluejack24_1.mysticvine.repositories.CustomAnswerRepository
import edu.bluejack24_1.mysticvine.repositories.CustomQuestionRepository
import java.util.UUID

class CustomQuestionViewModel (application: Application) : AndroidViewModel(application) {

    private val customQuestionRepository = CustomQuestionRepository()
    private val customAnswerRepository = CustomAnswerRepository()

    private val _createCustomQuestionResult = MutableLiveData<String>()
    val createCustomQuestionResult: LiveData<String> = _createCustomQuestionResult
    fun createCustomQuestion(partyCode: String,partyQuestionId: String, question: String, answer: String, userId: String) {

        val questionModel = CustomQuizQuestion(UUID.randomUUID().toString(),partyCode,
            question.ifEmpty { "No Question Inserted" }, userId)
        val answerModel = CustomQuizAnswer(questionModel.questionId, answer.ifEmpty { "No Answer Inserted" }, userId)
        customQuestionRepository.createCustomQuestion(partyQuestionId,questionModel) { result ->
            if (result == "Question created") {
                customAnswerRepository.createCustomAnswer(partyCode,partyQuestionId, questionModel,answerModel) { questionResult ->
                    _createCustomQuestionResult.value = questionResult
                }
            }
        }
    }

    private val _customQuestionList = MutableLiveData<List<CustomQuizQuestion>>()
    val customQuestionList: LiveData<List<CustomQuizQuestion>> = _customQuestionList

    fun getCustomQuestions(partyCode: String, partyQuestionId: String) {
        customQuestionRepository.getCustomQuestions(partyCode,partyQuestionId, _customQuestionList)
    }


    fun deleteQuestions(partyCode: String) {
        customQuestionRepository.deleteQuestions(partyCode) {}
    }

    fun getNonRTCustomQuestions(partyCode: String, callback: (List<CustomQuizQuestion>) -> Unit) {
        customQuestionRepository.getNotRTCustomQuestions(partyCode, callback)
    }

    fun resetAll() {
        _createCustomQuestionResult.value = ""
        _customQuestionList.value = emptyList()
    }
}