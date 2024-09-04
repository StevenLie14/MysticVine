package edu.bluejack24_1.mysticvine.activities

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityQuestionPageBinding

class QuestionPage : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionPageBinding
    private val correctAnswer: String = "A"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuestionPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val timerText: TextView = findViewById(R.id.timer)
        val optionA: LinearLayout = findViewById(R.id.A)
        val optionB: LinearLayout = findViewById(R.id.B)
        val optionC: LinearLayout = findViewById(R.id.C)
        val optionD: LinearLayout = findViewById(R.id.D)
        val optionE: LinearLayout = findViewById(R.id.E)

        setOptionClickListener(optionA, "A")
        setOptionClickListener(optionB, "B")
        setOptionClickListener(optionC, "C")
        setOptionClickListener(optionD, "D")
        setOptionClickListener(optionE, "E")

        val timer = MyCounter(10000, 1000, timerText, this)
        timer.start()
    }
    private fun setOptionClickListener(option: LinearLayout, selectedAnswer: String) {
        option.setOnClickListener {
            handleOptionClick(option, selectedAnswer)
        }
    }

    private fun handleOptionClick(option: LinearLayout, selectedAnswer: String) {
        if (correctAnswer == selectedAnswer) {
            option.setBackgroundResource(R.drawable.light_green_rounded)
            showToast("Correct!")
        } else {
            option.setBackgroundResource(R.drawable.light_red_rounded)
            showToast("Wrong!")
        }
        disableOtherOptions()
    }

    inner class MyCounter(
        millisInFuture: Long,
        countDownInterval: Long,
        private val timerTextView: TextView,
        private val context: Context

    ) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            timerTextView.text = "Time's up!"
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun disableOtherOptions() {
        val optionA: LinearLayout = findViewById(R.id.A)
        val optionB: LinearLayout = findViewById(R.id.B)
        val optionC: LinearLayout = findViewById(R.id.C)
        val optionD: LinearLayout = findViewById(R.id.D)
        val optionE: LinearLayout = findViewById(R.id.E)
        optionA.isClickable = false
        optionB.isClickable = false
        optionC.isClickable = false
        optionD.isClickable = false
        optionE.isClickable = false
    }
}
