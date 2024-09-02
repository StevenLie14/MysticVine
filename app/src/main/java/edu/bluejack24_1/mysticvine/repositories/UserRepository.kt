package edu.bluejack24_1.mysticvine.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.utils.SharedPrefUtils

class UserRepository (context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    private val sharedPrefUtils = SharedPrefUtils(context)
    private val storage = FirebaseStorage.getInstance()

    fun login(email: String, password: String, callback: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userRef = db.getReference("users").child(auth.currentUser!!.uid)

                userRef.get().addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        try {
                            sharedPrefUtils.saveData(SharedPrefUtils.CURRENT_USER, auth.currentUser!!.uid )
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
                val user = Users(auth.currentUser!!.uid, username, email, "https://firebasestorage.googleapis.com/v0/b/mystic-vine-316b2.appspot.com/o/users%2Flogo.png?alt=media&token=1284a3fe-ad3f-4442-a74d-29df730ccdba" , 1, 0, 0, 0)
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

    fun isLoggedIn() : Boolean {
        return sharedPrefUtils.loadData(SharedPrefUtils.CURRENT_USER, String::class.java) != null
    }

    fun getCurrentUser(users : MutableLiveData<Users?>)  {
        var uid = sharedPrefUtils.loadData(SharedPrefUtils.CURRENT_USER, String::class.java)
        if (uid != null) {
            val userRef = db.getReference("users").child(uid)
            userRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val user = snapshot.getValue(Users::class.java)
                        users.postValue(user)
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

    fun editProfilePicture(uri: Uri, callback: (String) -> Unit) {
        val storageRef = storage.getReference("users").child(auth.currentUser!!.uid)
        val fileRef = storageRef.child("${auth.currentUser!!.uid}.jpg")
        val uploadTask = fileRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { it ->
                val userRef = db.getReference("users").child(auth.currentUser!!.uid)
                userRef.child("profilePicture").setValue(it.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback("Profile Picture Updated")
                    } else {
                        callback(it.exception?.message ?: "Failed to update profile picture")
                    }
                }
            }
        }.addOnFailureListener {
            callback(it.message ?: "Failed to upload profile picture")
        }
    }

    fun editUsername(username: String, callback: (String) -> Unit) {
        val userRef = db.getReference("users").child(auth.currentUser!!.uid)
        userRef.child("username").setValue(username).addOnCompleteListener {
            if (it.isSuccessful) {
                callback("Username Updated")
            } else {
                callback(it.exception?.message ?: "Failed to update username")
            }
        }
    }

}