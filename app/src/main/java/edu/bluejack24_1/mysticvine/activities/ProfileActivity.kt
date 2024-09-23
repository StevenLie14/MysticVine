package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
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
import edu.bluejack24_1.mysticvine.adapters.QuizzesAdapter
import edu.bluejack24_1.mysticvine.databinding.ActivityLandingBinding
import edu.bluejack24_1.mysticvine.databinding.ActivityProfilePageBinding
import edu.bluejack24_1.mysticvine.databinding.ActivityRegisterBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.FlashCardViewModel
import edu.bluejack24_1.mysticvine.viewmodel.QuestionViewModel
import edu.bluejack24_1.mysticvine.viewmodel.QuizResultViewModel
import edu.bluejack24_1.mysticvine.viewmodel.QuizViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel


class ProfilePage : AppCompatActivity() {

    private lateinit var binding: ActivityProfilePageBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var flashCardViewModel: FlashCardViewModel
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var quizResultViewModel: QuizResultViewModel

    private val rotateOpen by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom by lazy { AnimationUtils.loadAnimation(this, R.anim.top_bottom_anim) }
    private var clicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editProfilePic.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        quizResultViewModel = ViewModelProvider(this)[QuizResultViewModel::class.java]
        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)

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
            binding.coinBoosterCount.text = user.coinBooster.toString() + "x"
            binding.expBoosterCount.text = user.expBooster.toString() + "x"
            binding.shieldBoosterCount.text = user.shieldBooster.toString() + "x"

            if(user.coinBooster != 0){
                binding.coinBooster.visibility = View.VISIBLE
            }

            if(user.expBooster != 0){
                binding.expBooster.visibility = View.VISIBLE
            }

            if(user.shieldBooster != 0){
                binding.shieldBooster.visibility = View.VISIBLE
            }

            val quizAdapter = QuizzesAdapter() { quizId, creatorId ->
                quizResultViewModel.createQuizResult(quizId, user.id) { code, message ->
                    if (code == 200) {
                        val intent = Intent(this, QuestionPage::class.java)
                        intent.putExtra("quizId", quizId)
                        intent.putExtra("resultId", message)
                        intent.putExtra("creatorId", creatorId)
                        startActivity(intent)
                    } else {
                        Utils.showSnackBar(binding.root, message,true)
                    }

                }
            }
            binding.rvQuizCard.adapter = quizAdapter
            binding.rvQuizCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            quizViewModel.userQuizzes.observe(this){
                quizAdapter.updateList(it)
            }

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
            if (result == "Profile Picture Updated") {
                Utils.showSnackBar(binding.root, result, false)
            } else {
                Utils.showSnackBar(binding.root, result,true)
            }
        }

        userViewModel.editUsernameResult.observe(this) { result ->
            if (result == "Username Updated") {
                Utils.showSnackBar(binding.root, result, false)
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

        binding.createQuiz.setOnClickListener(){
            val intent = Intent(this, CreateQuizPage::class.java)
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

        binding.startFlash.setOnClickListener {
            val intent = Intent(this, FlashCardPage::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener {
            userViewModel.logout()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }

        flashCardViewModel.daily.observe(this) {result ->
            if (result.isNotEmpty()) {
                binding.rememberFlash.visibility = View.VISIBLE
            } else {
                binding.rememberFlash.visibility = View.GONE
            }
        }

        binding.highscore.setOnClickListener{
            val intent = Intent(this, LeaderBoardPage::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            val uri = data.data

            userViewModel.editProfilePic(uri!!)
        }
    }




    private fun setVisibility(clicked: Boolean, binding: ActivityProfilePageBinding){
        if(!clicked){
            binding.home.visibility = View.VISIBLE
            binding.shop.visibility = View.VISIBLE
            binding.profile.visibility = View.VISIBLE
            binding.highscore.visibility = View.VISIBLE
            binding.logout.visibility = View.VISIBLE
        } else {
            binding.home.visibility = View.GONE
            binding.shop.visibility = View.GONE
            binding.highscore.visibility = View.GONE
            binding.profile.visibility = View.GONE
            binding.logout.visibility = View.GONE
        }
    }
    private fun setAnimation(clicked: Boolean, binding: ActivityProfilePageBinding){
        if(!clicked){
            binding.home.startAnimation(fromBottom)
            binding.shop.startAnimation(fromBottom)
            binding.profile.startAnimation(fromBottom)
            binding.highscore.startAnimation(fromBottom)
            binding.logout.startAnimation(fromBottom)
            binding.sortFab.startAnimation(rotateOpen)
        } else {
            binding.home.startAnimation(toBottom)
            binding.shop.startAnimation(toBottom)
            binding.profile.startAnimation(toBottom)
            binding.highscore.startAnimation(toBottom)
            binding.logout.startAnimation(toBottom)
            binding.sortFab.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean, binding: ActivityProfilePageBinding){
        if(!clicked){
            binding.home.isClickable = true
            binding.highscore.isClickable = true
            binding.shop.isClickable = true
            binding.profile.isClickable = true
            binding.logout.isClickable = true
        } else {
            binding.home.isClickable = false
            binding.shop.isClickable = false
            binding.highscore.isClickable = false
            binding.profile.isClickable = false
            binding.logout.isClickable = false
        }
    }
}