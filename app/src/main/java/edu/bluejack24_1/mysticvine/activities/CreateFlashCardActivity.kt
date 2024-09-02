package edu.bluejack24_1.mysticvine.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityCreateFlashCardBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.FlashCardViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel


class CreateFlashCardPage : AppCompatActivity() {

    private lateinit var binding: ActivityCreateFlashCardBinding
    private lateinit var flashCardViewModel: FlashCardViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateFlashCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        flashCardViewModel = ViewModelProvider(this).get(FlashCardViewModel::class.java)

        userViewModel.currentUser.observe(this) { user ->
            if (user == null) return@observe

            Glide.with(binding.ivAvatar)
                .load(user.profilePicture)
                .into(binding.ivAvatar)

            binding.btnCreateCard.setOnClickListener {
                val question = binding.etQuestion.text.toString()
                val answer = binding.etAnswer.text.toString()
                binding.progressBar.visibility = View.VISIBLE
                flashCardViewModel.createFlashCard(question, answer, user.id)
            }
        }

        binding.ivClose.setOnClickListener {
            Utils.backButton(this)
        }

        flashCardViewModel.flashCardsResult.observe(this) { result ->
            if (result == "Flashcard created") {
                binding.etQuestion.text = null
                binding.etAnswer.text = null
                Utils.showSnackBar(binding.root, result,false)
            }else {
                Utils.showSnackBar(binding.root, result,true)
            }
            binding.progressBar.visibility = View.GONE
        }




    }
}