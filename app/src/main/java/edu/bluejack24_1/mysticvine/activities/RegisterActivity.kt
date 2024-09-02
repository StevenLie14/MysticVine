package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.bluejack24_1.mysticvine.utils.Utils
import edu.bluejack24_1.mysticvine.databinding.ActivityRegisterBinding
import edu.bluejack24_1.mysticvine.viewmodel.UserViewModel

class RegisterPage : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        Utils.loggedInMiddleware(userViewModel, this)

        binding.btnRegister.setOnClickListener {

            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            binding.progressBar.visibility = View.VISIBLE
            userViewModel.register(username, email, password, confirmPassword)

            binding.etUsername.text = null
            binding.etEmail.text = null
            binding.etPassword.text = null
            binding.etConfirmPassword.text = null
        }

        binding.linkLogin.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }

        userViewModel.registerResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            if (result == "Register Success") {
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
                finish()
            } else {
                Utils.showSnackBar(binding.root, result)
            }
        }
    }
}
