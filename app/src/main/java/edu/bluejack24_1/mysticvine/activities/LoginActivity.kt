package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.bluejack24_1.mysticvine.databinding.ActivityLoginBinding
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel

class LoginPage : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        Utils.loggedInMiddleware(userViewModel, this)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            binding.progressBar.visibility = View.VISIBLE
            userViewModel.login(email, password)
        }

        binding.linkRegister.setOnClickListener {
            val intent = Intent(this, RegisterPage::class.java)
            startActivity(intent)
            finish()
        }

        userViewModel.loginResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            if (result == "Login Success") {
                val intent = Intent(this, LandingPage::class.java)
                startActivity(intent)
                finish()
            } else {
                Utils.showSnackBar(binding.root, result)
            }
        }
    }
}
