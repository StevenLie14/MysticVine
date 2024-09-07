package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.bluejack24_1.mysticvine.model.CustomQuizAnswer
import edu.bluejack24_1.mysticvine.model.CustomQuizQuestion
import edu.bluejack24_1.mysticvine.repositories.CustomAnswerRepository
import edu.bluejack24_1.mysticvine.repositories.CustomQuestionRepository

class CustomAnswerViewModel (application: Application) : AndroidViewModel(application) {

    private val customAnswerRepository = CustomAnswerRepository()


    private val _createCustomAnswerResult = MutableLiveData<String>()
    val createCustomAnswerResult: LiveData<String> = _createCustomAnswerResult
    fun addCustomAnswer(partyCode: String,partyQuestionId : String, question: CustomQuizQuestion, answer: String, userId: String) {
        val answer = CustomQuizAnswer(question.questionId, answer.ifEmpty { "No Answer Inserted" }, userId)
        customAnswerRepository.createCustomAnswer(partyCode,partyQuestionId, question,answer) { result ->
            _createCustomAnswerResult.value = result
        }
    }

    private val _customAnswerList = MutableLiveData<List<CustomQuizAnswer>>()
    val customAnswerList: LiveData<List<CustomQuizAnswer>> = _customAnswerList
    fun getCustomAnswers(partyCode: String,partyQuestionId : String, question: CustomQuizQuestion) {
        customAnswerRepository.getCustomAnswers(partyCode,partyQuestionId, question, _customAnswerList)
    }

    private val _customAnswerMap = MutableLiveData<Map<CustomQuizQuestion, List<CustomQuizAnswer>>>()
    val customAnswerMap: LiveData<Map<CustomQuizQuestion, List<CustomQuizAnswer>>> = _customAnswerMap

    fun getCustomAnswersForQuestions(partyCode: String, partyQuestionId: String, questions: List<CustomQuizQuestion>) {
        customAnswerRepository.getCustomAnswersFromQuestions(partyCode, partyQuestionId, questions, _customAnswerMap)
    }

    fun getNonRTCustomAnswers(partyCode: String, question: CustomQuizQuestion, callback: (List<CustomQuizAnswer>) -> Unit) {
        customAnswerRepository.getNonRTCustomAnswers(partyCode, question, callback)
    }

    fun resetAll() {
        _createCustomAnswerResult.value = ""
        _customAnswerList.value = emptyList()
    }
}