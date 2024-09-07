package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.adapters.WaitingRoomAdapter
import edu.bluejack24_1.mysticvine.databinding.ActivityWaitingRoomBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.PartyMemberViewModel
import edu.bluejack24_1.mysticvine.viewmodel.PartyViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel


class WaitingRoomPage : AppCompatActivity() {

    private lateinit var binding: ActivityWaitingRoomBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var partyViewModel: PartyViewModel
    private  lateinit var partyMemberViewModel: PartyMemberViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaitingRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val partyCode = intent.getStringExtra("partyCode")

        partyViewModel = ViewModelProvider(this)[PartyViewModel::class.java]
        partyMemberViewModel = ViewModelProvider(this)[PartyMemberViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        partyViewModel.resetAll()

        partyViewModel.getPartyHost(partyCode!!)
        partyViewModel.getPartyRoom(partyCode)

        userViewModel.currentUser.observe(this) { user ->
            if (user == null) return@observe
            Glide.with(binding.ivAvatar)
                .load(user.profilePicture)
                .into(binding.ivAvatar)
            partyViewModel.partyHost.observe(this) {host ->
                if (host == null) return@observe
                binding.hostUsername.text = host.username
                binding.partyCode.text = partyCode
                if (host.id != user.id) {
                    binding.startBtn.visibility = View.VISIBLE
                    binding.startBtn.isEnabled = false
                    binding.startBtn.text = getString(R.string.waiting_for_host_to_start)
                    binding.closeButton.setOnClickListener {
                        partyMemberViewModel.leaveParty(partyCode, user.id ) {
                            if (it == "Success") {
                                val intent = Intent(this, LandingPage::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }


                }else {
                    binding.closeButton.setOnClickListener {
                        partyViewModel.deleteParty(partyCode)
                    }

                }
            }
        }

        binding.startBtn.setOnClickListener {
            partyViewModel.changePartyStatus(partyCode!!, "Started")
        }

        binding.closeButton.setOnClickListener {
            val intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
            finish()
        }

        partyViewModel.partyRoom.observe(this) { partyRoom ->
            if (partyRoom == null) {
                Utils.showSnackBar(binding.root, "Host has leave the party")
                val intent = Intent(this, LandingPage::class.java)
                startActivity(intent)
                finish()
                return@observe
            }
            if (partyRoom.partyStatus == "Started") {
                val intent = Intent(this, CreateCustomQuizPage::class.java)
                intent.putExtra("partyCode", partyCode)
                intent.putExtra("partyQuestionId",partyRoom.partyQuestionID)
                startActivity(intent)
                finish()
            }
        }



        val waitingRoomAdapter = WaitingRoomAdapter()
        partyMemberViewModel.getPartyMember(partyCode)
        binding.rvWaitingProfile.adapter = waitingRoomAdapter
        binding.rvWaitingProfile.layoutManager = GridLayoutManager(this, 4)
        binding.rvWaitingProfile.setHasFixedSize(true)

        partyMemberViewModel.joinedMemberList.observe(this) {
            waitingRoomAdapter.updateUserList(it)
        }




    }
}