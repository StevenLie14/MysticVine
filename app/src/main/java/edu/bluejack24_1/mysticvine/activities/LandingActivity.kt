package edu.bluejack24_1.mysticvine.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        Utils.guestMiddleware(userViewModel, this)
        userViewModel.getCurrentUser()
        userViewModel.currentUser.observe(this) { user ->
            binding.welcomeText.text = "Welcome, ${user?.username}"
            binding.coinText.text = user?.coin.toString()
            binding.levelText.text = user?.level.toString()
            binding.levelProgress.progress = user?.exp ?: 0
            binding.levelProgress.max = Utils.getExpForLevel(user?.level ?: 0)
        }

        val leaderBoardAdapter = LandingLeaderBoardAdapter()
        binding.rvLeaderboard.adapter = leaderBoardAdapter
        binding.rvLeaderboard.layoutManager = GridLayoutManager(this, 1)
        binding.rvLeaderboard.setHasFixedSize(true)
        userViewModel.leaderboard.observe(this) {
            leaderBoardAdapter.updateUserList(it)
        }

        userViewModel.getLeaderboard()










    }
}