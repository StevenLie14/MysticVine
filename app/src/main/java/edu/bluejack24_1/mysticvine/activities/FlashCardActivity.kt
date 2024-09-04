package edu.bluejack24_1.mysticvine.activities

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack24_1.mysticvine.R

class FlashCardPage : AppCompatActivity() {

    private var isFront = true
    private lateinit var frontView: View
    private lateinit var backView: View
    private lateinit var frontAnimator: AnimatorSet
    private lateinit var backAnimator: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_flash_card)

        frontView = findViewById(R.id.front)
        backView = findViewById(R.id.back)

        loadAnimations()

        findViewById<FrameLayout>(R.id.toggle).setOnClickListener {
            flipCardAnimation()
        }
    }

    private fun loadAnimations() {
        frontAnimator = AnimatorInflater.loadAnimator(this, R.animator.card_flip_out_anim) as AnimatorSet
        backAnimator = AnimatorInflater.loadAnimator(this, R.animator.card_flip_in_anim) as AnimatorSet
    }

    private fun flipCardAnimation() {
        val visibilityListener = object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                findViewById<FrameLayout>(R.id.toggle).isClickable = false
            }
            override fun onAnimationEnd(animation: Animator) {
                findViewById<FrameLayout>(R.id.toggle).isClickable = true
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
