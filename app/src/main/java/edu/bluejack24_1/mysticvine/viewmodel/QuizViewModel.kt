package edu.bluejack24_1.mysticvine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.mysticvine.model.FlashCard
import edu.bluejack24_1.mysticvine.model.Quizzes
import edu.bluejack24_1.mysticvine.repositories.QuizRepository

class QuizViewModel : ViewModel() {

    private val quizRepository: QuizRepository = QuizRepository()

    private val _quizResult = MutableLiveData<String>()
    val quizResult: LiveData<String> = _quizResult

    private val _quizzesList = MutableLiveData<List<Quizzes>>()
    val quizzesList: LiveData<List<Quizzes>> = _quizzesList

    fun isQuizEligible(title: String): Boolean {
        return title.isNotEmpty()
    }

    fun createQuizzes(quizId: String, userId: String, title: String) {
        val quiz = Quizzes(quizId, userId, title)
        quizRepository.createQuiz(quiz) { result ->
            _quizResult.value = result
        }
    }

    private val _allQuizzes = MutableLiveData<List<Quizzes>>()
    val allQuizzes: LiveData<List<Quizzes>> = _allQuizzes

    private val _userQuizzes = MutableLiveData<List<Quizzes>>()
    val userQuizzes: LiveData<List<Quizzes>> = _userQuizzes

    private val _random3Quiz = MutableLiveData<List<Quizzes>>()
    val random3Quiz: LiveData<List<Quizzes>> = _random3Quiz

    init {
        quizRepository.getAllQuizzes(_allQuizzes)
        quizRepository.getUserQuizzes(_userQuizzes)
        quizRepository.get3RandomQuiz(_random3Quiz)
    }
}
