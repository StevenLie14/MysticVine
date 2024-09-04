package edu.bluejack24_1.mysticvine.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.bluejack24_1.mysticvine.R
import edu.bluejack24_1.mysticvine.databinding.ActivityCustomAnswerQuizBinding


class CustomAnswerQuizPage : AppCompatActivity() {

    private lateinit var binding: ActivityCustomAnswerQuizBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomAnswerQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}