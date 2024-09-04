package edu.bluejack24_1.mysticvine.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityCustomQuizBinding
import edu.bluejack24_1.mysticvine.viewmodel.CustomAnswerViewModel
import edu.bluejack24_1.mysticvine.viewmodel.CustomQuestionViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel
import kotlin.concurrent.timer


class CustomQuizPage : AppCompatActivity() {

    private lateinit var binding: ActivityCustomQuizBinding
    private lateinit var customAnswerViewModel: CustomAnswerViewModel
    private lateinit var customQuestionViewModel: CustomQuestionViewModel
    private lateinit var userViewModel: UserViewModel
    private var idx = 0
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
        binding = ActivityCustomQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customQuestionViewModel = ViewModelProvider(this)[CustomQuestionViewModel::class.java]
        customAnswerViewModel = ViewModelProvider(this)[CustomAnswerViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val partyCode = intent.getStringExtra("partyCode")

        customQuestionViewModel.getCustomQuestions(partyCode!!)



        userViewModel.currentUser.observe(this) {user ->
            if (user == null) return@observe
            Glide.with(binding.avatar).load(user.profilePicture).into(binding.avatar)
            Log.d("User Custom Quiz", user.toString())
            customQuestionViewModel.customQuestionList.observe(this) { questions ->
                if (questions == null) {
                    return@observe
                }

                val question = questions[idx]

                binding.question.text = question.question
                binding.btnFinalize.setOnClickListener {
                    customAnswerViewModel.addCustomAnswer(question.questionId, binding.etAnswer.text.toString(), user.id)
                }

                val timer = MyCounter(20000, 1000, binding.tvTimer, this) {
                    customAnswerViewModel.addCustomAnswer(question.questionId, binding.etAnswer.text.toString(), user.id)
                }
                timer.start()

                customAnswerViewModel.createCustomAnswerResult.observe(this) { result ->
                    Log.e("Custom Answer Hai", result)
                    if (result == "Answer created") {
                        idx++
                        if (idx < questions.size) {
                            if (user.id == question.userId) {
                                idx ++
                            }
                            val nextQuestion = questions[idx]
                            binding.question.text = nextQuestion.question
                            binding.etAnswer.text.clear()
                        } else {
                            val intent = Intent(this, CustomAnswerQuizPage::class.java)
                            intent.putExtra("partyCode", partyCode)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

            }
        }


    }
}