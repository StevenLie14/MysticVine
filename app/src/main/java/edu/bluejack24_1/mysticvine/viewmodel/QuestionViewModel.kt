package edu.bluejack24_1.mysticvine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.mysticvine.model.FlashCard
import edu.bluejack24_1.mysticvine.model.Questions
import edu.bluejack24_1.mysticvine.model.Quizzes
import edu.bluejack24_1.mysticvine.repositories.QuestionRepository
import edu.bluejack24_1.mysticvine.repositories.QuizRepository
import java.util.UUID

class QuestionViewModel : ViewModel() {

    private val questionRepository: QuestionRepository = QuestionRepository()

    private val _questionResult = MutableLiveData<String>()
    val questionResult: LiveData<String> = _questionResult
    fun isQuestionEligible(
        questions: String,
        rightAnswer: String,
        answerA: String,
        answerB: String,
        answerC: String,
        answerD: String,
        answerE: String
    ): Boolean {
        if (questions.isEmpty() || rightAnswer.length > 1 ||
            answerA.isEmpty() || answerB.isEmpty() || answerC.isEmpty() ||
            answerD.isEmpty() || answerE.isEmpty()
        ) {
            _questionResult.value = "Please fill all the fields"
            return false
        }
        return true
    }

    fun createQuestion(
        quizId: String,
        questions: String,
        rightAnswer: String,
        answerA: String,
        answerB: String,
        answerC: String,
        answerD: String,
        answerE: String
    ) {
        val question = Questions(quizId, UUID.randomUUID().toString(), questions, rightAnswer, answerA, answerB, answerC, answerD, answerE)
        questionRepository.createQuestion(question) { result ->
            _questionResult.value = result
        }
    }

}

