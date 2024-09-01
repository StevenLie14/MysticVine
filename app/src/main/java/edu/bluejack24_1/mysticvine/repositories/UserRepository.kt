package edu.bluejack24_1.mysticvine.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.utils.SharedPrefUtils

class UserRepository (context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    private val sharedPrefUtils = SharedPrefUtils(context)

    fun login(email: String, password: String, callback: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userRef = db.getReference("users").child(auth.currentUser!!.uid)

                userRef.get().addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        try {
                            val user = dataSnapshot.getValue(Users::class.java)
                            sharedPrefUtils.saveData(SharedPrefUtils.CURRENT_USER, user)
                            callback("Login Success")
                        } catch (e: Exception) {
                            callback("Error parsing user data")
                        }
                    } else {
                        callback("No user data found")
                    }
                }.addOnFailureListener { exception ->
                    callback(exception.message ?: "Failed to get user data")
                }
            } else {
                callback(task.exception?.message ?: "Login Failed")
            }
        }
    }

    fun register(username: String, email: String, password: String, callback: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userRef = db.reference.child("users").child(auth.currentUser!!.uid)
                val user = Users(auth.currentUser!!.uid, username, email, "tes" , 1, 0, 0, 0)
                userRef.setValue(user).addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback("Register Success")
                    } else {
                        callback(it.exception?.message ?: "Failed to save user data")
                    }
                }
            } else {
                callback(task.exception?.message ?: "Register Failed")
            }
        }
    }

    fun logout() {
        sharedPrefUtils.removeData(SharedPrefUtils.CURRENT_USER)
        auth.signOut()
    }

    fun getCurrentUser(): Users? {
        return sharedPrefUtils.loadData(SharedPrefUtils.CURRENT_USER, Users::class.java)
    }


    fun getLeaderBoard(callback: (List<Users>) -> Unit) {
        val userRef = db.getReference("users")
        userRef.orderByChild("score").orderByChild("level").get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val leaderBoardData = ArrayList<Users>()
                for (data in dataSnapshot.children) {
                    val user = data.getValue(Users::class.java)
                    user?.let { leaderBoardData.add(it) }
                }
                callback(leaderBoardData)
            } else {
                Log.d("LeaderBoard", "No data found")
                callback(emptyList())
            }
        }.addOnFailureListener { exception ->
            Log.d("LeaderBoard", exception.message ?: "Failed to get leader board data")
            callback(emptyList())
        }
    }
}