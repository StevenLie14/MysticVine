package edu.bluejack24_1.mysticvine.activities

import android.annotation.SuppressLint
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
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityFlashCardBinding
import edu.bluejack24_1.mysticvine.databinding.ActivityLoginBinding
import edu.bluejack24_1.mysticvine.viewmodel.FlashCardViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel


class FlashCardPage : AppCompatActivity() {

    private lateinit var binding: ActivityFlashCardBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var flashCardViewModel: FlashCardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        flashCardViewModel = ViewModelProvider(this)[FlashCardViewModel::class.java]
        var id = 0;

        binding.backToHome.setOnClickListener {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
            finish()
        }

        binding.closeButton.setOnClickListener {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
            finish()
        }

        userViewModel.currentUser.observe(this) { user ->
            if (user == null) return@observe
            Glide.with(binding.ivAvatar)
                .load(user.profilePicture)
                .into(binding.ivAvatar)
            flashCardViewModel.daily.observe(this) { daily ->
                if (daily == null || daily.isEmpty()) return@observe
                if (id <= daily.size - 1) {
                    val card = daily[id]
                    binding.tvQuestion.text = card.question
                    binding.questionProgress.max = daily.size
                    binding.questionProgress.progress = id + 1
                    binding.tvQuestionNumber.text = "${id + 1}/${daily.size} "
                }else{
                    binding.tvQuestion.text = getString(R.string.finish_flash_card)
                    binding.rememberButton.visibility = View.GONE
                    binding.forgotButton.visibility = View.GONE
                    binding.backToHome.visibility = View.VISIBLE
                }

                binding.rememberButton.setOnClickListener {
                    val card = daily[id]
                    flashCardViewModel.updateFlashCardResult(card, true)
                    id++
                }
                binding.forgotButton.setOnClickListener {
                    val card = daily.get(id)
                    flashCardViewModel.updateFlashCardResult(card, false)
                    id++
                }
            }
        }
    }
}