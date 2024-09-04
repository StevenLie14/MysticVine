package edu.bluejack24_1.mysticvine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.mysticvine.model.FlashCard
import edu.bluejack24_1.mysticvine.model.Quizzes
import edu.bluejack24_1.mysticvine.repositories.QuizRepository
import java.util.UUID

class QuizViewModel : ViewModel() {

    private val quizRepository : QuizRepository = QuizRepository()

    private val _quizResult = MutableLiveData<String>()
    val quizResult: LiveData<String> = _quizResult

    fun isQuizEligible(title: String): Boolean {
        if (title.isEmpty()) {
            return false
        }
        return true
    }


    fun createQuizzes(quizId : String, userId : String, title : String) {
        val quiz = Quizzes(quizId, userId, title)
        quizRepository.createQuiz(quiz) { result ->
            _quizResult.value = result
        }
    }











}