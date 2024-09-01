package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack24_1.mysticvine.activities.LandingPage
import edu.bluejack24_1.mysticvine.activities.LoginPage
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository : UserRepository = UserRepository(application);


    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> = _loginResult

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginResult.value = "Please fill all the fields"
            return
        }

        userRepository.login(email, password) { result ->
            _loginResult.value = result
        }
    }

    private val _registerResult = MutableLiveData<String>()
    val registerResult: LiveData<String> = _registerResult

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _registerResult.value = "Please fill all the fields"
            return
        }

        if (password != confirmPassword) {
            _registerResult.value = "Password and Confirm Password must be the same"
            return
        }

        userRepository.register(username, email, password) { result ->
            _registerResult.value = result
        }
    }

    private val _currentUser = MutableLiveData<Users>()
    val currentUser: LiveData<Users> = _currentUser
    fun getCurrentUser() {
        _currentUser.value = userRepository.getCurrentUser()
    }

    fun isLoggedIn() : Boolean {
        return userRepository.getCurrentUser() != null
    }



    private val _leaderboard = MutableLiveData<List<Users>>()
    val leaderboard: LiveData<List<Users>> = _leaderboard

    init {
        userRepository.getLeaderBoard(_leaderboard)
    }



}
