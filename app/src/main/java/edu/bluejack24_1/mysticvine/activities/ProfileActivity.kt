package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageTask
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.adapters.FlashCardAdapter
import edu.bluejack24_1.mysticvine.databinding.ActivityProfilePageBinding
import edu.bluejack24_1.mysticvine.databinding.ActivityRegisterBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.FlashCardViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel


class ProfilePage : AppCompatActivity() {

    private lateinit var binding: ActivityProfilePageBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var flashCardViewModel: FlashCardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editProfilePic.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.currentUser.observe(this) { user ->
            Log.e("user", user.toString())
            if (user == null) return@observe
            Glide.with(binding.editProfilePic)
                .load(user.profilePicture.toUri())
                .into(binding.editProfilePic)
            binding.userName.text = user.username
            binding.editUserName.setText(user.username)
            binding.tvLevel.text = user.level.toString()
            binding.exp.text = user.exp.toString()
            binding.nextExp.text = Utils.getExpForLevel(user.level).toString()
            binding.tvCoin.text = user.coin.toString()
            binding.progressLevel.max = Utils.getExpForLevel(user.level)
            binding.progressLevel.progress = user.exp
        }

        binding.editIcon.setOnClickListener(){
            if (binding.userName.visibility == View.VISIBLE){
                binding.userName.visibility = View.GONE
                binding.editUserName.visibility = View.VISIBLE
            } else {
                binding.userName.visibility = View.VISIBLE
                binding.editUserName.visibility = View.GONE
                userViewModel.editUsername(binding.editUserName.text.toString())
            }
        }

        binding.cardAddFlashCard.setOnClickListener(){
            val intent = Intent(this, CreateFlashCardPage::class.java)
            startActivity(intent)
        }


        userViewModel.editProfilePicResult.observe(this) { result ->
            if (result == "Edit Profile Picture Success") {
                Utils.showSnackBar(binding.root, result)
            } else {
                Utils.showSnackBar(binding.root, result,true)
            }
        }

        flashCardViewModel = ViewModelProvider(this).get(FlashCardViewModel::class.java)
        val flashCardAdapter = FlashCardAdapter(flashCardViewModel)
        binding.rvFlashCard.adapter = flashCardAdapter
        binding.rvFlashCard.setHasFixedSize(true)
        binding.rvFlashCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        flashCardViewModel.flashcards.observe(this) {
            flashCardAdapter.updateList(it)
        }

        flashCardViewModel.deleteFlashCardResult.observe(this) { result ->
            if (result == "Flashcard deleted") {
                Utils.showSnackBar(binding.root, result, false)
            } else {
                Utils.showSnackBar(binding.root, result, true)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            val uri = data.data

            userViewModel.editProfilePic(uri!!)
        }
    }
}