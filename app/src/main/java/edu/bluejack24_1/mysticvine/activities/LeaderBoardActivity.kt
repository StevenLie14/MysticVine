package edu.bluejack24_1.mysticvine.activities

import LeaderboardAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.databinding.ActivityLeaderboardBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel

class LeaderBoardPage : AppCompatActivity() {
    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]



        userViewModel.leaderboard.observe(this) { userList ->
            if (userList != null) {
                if (userList.isNotEmpty()) {
                    binding.rank1Score.text = userList[0].score.toString()
                    binding.rank1Username.text = userList[0].username
                    Glide.with(binding.rank1Picture)
                        .load(userList[0].profilePicture.toUri())
                        .into(binding.rank1Picture)
                }

                if (userList.size > 1) {
                    binding.rank2Score.text = userList[1].score.toString()
                    binding.rank2Username.text = userList[1].username
                    Glide.with(binding.rank2Picture)
                        .load(userList[1].profilePicture.toUri())
                        .into(binding.rank2Picture)
                }

                if (userList.size > 2) {
                    binding.rank3Score.text = userList[2].score.toString()
                    binding.rank3Username.text = userList[2].username
                    Glide.with(binding.rank3Picture)
                        .load(userList[2].profilePicture.toUri())
                        .into(binding.rank3Picture)
                }
            }
        }

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.currentUser.observe(this) { user ->
            Log.e("user", user.toString())
            if (user == null) return@observe
            Glide.with(binding.profilePicture)
                .load(user.profilePicture.toUri())
                .into(binding.profilePicture)

            val leaderBoardAdapter = LeaderboardAdapter()
            binding.rvLeaderboardRanking.adapter = leaderBoardAdapter
            binding.rvLeaderboardRanking.layoutManager = LinearLayoutManager(this)
            binding.rvLeaderboardRanking.setHasFixedSize(true)

            userViewModel.leaderboardPageAfter4.observe(this) { userList ->
                leaderBoardAdapter.updateUserList(userList)

                var idx = userList.indexOfFirst { it.id == user.id }
                binding.rvLeaderboardRanking.scrollToPosition(idx)
            }
        }

        binding.closeButton.setOnClickListener {
            finish()
        }


    }
}
