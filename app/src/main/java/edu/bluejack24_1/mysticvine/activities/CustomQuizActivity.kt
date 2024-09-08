package edu.bluejack24_1.mysticvine.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityCustomQuizBinding
import edu.bluejack24_1.mysticvine.model.CustomQuizQuestion
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.viewmodel.CustomAnswerViewModel
import edu.bluejack24_1.mysticvine.viewmodel.CustomQuestionViewModel
import edu.bluejack24_1.mysticvine.viewmodel.PartyMemberViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel

class CustomQuizPage : AppCompatActivity() {

    private lateinit var binding: ActivityCustomQuizBinding
    private lateinit var customAnswerViewModel: CustomAnswerViewModel
    private lateinit var customQuestionViewModel: CustomQuestionViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var partyMemberViewModel: PartyMemberViewModel
    private var idx = 0
    private var timer: MyCounter? = null
    private var isTimerRunning = false
    private var currentQuestion: CustomQuizQuestion? = null
    private lateinit var questions: List<CustomQuizQuestion>
    private lateinit var user: Users
    private var isLoaded = false

    inner class MyCounter(
        millisInFuture: Long,
        countDownInterval: Long,
        private val timerTextView: TextView,
        private val context: Context,
        private val callback: () -> Unit
    ) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            callback()
        }

        override fun onTick(millisUntilFinished: Long) {
            val secondsRemaining = millisUntilFinished / 1000
            timerTextView.text = secondsRemaining.toString()
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
        partyMemberViewModel = ViewModelProvider(this)[PartyMemberViewModel::class.java]

        partyMemberViewModel.resetAll()
        customAnswerViewModel.resetAll()
        customQuestionViewModel.resetAll()

        val partyCode = intent.getStringExtra("partyCode")
        val partyQuestionId = intent.getStringExtra("partyQuestionId")

        customQuestionViewModel.getCustomQuestions(partyCode!!, partyQuestionId!!)
        partyMemberViewModel.getPartyMember(partyCode)

        binding.closeButton.setOnClickListener { finish() }

        userViewModel.currentUser.observe(this) { currentUser ->
            if (currentUser == null) return@observe
            user = currentUser
            Glide.with(binding.avatar).load(user.profilePicture).into(binding.avatar)

            customQuestionViewModel.customQuestionList.observe(this) { questionList ->
                if (questionList.isNullOrEmpty()) return@observe
                questions = questionList

                customAnswerViewModel.getCustomAnswersForQuestions(partyCode, partyQuestionId, questions)
                partyMemberViewModel.joinedMemberList.observe(this) { joinedMembers ->
                    customAnswerViewModel.customAnswerMap.observe(this) { map ->
                        Log.d("CustomQuizPage Map", "Joined members: $joinedMembers")
                        if (joinedMembers.isNullOrEmpty() || map.isNullOrEmpty()) return@observe

                        val navigate = map.all { (_, answers) ->
                            Log.d("CustomQuizPage Map", "Answers: ${answers.size} == ${joinedMembers.size}")
                            answers.size == joinedMembers.size
                        }
                        Log.d("CustomQuizPage Map", "Navigate: $navigate")

                        if (navigate) {
                            stopTimer()
                            val intent = Intent(this, CustomAnswerQuizPage::class.java)
                            intent.putExtra("partyCode", partyCode)
                            intent.putExtra("partyQuestionId", partyQuestionId)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                if (!isLoaded) {
                    loadNextQuestion(partyQuestionId)
                    isLoaded = true
                }

            }
        }
    }

    private fun loadNextQuestion(partyQuestionId: String) {
        stopTimer()
        if (idx >= questions.size) {
            binding.tvTimer.text = "0"
            binding.question.text = getString(R.string.waiting_for_other_players)
            binding.btnFinalize.text = getString(R.string.waiting_for_other_players)
            binding.etAnswer.hint = getString(R.string.waiting_for_other_players)
            binding.etAnswer.isEnabled = false
            binding.btnFinalize.isEnabled = false
            return
        }

        currentQuestion = questions[idx]
        idx++

        if (user.id == currentQuestion?.userId) {
            loadNextQuestion(partyQuestionId)
            return
        }

        binding.question.text = currentQuestion?.question ?: ""
        binding.etAnswer.text.clear()
        binding.etAnswer.isEnabled = true
        binding.btnFinalize.isEnabled = true

        startTimer(20000) {
            submitAnswer(partyQuestionId = partyQuestionId)
        }

        binding.btnFinalize.setOnClickListener {
            submitAnswer(partyQuestionId = partyQuestionId)
        }
    }

    private fun submitAnswer(partyQuestionId : String) {
        stopTimer()
        binding.etAnswer.isEnabled = false
        binding.btnFinalize.isEnabled = false

        currentQuestion?.let { question ->
            customAnswerViewModel.addCustomAnswer(
                question.partyCode,
                partyQuestionId,
                question,
                binding.etAnswer.text.toString(),
                user.id
            )
        }

        customAnswerViewModel.createCustomAnswerResult.observe(this) { result ->
            if (result == "Answer created") {
                loadNextQuestion(partyQuestionId)
            } else {
                Log.e("CustomQuizPage", "Error adding answer")
            }
        }
    }

    private fun startTimer(duration: Long, onFinishCallback: () -> Unit) {
        if (isTimerRunning) stopTimer()
        timer = MyCounter(duration, 1000, binding.tvTimer, this) {
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
