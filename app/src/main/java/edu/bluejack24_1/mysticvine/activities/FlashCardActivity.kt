package edu.bluejack24_1.mysticvine.activities

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityFlashCardBinding
import edu.bluejack24_1.mysticvine.viewmodel.FlashCardViewModel
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel

class FlashCardPage : AppCompatActivity() {

    private var isFront = true
    private lateinit var frontView: View
    private lateinit var backView: View
    private lateinit var frontAnimator: AnimatorSet
    private lateinit var backAnimator: AnimatorSet
    private lateinit var binding: ActivityFlashCardBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var flashCardViewModel: FlashCardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        frontView = binding.front
        backView = binding.back

        loadAnimations()

        binding.toggle.setOnClickListener {
            flipCardAnimation()
        }



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
                    binding.front.text = card.question
                    binding.back.text = card.answer
                    binding.questionProgress.max = daily.size
                    binding.questionProgress.progress = id + 1
                    binding.tvQuestionNumber.text = "${id + 1}/${daily.size} "
                } else {
                    binding.front.text = getString(R.string.finish_flash_card)
                    binding.back.text = getString(R.string.finish_flash_card)
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

    private fun loadAnimations() {
        frontAnimator = AnimatorInflater.loadAnimator(this, R.animator.card_flip_out_anim) as AnimatorSet
        backAnimator = AnimatorInflater.loadAnimator(this, R.animator.card_flip_in_anim) as AnimatorSet
    }

    private fun flipCardAnimation() {
        val visibilityListener = object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                binding.toggle.isClickable = false
            }
            override fun onAnimationEnd(animation: Animator) {
                binding.toggle.isClickable = true
            }
        }

        if (isFront) {
            frontAnimator.setTarget(frontView)
            backAnimator.setTarget(backView)
            frontView.visibility = View.INVISIBLE
            backView.visibility = View.VISIBLE
            frontAnimator.addListener(visibilityListener)
            backAnimator.addListener(visibilityListener)

            frontAnimator.start()
            backAnimator.start()
        } else {
            frontAnimator.setTarget(backView)
            backAnimator.setTarget(frontView)
            backView.visibility = View.INVISIBLE
            frontView.visibility = View.VISIBLE
            frontAnimator.addListener(visibilityListener)
            backAnimator.addListener(visibilityListener)

            backAnimator.start()
            frontAnimator.start()
        }

        isFront = !isFront
    }
}
