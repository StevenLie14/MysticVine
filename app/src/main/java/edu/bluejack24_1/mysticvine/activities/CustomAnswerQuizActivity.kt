package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.adapters.CustomQuizAnswerAdapter
import edu.bluejack24_1.mysticvine.databinding.ActivityCustomAnswerQuizBinding
import edu.bluejack24_1.mysticvine.model.CustomQuizAnswer
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.CustomAnswerViewModel
import edu.bluejack24_1.mysticvine.viewmodel.CustomQuestionViewModel
import edu.bluejack24_1.mysticvine.viewmodel.PartyMemberViewModel
import edu.bluejack24_1.mysticvine.viewmodel.PartyViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel


class CustomAnswerQuizPage : AppCompatActivity() {

    private lateinit var binding: ActivityCustomAnswerQuizBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var customQuestionViewModel: CustomQuestionViewModel
    private lateinit var customAnswerViewModel: CustomAnswerViewModel
    private lateinit var partyMemberViewModel: PartyMemberViewModel
    private lateinit var partyViewModel: PartyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomAnswerQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        customQuestionViewModel = ViewModelProvider(this)[CustomQuestionViewModel::class.java]
        customAnswerViewModel = ViewModelProvider(this)[CustomAnswerViewModel::class.java]
        partyMemberViewModel = ViewModelProvider(this)[PartyMemberViewModel::class.java]
        partyViewModel = ViewModelProvider(this)[PartyViewModel::class.java]

        customQuestionViewModel.resetAll()
        customAnswerViewModel.resetAll()
        partyMemberViewModel.resetAll()
        partyViewModel.resetAll()

        val partyCode = intent.getStringExtra("partyCode")
        val partyQuestionId = intent.getStringExtra("partyQuestionId")


        customQuestionViewModel.getCustomQuestions(partyCode!!, partyQuestionId!!)
        partyMemberViewModel.getPartyMember(partyCode)
        partyViewModel.getPartyHost(partyCode)
        partyViewModel.getPartyRoom(partyCode)

        binding.btnBackToParty.visibility = View.GONE
        binding.ivNext.visibility = View.GONE
        binding.ivClose.visibility = View.GONE

        partyViewModel.changePartyStatusResult.observe(this) { result ->
            if (result == 200) {
                val intent = Intent(this, WaitingRoomPage::class.java)
                intent.putExtra("partyCode", partyCode)
                startActivity(intent)
                finish()
            }
        }

        userViewModel.currentUser.observe(this) {user ->
            if (user == null) return@observe
            Glide.with(binding.ivAvatar).load(user.profilePicture).into(binding.ivAvatar)





            partyViewModel.partyRoom.observe(this) { room ->
                if (room == null) {
                    Log.d("CustomAnswerQuizPagehaha", "Room not found")
                    Utils.showSnackBar(binding.root, "Host has leave the party")
                    val intent = Intent(this, LandingPage::class.java)
                    startActivity(intent)
                    finish()
                    return@observe
                }

                customQuestionViewModel.customQuestionList.observe(this) { questions ->
                    if (questions == null || questions.isEmpty()) {
                        return@observe
                    }

                    val question = questions.getOrNull(room.partyIndex)

                    partyViewModel.partyHost.observe(this) { host ->
                        if (host == null) {
                            return@observe
                        }

                        if (questions.size <= room.partyIndex + 1) {
                            if (user.id == host.id) {
                                binding.ivClose.visibility = View.VISIBLE
                                binding.btnBackToParty.visibility = View.VISIBLE
                                binding.ivNext.visibility = View.GONE

                                binding.ivClose.setOnClickListener {
                                    partyViewModel.deleteParty(partyCode)
                                }

                                binding.btnBackToParty.setOnClickListener {
                                    partyViewModel.changePartyStatus(partyCode, "Waiting")
                                }

                            }else {


                                partyMemberViewModel.leaveParty(partyCode, user.id) {
                                    if (it == "Success") {

                                    }
                                }
                                binding.ivClose.visibility = View.VISIBLE
                                binding.btnBackToParty.visibility = View.VISIBLE
                                binding.ivNext.visibility = View.GONE
                                if (room.partyStatus != "Waiting") {
                                    binding.btnBackToParty.text =
                                        getString(R.string.room_not_available)
                                    binding.btnBackToParty.isEnabled = false
                                }else {
                                    binding.btnBackToParty.text = getString(R.string.back_to_party)
                                    binding.btnBackToParty.isEnabled = true
                                    binding.btnBackToParty.setOnClickListener {
                                        partyMemberViewModel.joinParty(partyCode, user.id, "join")
                                        partyMemberViewModel.joinPartyResult.observe(this) { result ->
                                            if (result.length == 6 ) {
                                                val intent = Intent(this, WaitingRoomPage::class.java)
                                                intent.putExtra("partyCode", partyCode)
                                                startActivity(intent)
                                                finish()
                                            }
                                        }
                                    }
                                }
                                binding.ivClose.setOnClickListener {
                                    val intent = Intent(this, LandingPage::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }else {
                            if (user.id == host.id) {
                                binding.ivNext.visibility = View.VISIBLE
                                binding.ivClose.visibility = View.GONE
                                binding.btnBackToParty.visibility = View.GONE
                                binding.ivNext.setOnClickListener {
                                    partyViewModel.nextAnswer(room)
                                }
                            }else {
                                binding.ivNext.visibility = View.GONE
                                binding.ivClose.visibility = View.GONE
                                binding.btnBackToParty.visibility = View.VISIBLE
                                binding.btnBackToParty.text =
                                    getString(R.string.waiting_for_host_to_continue)
                                binding.btnBackToParty.isEnabled = false
                            }
                        }
                    }

                    if (question == null) {
                        Log.e("CustomAnswerQuizPagehaha", "Question not found")
                        return@observe
                    }

                    binding.tvQuestion.text = question.question
//
                    userViewModel.getUserById(question.userId) {asker ->
                        binding.tvAskedBy.text = getString(R.string.asked_by, asker.username)
                    }
//
                    customAnswerViewModel.getCustomAnswers(partyCode,partyQuestionId, question)
                    customAnswerViewModel.customAnswerList.observe(this) { ans ->
                        if (ans == null || ans.isEmpty()) {
                            Log.e("CustomAnswerQuizPagehaha", "No answer found")
                            return@observe
                        }
                        Log.e("CustomAnswerQuizPagehaha", ans.toString())
//
                        val adapter = CustomQuizAnswerAdapter()
                        val ansPair = mutableListOf<Pair<CustomQuizAnswer, Users>>()

                        binding.answerRecyclerView.adapter = adapter
                        binding.answerRecyclerView.layoutManager = LinearLayoutManager(this)
                        binding.answerRecyclerView.setHasFixedSize(true)

                        for (i in ans) {
                            userViewModel.getUserById(i.userId) { user ->
                                ansPair.add(Pair(i,user))
                                adapter.updateList(ansPair)
                            }
                        }

                    }
                }






//                customQuestionViewModel.(partyCode) { questions ->
//                    if (questions == null || questions.isEmpty()) {
//                        return@getNonRTCustomQuestions
//                    }
//
//                    partyViewModel.partyHost.observe(this) { host ->
//                        if (host == null) {
//                            return@observe
//                        }
//
//                        if ( questions.size >= room.partyIndex + 1) {
//                            if (host.id == user.id) {
//                                binding.ivNext.visibility = View.GONE
//                                binding.ivClose.visibility = View.VISIBLE
//                                binding.btnBackToParty.visibility = View.VISIBLE
//                            }else {
//                                binding.ivClose.visibility = View.VISIBLE
//                                binding.btnBackToParty.visibility = View.VISIBLE
//                            }
//                        }else {
//                            if (host.id == user.id) {
//                                binding.ivNext.visibility = View.VISIBLE
//                                binding.ivClose.visibility = View.GONE
//                                binding.btnBackToParty.visibility = View.GONE
//                            }else {
//                                binding.ivNext.visibility = View.VISIBLE
//                                binding.ivClose.visibility = View.GONE
//                                binding.btnBackToParty.visibility = View.GONE
//                            }
//                        }
//
//                        if (host.id == user.id) {
//
//                            binding.ivClose.setOnClickListener {
//                                partyViewModel.deleteParty(partyCode)
//                            }
//
//                            binding.btnBackToParty.setOnClickListener {
//                                partyViewModel.changePartyStatus(partyCode, "Waiting")
//                            }
//
//                            binding.ivNext.setOnClickListener {
//                                partyViewModel.nextAnswer(room)
//                            }
//
//                        }else {
//
//                            if (room.partyStatus != "Waiting") {
//                                binding.btnBackToParty.text = getString(R.string.room_not_available)
//                                binding.btnBackToParty.isEnabled = false
//                            }else {
//                                binding.btnBackToParty.text = getString(R.string.back_to_party)
//                                binding.btnBackToParty.isEnabled = true
//                                binding.btnBackToParty.setOnClickListener {
//                                    val intent = Intent(this, WaitingRoomPage::class.java)
//                                    intent.putExtra("partyCode", partyCode)
//                                    startActivity(intent)
//                                    finish()
//                                }
//                            }
//
//                            binding.ivClose.setOnClickListener {
//                                partyMemberViewModel.leaveParty(partyCode, user.id) {
//                                    if (it == "Success") {
//                                        Utils.showSnackBar(binding.root, "You have leave the party")
//                                        val intent = Intent(this, LandingPage::class.java)
//                                        startActivity(intent)
//                                        finish()
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    if (room.partyIndex >= questions.size) {
//                        Utils.showSnackBar(binding.root, "All question has been answered")
//                        return@getNonRTCustomQuestions
//                    }
//
//                    val question = questions.getOrNull(room.partyIndex)
//                    if (question == null) {
//                        Utils.showSnackBar(binding.root, "Question not found")
//                        return@getNonRTCustomQuestions
//                    }
//                    binding.tvQuestion.text = question.question
//                    userViewModel.getUserById(question.userId) {asker ->
//                        binding.tvAskedBy.text = getString(R.string.asked_by, asker.username)
//                    }
//                    customAnswerViewModel.getNonRTCustomAnswers(partyCode, question) { ans ->
//                        if (ans == null || ans.isEmpty()) {
//                            Log.e("CustomAnswerQuizPagehaha", "No answer found")
//                            return@getNonRTCustomAnswers
//                        }
//                        Log.e("CustomAnswerQuizPagehaha", ans.toString())
//
//
//                        val adapter = CustomQuizAnswerAdapter(userViewModel)
//                        binding.answerRecyclerView.adapter = adapter
//                        binding.answerRecyclerView.layoutManager = LinearLayoutManager(this)
//                        binding.answerRecyclerView.setHasFixedSize(true)
//                        adapter.updateList(ans)
//                    }
//                }
            }
        }
    }

}