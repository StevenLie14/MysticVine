package edu.bluejack24_1.mysticvine.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityQuestionPageBinding
import edu.bluejack24_1.mysticvine.model.Questions
import edu.bluejack24_1.mysticvine.model.QuizResult
import edu.bluejack24_1.mysticvine.model.Quizzes
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.viewmodel.QuestionViewModel
import edu.bluejack24_1.mysticvine.viewmodel.QuizResultViewModel
import edu.bluejack24_1.mysticvine.viewmodel.QuizViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel

class QuestionPage : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionPageBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var quizResultViewModel: QuizResultViewModel
    private lateinit var questions : List<Questions>
    private var correctAnswer: String = ""
    private var timer: MyCounter? = null
    private var isTimerRunning = false
    private var idx = 0
    private var isLoaded = false
    private lateinit var quizId : String
    private lateinit var resultId : String
    private lateinit var creatorId : String
    private lateinit var question : Questions
    private lateinit var quiz : Quizzes
    private lateinit var result : QuizResult
    private lateinit var users: Users


    inner class MyCounter(
        millisInFuture: Long,
        countDownInterval: Long,
        private val timerTextView: TextView,
        private val context: Context,
        private val callback: (Boolean) -> Unit

    ) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            Log.d("Timer", "Timer finished")
            callback(true)
        }

        override fun onTick(millisUntilFinished: Long) {

            val secondsRemaining = millisUntilFinished / 1000
            timerTextView.text = (millisUntilFinished / 1000).toString()
            if (secondsRemaining <= 3) {
                timerTextView.textSize = 24f
                timerTextView.setTextColor(ContextCompat.getColor(context, R.color.dark_red))
            } else {
                timerTextView.textSize = 20f
                timerTextView.setTextColor(ContextCompat.getColor(context, R.color.dark_green))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuestionPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        questionViewModel = ViewModelProvider(this)[QuestionViewModel::class.java]
        quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        quizResultViewModel = ViewModelProvider(this)[QuizResultViewModel::class.java]

        val optionA: LinearLayout = binding.A
        val optionB: LinearLayout = binding.B
        val optionC: LinearLayout = binding.C
        val optionD: LinearLayout = binding.D
        val optionE: LinearLayout = binding.E

        quizId = intent.getStringExtra("quizId")!!
        creatorId = intent.getStringExtra("creatorId")!!
        resultId = intent.getStringExtra("resultId")!!

        setOptionClickListener(optionA, "A")
        setOptionClickListener(optionB, "B")
        setOptionClickListener(optionC, "C")
        setOptionClickListener(optionD, "D")
        setOptionClickListener(optionE, "E")

        quizViewModel.getQuizById(quizId,creatorId)
        questionViewModel.getQuestions(quizId)


        userViewModel.currentUser.observe(this) { user ->
            if (user == null) return@observe
            Glide.with(binding.profilePicture)
                .load(user.profilePicture.toUri())
                .into(binding.profilePicture)

            users = user

            quizResultViewModel.getQuizResult(quizId,user.id,resultId)
            quizResultViewModel.quizResult.observe(this) { it ->
                if (it == null) return@observe
                result = it
            }



            quizViewModel.quiz.observe(this) { quiz ->
                Log.d("QuestionPage Map", "Quiz: $quiz")
                if (quiz == null) return@observe
                this.quiz = quiz
                questionViewModel.questions.observe(this) { questionList ->
                    Log.d("QuestionPage Map", "Questions: $questionList")
                    if (questionList.isNullOrEmpty()) return@observe
                    questions = questionList
                    Log.d("QuestionPage Map", "Questions: $questions")
                    if (!isLoaded) {
                        isLoaded = true
                        moveToNextQuestion()
                    }
                }
            }

        }
    }
    private fun setOptionClickListener(option: LinearLayout, selectedAnswer: String) {
        option.setOnClickListener {
            handleOptionClick(option, selectedAnswer)
        }
    }

    private fun handleOptionClick(option: LinearLayout, selectedAnswer: String) {
        disableOtherOptions()
        if (correctAnswer == selectedAnswer) {
            option.setBackgroundResource(R.drawable.light_green_rounded)
            showToast("Correct!")
            quizResultViewModel.updateQuizResult(result,binding.timer.text.toString().toInt(),20, type = true) { code, message ->
                if (code == 200) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        moveToNextQuestion()
                    }, 1000)
                } else {
                    showToast("Failed to update quiz result")
                }
            }
        } else {
            option.setBackgroundResource(R.drawable.light_red_rounded)
            showToast("Wrong!")
            quizResultViewModel.updateQuizResult(result,binding.timer.text.toString().toInt(),20, type = false) { code, message ->
                if (code == 200) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        moveToNextQuestion()
                    }, 1000)
                } else {
                    showToast("Failed to update quiz result")
                }
            }
        }

    }

    private fun moveToNextQuestion() {
        stopTimer()
        if (idx >= questions.size) {
            binding.questionTitle.text = "Quiz Finished"
            if (users.coinBooster > 0) {
                quizResultViewModel.addBooster(result, 1) { code, _ ->
                    if (code == 200) {
                        userViewModel.deleteItem(1, users) { code ->
                            if (code == 200) {
                                showToast("Coin booster activated")
                            } else {
                                showToast("Failed to activate coin booster")
                            }
                        }
                    } else {
                        showToast("Failed to activate coin booster")
                    }
                }
            }
            if (users.expBooster > 0 && result.score > 0) {
                quizResultViewModel.addBooster(result, 2) { code, _ ->
                    if (code == 200) {
                        userViewModel.deleteItem(2, users) { code ->
                            if (code == 200) {
                                showToast("Exp booster activated")
                            } else {
                                showToast("Failed to activate exp booster")
                            }
                        }
                    } else {
                        showToast("Failed to activate exp booster")
                    }
                }

            }
            if (users.shieldBooster > 0 && result.score < 0) {
                quizResultViewModel.addBooster(result, 3) { code, _ ->
                    if (code == 200) {
                        userViewModel.deleteItem(3, users) { code ->
                            if (code == 200) {
                                showToast("Shield booster activated")
                            } else {
                                showToast("Failed to activate shield booster")
                            }
                        }
                    } else {
                        showToast("Failed to activate shield booster")
                    }
                }
            }
            userViewModel.updateStatus(users,result.score + result.extraExp, result.coin + result.extraCoin) { code ->
                if (code == 200) {
                    val intent = Intent(this, ResultPage::class.java)
                    intent.putExtra("quizId", quizId)
                    intent.putExtra("resultId", resultId)
                    startActivity(intent)
                    finish()
                } else {
                    showToast("Failed to update user status")
                }
            }
            return
        }

        question = questions[idx]
        idx++


        startTimer(20000, onFinishCallback = {
            quizResultViewModel.updateQuizResult(result,binding.timer.text.toString().toInt(),20, type = false) { code, message ->
                if (code == 200) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        moveToNextQuestion()
                    }, 1000)
                } else {
                    showToast("Failed to update quiz result")
                }
            }
        })

        binding.questionTitle.text = "Question ${idx}/${questions.size} - ${quiz.title}"
        binding.tvQuestion.text = question.question
        binding.A.isClickable = true
        binding.B.isClickable = true
        binding.C.isClickable = true
        binding.D.isClickable = true
        binding.E.isClickable = true
        binding.A.setBackgroundResource(R.drawable.dark_green_rounded)
        binding.B.setBackgroundResource(R.drawable.dark_green_rounded)
        binding.C.setBackgroundResource(R.drawable.dark_green_rounded)
        binding.D.setBackgroundResource(R.drawable.dark_green_rounded)
        binding.E.setBackgroundResource(R.drawable.dark_green_rounded)
        binding.answerA.text = question.optionA
        binding.answerB.text = question.optionB
        binding.answerC.text = question.optionC
        binding.answerD.text = question.optionD
        binding.answerE.text = question.optionE
        correctAnswer = question.answer
        binding.progressBar.progress = idx
        binding.progressBar.max = questions.size

    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun disableOtherOptions() {
        val optionA: LinearLayout = binding.A
        val optionB: LinearLayout = binding.B
        val optionC: LinearLayout = binding.C
        val optionD: LinearLayout = binding.D
        val optionE: LinearLayout = binding.E
        optionA.isClickable = false
        optionB.isClickable = false
        optionC.isClickable = false
        optionD.isClickable = false
        optionE.isClickable = false
    }

    private fun startTimer(duration: Long, onFinishCallback: () -> Unit) {
        if (isTimerRunning) stopTimer()
        timer = MyCounter(duration, 1000, binding.timer, this) {
            onFinishCallback()
        }
        timer?.start()
        isTimerRunning = true
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
        isTimerRunning = false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    override fun onStop() {
        super.onStop()
        stopTimer()
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }
}
