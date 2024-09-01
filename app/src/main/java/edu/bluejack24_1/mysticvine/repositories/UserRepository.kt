package edu.bluejack24_1.mysticvine.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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


    fun getLeaderBoard(userList : MutableLiveData<List<Users>>) {
        val userRef = db.getReference("users")
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val userDB : List<Users> = snapshot.children.map { it.getValue(Users::class.java)!! }
                    userList.postValue(userDB)
                }catch (e: Exception){
                    Log.e("UserRepository", "Error parsing user data")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserRepository", error.message)
            }

        })
    }
}