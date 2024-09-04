package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.adapters.LandingLeaderBoardAdapter
import edu.bluejack24_1.mysticvine.databinding.ActivityLandingBinding
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.QuizViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel

class LandingPage : AppCompatActivity() {

    private  lateinit var  binding : ActivityLandingBinding
    private  lateinit var quizViewModel : QuizViewModel
    private  lateinit var userViewModel: UserViewModel




    private val rotateOpen by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom by lazy { AnimationUtils.loadAnimation(this, R.anim.top_bottom_anim) }



    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        Utils.guestMiddleware(userViewModel, this)
        userViewModel.currentUser.observe(this) { user ->
            if (user == null) return@observe
            binding.welcomeText.text = "Welcome, ${user?.username}"
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

        binding.sortFab.setOnClickListener{

            setVisibility(clicked, binding)
            setAnimation(clicked, binding)
            setClickable(clicked, binding)
            clicked = !clicked

        }

        binding.profile.setOnClickListener{
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
            finish()
        }

        binding.shop.setOnClickListener{
            val intent = Intent(this, StorePage::class.java)
            startActivity(intent)
            finish()
        }

        binding.home.setOnClickListener{
            val intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun setVisibility(clicked: Boolean, binding: ActivityLandingBinding){
        if(!clicked){
            binding.home.visibility = View.VISIBLE
            binding.shop.visibility = View.VISIBLE
            binding.profile.visibility = View.VISIBLE
        } else {
            binding.home.visibility = View.GONE
            binding.shop.visibility = View.GONE
            binding.profile.visibility = View.GONE
        }
    }
    private fun setAnimation(clicked: Boolean, binding: ActivityLandingBinding){
        if(!clicked){
            binding.home.startAnimation(fromBottom)
            binding.shop.startAnimation(fromBottom)
            binding.profile.startAnimation(fromBottom)
            binding.sortFab.startAnimation(rotateOpen)
        } else {
            binding.home.startAnimation(toBottom)
            binding.shop.startAnimation(toBottom)
            binding.profile.startAnimation(toBottom)
            binding.sortFab.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean, binding: ActivityLandingBinding){
        if(!clicked){
            binding.home.isClickable = true
            binding.shop.isClickable = true
            binding.profile.isClickable = true
        } else {
            binding.home.isClickable = false
            binding.shop.isClickable = false
            binding.profile.isClickable = false
        }
    }
}