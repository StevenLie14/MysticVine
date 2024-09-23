package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.repositories.UserRepository
import edu.bluejack24_1.mysticvine.utils.Utils

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository = UserRepository(application)


    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> = _loginResult

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginResult.value = "Please fill all the fields"
            return
        }
        userRepository.login(email, password) { result ->
            _loginResult.value = result
            userRepository.getCurrentUser(_currentUser)
        }
    }

    private val _registerResult = MutableLiveData<String>()
    val registerResult: LiveData<String> = _registerResult

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _registerResult.value = "Please fill all the fields"
            return
        }

        if (username.length > 8) {
            _registerResult.value = "Username must be less than 8 characters"
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

    private val _currentUser = MutableLiveData<Users?>()
    val currentUser: LiveData<Users?> = _currentUser

    fun isLoggedIn(): Boolean {
        return userRepository.isLoggedIn()
    }

    private val _editProfilePicResult = MutableLiveData<String>()
    val editProfilePicResult: LiveData<String> = _editProfilePicResult
    fun editProfilePic(uri: Uri) {
        userRepository.editProfilePicture(uri) { result ->
            _editProfilePicResult.value = result
        }
    }

    private val _editUsernameResult = MutableLiveData<String>()
    val editUsernameResult: LiveData<String> = _editUsernameResult
    fun editUsername(username: String) {
        userRepository.editUsername(username) { result ->
            _editUsernameResult.value = result
        }
    }

    private val _boosterResult = MutableLiveData<String>()
    val boosterResult: LiveData<String> = _boosterResult
    fun addCoinBooster() {
        userRepository.updateCoinBooster() { result ->
            _boosterResult.value = result
        }
    }

    fun getUserById(userId: String, callback: (Users) -> Unit) {
        userRepository.getUserById(userId) {
            callback(it)
        }
    }

    fun addExpBooster() {
        userRepository.updateExpBooster { result ->
            _boosterResult.value = result
        }
    }

    fun addShieldBooster() {
        userRepository.updateShieldBooster { result ->
            _boosterResult.value = result
        }
    }

    fun deleteItem(type: Int, users: Users, callback: (Int) -> Unit) {
        when (type) {
            1 -> {
                var user = Users(
                    users.id,
                    users.username,
                    users.email,
                    users.profilePicture,
                    users.level,
                    users.exp,
                    users.coin,
                    users.score,
                    users.coinBooster -1,
                    users.expBooster,
                    users.shieldBooster
                )
                userRepository.updateUser(user) { code, _ ->
                    callback(code)
                }
            }

            2 -> {
                var user = Users(
                    users.id,
                    users.username,
                    users.email,
                    users.profilePicture,
                    users.level,
                    users.exp,
                    users.coin,
                    users.score,
                    users.coinBooster,
                    users.expBooster - 1,
                    users.shieldBooster
                )
                userRepository.updateUser(user) { code, _ ->
                    callback(code)
                }
            }

            3 -> {
                var user = Users(
                    users.id,
                    users.username,
                    users.email,
                    users.profilePicture,
                    users.level,
                    users.exp,
                    users.coin,
                    users.score,
                    users.coinBooster,
                    users.expBooster,
                    users.shieldBooster -1
                )
                userRepository.updateUser(user) { code, _ ->
                    callback(code)
                }
            }
        }
    }

    fun updateStatus(users: Users, score: Int, coin: Int, callback: (Int) -> Unit) {
        var level = users.level
        var exp = users.exp
        var userScore = users.score
        var remainingScore = score

        while (exp + remainingScore >= Utils.getExpForLevel(level)) {
            val nextExp = Utils.getExpForLevel(level)
            remainingScore -= (nextExp - exp)
            level += 1
            exp = 0
        }

        while (exp + remainingScore < 0 && level > 1) {
            remainingScore += exp
            level -= 1
            exp = Utils.getExpForLevel(level)
        }

        exp += remainingScore

        if (exp < 0 && level <= 1) {
            level = 1
            exp = 0
        }

        userScore = if (userScore + score < 0) 0 else userScore + score

        val user = Users(
            users.id,
            users.username,
            users.email,
            users.profilePicture,
            level,
            exp,
            users.coin + coin,
            userScore,
            users.coinBooster,
            users.expBooster,
            users.shieldBooster
        )

        userRepository.updateUser(user) { code, _ ->
            callback(code)
        }
    }

    fun logout() {
        userRepository.logout()
    }



    private val _leaderboardPageAfter4 = MutableLiveData<List<Users>>()
    val leaderboardPageAfter4: LiveData<List<Users>> = _leaderboardPageAfter4

    private val _leaderboard = MutableLiveData<List<Users>>()
    val leaderboard: LiveData<List<Users>> = _leaderboard

    private val _allLeaderboard = MutableLiveData<List<Users>>()
    val allLeaderboard: LiveData<List<Users>> = _allLeaderboard

    init {
        userRepository.getLandingLeaderBoard(_leaderboard)
        userRepository.getCurrentUser(_currentUser)
        userRepository.getAllLeaderBoard(_allLeaderboard)
        userRepository.getLeaderBoardAfter4ranks(_leaderboardPageAfter4)
    }


}
