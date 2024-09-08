package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityCustomQuizBinding
import edu.bluejack24_1.mysticvine.databinding.ActivityResultBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.QuizResultViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel


class ResultPage : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var quizResultViewModel: QuizResultViewModel
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resultId = intent.getStringExtra("resultId") ?: return
        val quizId = intent.getStringExtra("quizId") ?: return


        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        quizResultViewModel = ViewModelProvider(this)[QuizResultViewModel::class.java]
        userViewModel.currentUser.observe(this) { user ->
            if (user == null) return@observe

            quizResultViewModel.getQuizResult(quizId,user.id, resultId)
            quizResultViewModel.quizResult.observe(this) { quizResult ->
                binding.finalScore.text = quizResult.score.toString()
                binding.rightAnswers.text = quizResult.correct.toString()
                binding.wrongAnswers.text = quizResult.wrong.toString()
                binding.addExp.text = (quizResult.score + quizResult.extraExp).toString()
                if (quizResult.score > 0 && quizResult.extraExp > 0) {
                    binding.expMultiplier.text = "x2"
                } else if (quizResult.score < 0 && quizResult.extraExp > 0) {
                    binding.expMultiplier.text = "/2"
                } else {
                    binding.expMultiplier.text = ""
                }

                binding.addCoin.text = (quizResult.coin + quizResult.extraCoin).toString()
                if (quizResult.coin > 0 && quizResult.extraCoin > 0) {
                    binding.coinMultiplier.text = "x2"
                } else {
                    binding.coinMultiplier.text = ""
                }

            }


            Glide.with(binding.profilePicture)
                .load(user.profilePicture.toUri())
                .into(binding.profilePicture)

            binding.username.text = user.username
            binding.level.text = "Lv. ${user.level}"
            binding.score.text = user.score.toString()
            binding.level.text = "Lv. ${user.level}"
            Log.e("nextLevel", Utils.getExpForLevel(user.level).toString())
            binding.currentExp.text = user.exp.toString()
            binding.nextExp.text = Utils.getExpForLevel(user.level).toString()
            binding.levelText.text = "${user.level}"
            binding.progressLvl.progress = user.exp
            binding.progressLvl.max = Utils.getExpForLevel(user.level)
            binding.coin.text = user.coin.toString()

            binding.backToHome.setOnClickListener {
                finish()
            }


            userViewModel.allLeaderboard.observe(this) { leaderboard ->
                val rank = leaderboard.indexOfFirst { it.id == user.id } + 1
                binding.rank.text = "$rank. "

            }



        }

        binding.closeButton.setOnClickListener {
            val intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
            finish()
        }
    }
}