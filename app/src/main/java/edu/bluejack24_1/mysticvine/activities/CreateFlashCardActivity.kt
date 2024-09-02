package edu.bluejack24_1.mysticvine.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityCreateFlashCardBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.FlashCardViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel


class CreateFlashCardPage : AppCompatActivity() {

    private lateinit var binding: ActivityCreateFlashCardBinding
    private lateinit var flashCardviewModel: FlashCardViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateFlashCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        flashCardviewModel = ViewModelProvider(this).get(FlashCardViewModel::class.java)

        userViewModel.currentUser.observe(this) { user ->
            if (user == null) return@observe

            binding.btnCreateCard.setOnClickListener {
                val question = binding.etQuestion.text.toString()
                val answer = binding.etAnswer.text.toString()
                flashCardviewModel.createFlashCard(question, answer, user.id)
                binding.etQuestion.text.clear()
                binding.etAnswer.text.clear()
            }
        }

        flashCardviewModel.flashCardsResult.observe(this) { result ->
            if (result == "Flashcard created") {
                Utils.showSnackBar(binding.root, "Flashcard created")
            }else {
                Utils.showSnackBar(binding.root, "Failed to create flashcard")
            }
        }




    }
}