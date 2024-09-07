package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.util.Log
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
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.adapters.LandingLeaderBoardAdapter
import edu.bluejack24_1.mysticvine.adapters.QuizzesAdapter
import edu.bluejack24_1.mysticvine.adapters.Random3QuizAdapter
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




    private val rotateOpen by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom by lazy { AnimationUtils.loadAnimation(this, R.anim.top_bottom_anim) }



    private var clicked = false

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
            Glide.with(binding.profilePicture)
                .load(user.profilePicture.toUri())
                .into(binding.profilePicture)
        }

        val random3QuizAdapter = Random3QuizAdapter()
        binding.rvRandom3Quiz.adapter = random3QuizAdapter
        binding.rvRandom3Quiz.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRandom3Quiz.setHasFixedSize(true)
        quizViewModel.random3Quiz.observe(this) {
            random3QuizAdapter.updateList(it)
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

        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)

        val quizAdapter = QuizzesAdapter()

        binding.rvQuizCard.adapter = quizAdapter
        binding.rvQuizCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        quizViewModel.allQuizzes.observe(this) { quizzes ->
            quizAdapter.updateList(quizzes)
        }



        binding.highscore.setOnClickListener{
            val intent = Intent(this, LeaderBoardPage::class.java)
            startActivity(intent)
        }
        binding.sortFab.setOnClickListener{

            setVisibility(clicked, binding)
            setAnimation(clicked, binding)
            setClickable(clicked, binding)
            clicked = !clicked

        }
        binding.profile.setOnClickListener{
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
        }

        binding.shop.setOnClickListener{
            val intent = Intent(this, StorePage::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener{
            val intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
        }
    }


    private fun setVisibility(clicked: Boolean, binding: ActivityLandingBinding){
        if(!clicked){
            binding.home.visibility = View.VISIBLE
            binding.shop.visibility = View.VISIBLE
            binding.profile.visibility = View.VISIBLE
            binding.highscore.visibility = View.VISIBLE
        } else {
            binding.home.visibility = View.GONE
            binding.shop.visibility = View.GONE
            binding.highscore.visibility = View.GONE
            binding.profile.visibility = View.GONE
        }
    }
    private fun setAnimation(clicked: Boolean, binding: ActivityLandingBinding){
        if(!clicked){
            binding.home.startAnimation(fromBottom)
            binding.shop.startAnimation(fromBottom)
            binding.profile.startAnimation(fromBottom)
            binding.highscore.startAnimation(fromBottom)
            binding.sortFab.startAnimation(rotateOpen)
        } else {
            binding.home.startAnimation(toBottom)
            binding.shop.startAnimation(toBottom)
            binding.profile.startAnimation(toBottom)
            binding.highscore.startAnimation(toBottom)
            binding.sortFab.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean, binding: ActivityLandingBinding){
        if(!clicked){
            binding.home.isClickable = true
            binding.highscore.isClickable = true
            binding.shop.isClickable = true
            binding.profile.isClickable = true
        } else {
            binding.home.isClickable = false
            binding.shop.isClickable = false
            binding.highscore.isClickable = false
            binding.profile.isClickable = false
        }
    }
}