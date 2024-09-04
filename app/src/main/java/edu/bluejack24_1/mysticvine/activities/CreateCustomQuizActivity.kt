package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityCreateCustomQuizBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateCustomQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        customQuestionViewModel = ViewModelProvider(this)[CustomQuestionViewModel::class.java]
        customAnswerViewModel = ViewModelProvider(this)[CustomAnswerViewModel::class.java]
        partyMemberViewModel = ViewModelProvider(this)[PartyMemberViewModel::class.java]



        val partyCode = intent.getStringExtra("partyCode")
        customQuestionViewModel.getCustomQuestions(partyCode!!)

        userViewModel.currentUser.observe(this) {user ->
            if (user == null) return@observe
            binding.btnFinalize.setOnClickListener {
                val question = binding.etQuestion.text.toString()
                val answer = binding.etAnswer.text.toString()
                customQuestionViewModel.createCustomQuestion(partyCode!!, question, user.id)
                customQuestionViewModel.createCustomQuestionResult.observe(this) { result ->
                    if (result != "Failed to create question") {
                        customAnswerViewModel.addCustomAnswer(result, answer, user.id)
                        customAnswerViewModel.createCustomAnswerResult.observe(this) {
                            if (it == "Success") {
                                binding.btnFinalize.isEnabled = false
                            }
                        }
                    }
                }
            }
        }

        customQuestionViewModel.customQuestionList.observe(this) { customQuestions ->
            partyMemberViewModel.getPartyMember(partyCode)
            partyMemberViewModel.joinedMemberList.observe(this) { joinedMembers ->
                if (customQuestions == null || joinedMembers == null) return@observe
                if (customQuestions.size == joinedMembers.size) {
                    val intent = Intent(this, CustomAnswerQuizPage::class.java)
                    intent.putExtra("partyCode", partyCode)
                    startActivity(intent)
                    finish()
                }
            }
        }



    }
}