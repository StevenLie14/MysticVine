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
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityCreateCustomQuizBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.CustomAnswerViewModel
import edu.bluejack24_1.mysticvine.viewmodel.CustomQuestionViewModel
import edu.bluejack24_1.mysticvine.viewmodel.PartyMemberViewModel
import edu.bluejack24_1.mysticvine.viewmodel.PartyViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel


class CreateCustomQuizPage : AppCompatActivity() {

    private lateinit var binding : ActivityCreateCustomQuizBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var customQuestionViewModel: CustomQuestionViewModel
    private lateinit var customAnswerViewModel: CustomAnswerViewModel
    private lateinit var partyMemberViewModel: PartyMemberViewModel
    private var timer: CreateCustomQuizPage.MyCounter? = null
    private val created = false
    private var isNavigated = false

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

        binding = ActivityCreateCustomQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        customQuestionViewModel = ViewModelProvider(this)[CustomQuestionViewModel::class.java]
        customAnswerViewModel = ViewModelProvider(this)[CustomAnswerViewModel::class.java]
        partyMemberViewModel = ViewModelProvider(this)[PartyMemberViewModel::class.java]

        customQuestionViewModel.resetAll()
        customAnswerViewModel.resetAll()
        partyMemberViewModel.resetAll()

        binding.closeButton.setOnClickListener {
            finish()
        }

        customQuestionViewModel.createCustomQuestionResult.observe(this) { result ->
            if (result == "Answer created") {
                binding.btnFinalize.isEnabled = false
                binding.etQuestion.isEnabled = false
                binding.etAnswer.isEnabled = false
                binding.btnFinalize.text = getString(R.string.waiting_for_other_players)
            }else if (result == "Failed to create question") {
                Utils.showSnackBar(binding.root, result, true)
            }
        }

        val partyCode = intent.getStringExtra("partyCode")
        val partyQuestionId = intent.getStringExtra("partyQuestionId")

        customQuestionViewModel.getCustomQuestions(partyCode!!, partyQuestionId!!)

        userViewModel.currentUser.observe(this) {user ->
            if (user == null) return@observe
            Glide.with(binding.ivAvatar).load(user.profilePicture).into(binding.ivAvatar)
            timer = MyCounter(20000, 1000, binding.tvTimer, this) {
                if (created) return@MyCounter
                Log.d("Timer", "Timer finished")
                val question = binding.etQuestion.text.toString()
                val answer = binding.etAnswer.text.toString()

                customQuestionViewModel.createCustomQuestion(partyCode!!,partyQuestionId, question, answer, user.id)
            }

            timer?.start()

            binding.btnFinalize.setOnClickListener {
                if (created) return@setOnClickListener
                val question = binding.etQuestion.text.toString()
                val answer = binding.etAnswer.text.toString()

                customQuestionViewModel.createCustomQuestion(partyCode!!,partyQuestionId, question,answer, user.id)
            }
        }

        partyMemberViewModel.getPartyMember(partyCode)
        partyMemberViewModel.joinedMemberList.observe(this) { joinedMembers ->
            customQuestionViewModel.customQuestionList.observe(this) { customQuestions ->
                if (customQuestions == null || joinedMembers == null || customQuestions.isEmpty() || joinedMembers.isEmpty()) return@observe
                if (customQuestions.size == joinedMembers.size && !isNavigated) {
                    var total = 0
                    for (customQuestion in customQuestions) {
                        customAnswerViewModel.getCustomAnswers(partyCode,partyQuestionId,customQuestion)
                        customAnswerViewModel.customAnswerList.observe(this) { ans ->
                            if (ans == null || ans.isEmpty()) return@observe
                            total += ans.size
                            if (total == joinedMembers.size) {
                                if (!isNavigated) {
                                    isNavigated = true
                                    timer?.cancel()
                                    val intent = Intent(this, CustomQuizPage::class.java)
                                    intent.putExtra("partyCode", partyCode)
                                    intent.putExtra("partyQuestionId", partyQuestionId)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }

                    }
                }
            }
        }
    }

}