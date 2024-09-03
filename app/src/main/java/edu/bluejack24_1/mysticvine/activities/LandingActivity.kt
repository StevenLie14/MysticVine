package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.adapters.LandingLeaderBoardAdapter
import edu.bluejack24_1.mysticvine.databinding.ActivityLandingBinding
import edu.bluejack24_1.mysticvine.databinding.CustomGamePopUpBinding
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.PartyMemberViewModel
import edu.bluejack24_1.mysticvine.viewmodel.PartyViewModel
import edu.bluejack24_1.mysticvine.viewmodel.QuizViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel

class LandingPage : AppCompatActivity() {

    private  lateinit var  binding : ActivityLandingBinding
    private  lateinit var quizViewModel : QuizViewModel
    private  lateinit var partyViewModel : PartyViewModel
    private  lateinit var userViewModel: UserViewModel
    private  lateinit var partyMemberViewModel: PartyMemberViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        partyViewModel = ViewModelProvider(this)[PartyViewModel::class.java]
        partyMemberViewModel = ViewModelProvider(this)[PartyMemberViewModel::class.java]

        Utils.guestMiddleware(userViewModel, this)
        userViewModel.currentUser.observe(this) { user ->
            if (user == null) return@observe
            binding.welcomeText.text = getString(R.string.welcome_user, user?.username)
            binding.coinText.text = user?.coin.toString()
            binding.levelText.text = user?.level.toString()
            binding.levelProgress.progress = user?.exp ?: 0
            binding.levelProgress.max = Utils.getExpForLevel(user?.level ?: 0)
        }

        val leaderBoardAdapter = LandingLeaderBoardAdapter()
        binding.rvLeaderboard.adapter = leaderBoardAdapter
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(this)
        binding.rvLeaderboard.setHasFixedSize(true)
        userViewModel.leaderboard.observe(this) {
            leaderBoardAdapter.updateUserList(it)
        }

        binding.customGame.setOnClickListener {
            val dialogBinding = CustomGamePopUpBinding.inflate(layoutInflater)

            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle("Custom Game")
            builder.setView(dialogBinding.root)

            val dialog = builder.create()

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.btnCreateParty.setOnClickListener {
                userViewModel.currentUser.observe(this@LandingPage) { user ->
                    if (user == null) {
                        return@observe
                    }
                    partyViewModel.createParty(user.id)
                    partyViewModel.createPartyResult.observe(this@LandingPage) {result ->
                        if (result.length == 6 ){
                            partyMemberViewModel.joinParty(result, user.id, "create")
                            partyMemberViewModel.joinPartyResult.observe(this@LandingPage) { res ->
                                if ( res ==  "Create party success" ) {
                                    val intent = Intent(binding.root.context, WaitingRoomPage::class.java)
                                    intent.putExtra("partyCode", result)
                                    dialogBinding.etPartyCode.text = null
                                    dialog.dismiss()
                                    startActivity(intent)
                                } else {
                                    Utils.showSnackBar(binding.root, partyMemberViewModel.joinPartyResult.value!!,true)
                                }
                            }
                        }else {
                            Utils.showSnackBar(binding.root, result,true)
                        }
                    }

                }
            }

            dialogBinding.btnJoinParty.setOnClickListener {
                userViewModel.currentUser.observe(this@LandingPage) { user ->
                    if (user == null) return@observe
                    val partyCode = dialogBinding.etPartyCode.text.toString()
                    partyMemberViewModel.joinParty(partyCode, user.id, "join")
                    partyMemberViewModel.joinPartyResult.observe(this@LandingPage) { res ->
                        Log.e("JOIN PARTY", res)
                        if ( res ==  "Joined party success" ) {
                            val intent = Intent(binding.root.context, WaitingRoomPage::class.java)
                            intent.putExtra("partyCode", partyCode)
                            dialogBinding.etPartyCode.text = null
                            dialog.dismiss()
                            startActivity(intent)
                        } else {
                            Utils.showSnackBar(binding.root, partyMemberViewModel.joinPartyResult.value!!,true)
                        }
                    }
                }

            }

            dialog.show()
        }

    }
}