package edu.bluejack24_1.mysticvine.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.ResultCallback
import edu.bluejack24_1.mysticvine.model.QuizResult
import edu.bluejack24_1.mysticvine.repositories.QuizResultRepository
import java.util.UUID

class QuizResultViewModel : ViewModel() {

    private val quizResultRepository = QuizResultRepository()

    fun createQuizResult(quizId: String, userId: String, callback: (Int, String) -> Unit) {
        var quizResult = QuizResult(UUID.randomUUID().toString(), quizId, userId, 0, 0, 0)
        quizResultRepository.createQuizResult(quizResult) { code, message ->
            callback(code, message)
        }
    }


    fun updateQuizResult(
        quizResult: QuizResult,
        time: Int,
        maxTime: Int,
        baseScore: Int = 100,
        type: Boolean,
        callback: (Int, String) -> Unit
    ) {
        val timeFactor: Double = (maxTime - time).toDouble() / maxTime
        val timeBonus: Int = (timeFactor * baseScore).toInt()
        Log.d("timeBonus", timeBonus.toString())
        val score = if (type) baseScore + timeBonus else (-1 * baseScore) - timeBonus
        val coin = (score * timeFactor * 3).toInt() * if (score > 0) 1 else -1
        var quizResult = QuizResult(
            quizResult.resultId,
            quizResult.quizId,
            quizResult.userId,
            quizResult.score + score,
            quizResult.correct + if (type) 1 else 0,
            quizResult.wrong + if (!type) 1 else 0,
            quizResult.coin + coin
        )
        quizResultRepository.updateQuizResult(quizResult) { code, message ->
            callback(code, message)
        }
    }

    fun addBooster(quizResult: QuizResult, boosterType: Int, callback: (Int, String) -> Unit) {
        when (boosterType) {
            1 -> {
                var quizResult = QuizResult(
                    quizResult.resultId,
                    quizResult.quizId,
                    quizResult.userId,
                    quizResult.score,
                    quizResult.correct,
                    quizResult.wrong,
                    quizResult.coin,
                    quizResult.extraExp,
                    quizResult.coin
                )
                quizResultRepository.updateQuizResult(quizResult) { code, message ->
                    callback(code, message)
                }
            }

            2 -> {
                var quizResult = QuizResult(
                    quizResult.resultId,
                    quizResult.quizId,
                    quizResult.userId,
                    quizResult.score,
                    quizResult.correct,
                    quizResult.wrong,
                    quizResult.coin,
                    quizResult.score,
                    quizResult.extraCoin
                )
                quizResultRepository.updateQuizResult(quizResult) { code, message ->
                    callback(code, message)
                }
            }

            3 -> {
                var quizResult = QuizResult(
                    quizResult.resultId,
                    quizResult.quizId,
                    quizResult.userId,
                    quizResult.score,
                    quizResult.correct,
                    quizResult.wrong,
                    quizResult.coin,
                    (quizResult.score * -1 / 2),
                    quizResult.extraCoin
                )
                quizResultRepository.updateQuizResult(quizResult) { code, message ->
                    callback(code, message)
                }
            }
        }
    }

    private val _quizResult = MutableLiveData<QuizResult>()
    val quizResult: LiveData<QuizResult> = _quizResult
    fun getQuizResult(quizId: String, userId: String, resultId: String) {
        quizResultRepository.getQuizResult(quizId, userId, resultId, _quizResult)
    }

    fun resetAll() {
        _quizResult.value = QuizResult()
    }


    init {
        _quizResult.value = QuizResult()
    }
}