package edu.bluejack24_1.mysticvine.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import edu.bluejack24_1.mysticvine.R
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
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
                binding.etEmail.text = null
                binding.etPassword.text = null
                val intent = Intent(this, LandingPage::class.java)
                startActivity(intent)
                finish()
            } else {
                Utils.showSnackBar(binding.root, result)
            }
        }



        val biometricManager = androidx.biometric.BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {

                if (userViewModel.isLoggedIn()) {
                    binding.ivFingerprint.visibility = View.VISIBLE
                    binding.llFingerprint.visibility = View.VISIBLE
                }else{
                    binding.ivFingerprint.visibility = View.GONE
                    binding.llFingerprint.visibility = View.GONE
                }

                var executor = ContextCompat.getMainExecutor(this)
                var biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Utils.showSnackBar(binding.root, "Authentication error: $errString")
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
//                        Utils.showSnackBar(binding.root, "Authentication succeeded!")
                        Log.d("Fingerprint", userViewModel.isLoggedIn().toString())
                        if (userViewModel.isLoggedIn()) {
                            val intent = Intent(this@LoginPage, ProfilePage::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Utils.showSnackBar(binding.root, getString(R.string.biometric_auth_failed))
                    }
                })
                var promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_prompt_title))
                    .setSubtitle(getString(R.string.biometric_prompt_desc))
                    .setNegativeButtonText(getString(R.string.cancel))
                    .build()

                binding.ivFingerprint.setOnClickListener() {
                    biometricPrompt.authenticate(promptInfo)
                }
            }
            else -> {
                binding.llFingerprint.visibility = View.GONE
                binding.ivFingerprint.visibility = View.GONE
            }
        }

    }
}
