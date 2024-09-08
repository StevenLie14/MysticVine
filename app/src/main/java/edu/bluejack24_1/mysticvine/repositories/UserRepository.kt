package edu.bluejack24_1.mysticvine.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
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
                val user = Users(auth.currentUser!!.uid, username, email, "https://firebasestorage.googleapis.com/v0/b/mystic-vine-316b2.appspot.com/o/users%2Flogo.png?alt=media&token=b649647c-3440-4b01-92b2-93f22b649845" , 1, 0, 0, 0,0,0,0    )
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

    fun getUserByIds(userIds: List<String>,userList : MutableLiveData<List<Users>>) {
        val users = mutableListOf<Users>()
        val usersRef = db.getReference("users")
        userIds.forEach { userId ->
            val userRef = usersRef.child(userId)
            userRef.addValueEventListener (object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        Log.v("UserRepository", snapshot.toString())
                        val userDB : Users = snapshot.getValue(Users::class.java)!!
                        userDB?.let { user ->
                            users.removeIf { it.id == user.id }
                            users.add(user)
                            userList.postValue(users.toList())
                        }
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

    fun getNonRTUserByIds(userIds: List<String>, callback: (List<Users>) -> Unit) {
        val users = mutableListOf<Users>()
        val usersRef = db.getReference("users")
        userIds.forEach { userId ->
            val userRef = usersRef.child(userId)
            userRef.get().addOnSuccessListener { snapshot ->
                try {
                    val userDB = snapshot.getValue(Users::class.java)
                    userDB?.let { user ->
                        users.removeIf { it.id == user.id }
                        users.add(user)
                        callback(users.toList())
                    }
                } catch (e: Exception) {
                    Log.e("UserRepository", "Error parsing user data")
                }
            }.addOnFailureListener { error ->
                Log.e("UserRepository", error.message ?: "Failed to get user data")
            }
        }
    }
    fun getLandingLeaderBoard(userList : MutableLiveData<List<Users>>) {
        val userRef = db.getReference("users").orderByChild("score")
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val userDB: List<Users> = snapshot.children
                        .mapNotNull { it.getValue(Users::class.java) }
                        .sortedByDescending { it.score }
                        .take(3)
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

    fun getAllLeaderBoard(userList: MutableLiveData<List<Users>>) {
        val userRef = db.getReference("users").orderByChild("score")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val userDB: List<Users> = snapshot.children.mapNotNull { it.getValue(Users::class.java) }
                        .sortedByDescending { it.score }

                    userList.postValue(userDB)

                } catch (e: Exception) {
                    Log.e("UserRepository", "Error parsing user data", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserRepository", error.message)
            }
        })
    }

    fun getLeaderBoardAfter4ranks(userList: MutableLiveData<List<Users>>) {
        val userRef = db.getReference("users").orderByChild("score")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val userDB: List<Users> = snapshot.children.mapNotNull { it.getValue(Users::class.java) }
                        .sortedByDescending { it.score }

                    if (userDB.size > 3) {
                        userList.postValue(userDB.drop(3))
                    } else {
                        userList.postValue(emptyList())
                    }
                } catch (e: Exception) {
                    Log.e("UserRepository", "Error parsing user data", e)
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
    
    fun updateExpBooster(callback: (String) -> Unit) {
            val userRef = db.getReference("users").child(auth.currentUser!!.uid)
            Log.d("User", "${userRef}")
            userRef.child("expBooster").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentBooster = task.result.getValue(Int::class.java) ?: 0
                    val newBoosterValue = currentBooster + 1
                    userRef.child("expBooster").setValue(newBoosterValue).addOnCompleteListener { boosterTask ->
                        if (boosterTask.isSuccessful) {
                            userRef.child("coin").get().addOnCompleteListener { coinTask ->
                                if (coinTask.isSuccessful) {
                                    Log.d("Coins", "${coinTask.result}")
                                    val currentCoins = coinTask.result.getValue(Int::class.java) ?: 0
                                    val newCoinValue = currentCoins - 500

                                    if (newCoinValue >= 0) {
                                        userRef.child("coin").setValue(newCoinValue).addOnCompleteListener { coinUpdateTask ->
                                            if (coinUpdateTask.isSuccessful) {
                                                callback("Experience booster obtained.")
                                            } else {
                                                callback(coinUpdateTask.exception?.message ?: "Failed to update coins.")
                                            }
                                        }
                                    } else {
                                        callback("Not enough coins to purchase Exp Booster.")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    fun updateCoinBooster(callback: (String) -> Unit) {
            val userRef = db.getReference("users").child(auth.currentUser!!.uid)
            userRef.child("coinBooster").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentBooster = task.result.getValue(Int::class.java) ?: 0
                    val newBoosterValue = currentBooster + 1
                    userRef.child("coinBooster").setValue(newBoosterValue).addOnCompleteListener { boosterTask ->
                        if (boosterTask.isSuccessful) {
                            userRef.child("coin").get().addOnCompleteListener { coinTask ->
                                if (coinTask.isSuccessful) {
                                    val currentCoins = coinTask.result.getValue(Int::class.java) ?: 0
                                    val newCoinValue = currentCoins - 1000
                                    if (newCoinValue >= 0) {
                                        userRef.child("coin").setValue(newCoinValue).addOnCompleteListener { coinUpdateTask ->
                                            if (coinUpdateTask.isSuccessful) {
                                                callback("Coin booster obtained")
                                            } else {
                                                callback(coinUpdateTask.exception?.message ?: "Failed to update coins.")
                                            }
                                        }
                                    } else {
                                        callback("Not enough coins to purchase Coin Booster.")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    fun updateShieldBooster(callback: (String) -> Unit) {
        val userRef = db.getReference("users").child(auth.currentUser!!.uid)
        userRef.child("shieldBooster").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentBooster = task.result.getValue(Int::class.java) ?: 0
                val newBoosterValue = currentBooster + 1
                userRef.child("shieldBooster").setValue(newBoosterValue).addOnCompleteListener { boosterTask ->
                    if (boosterTask.isSuccessful) {
                        userRef.child("coin").get().addOnCompleteListener { coinTask ->
                            if (coinTask.isSuccessful) {
                                val currentCoins = coinTask.result.getValue(Int::class.java) ?: 0
                                val newCoinValue = currentCoins - 1500
                                if (newCoinValue >= 0) {
                                    userRef.child("coin").setValue(newCoinValue).addOnCompleteListener { coinUpdateTask ->
                                        if (coinUpdateTask.isSuccessful) {
                                            callback("Shield Booster obtained")
                                        } else {
                                            callback(coinUpdateTask.exception?.message ?: "Failed to update coins.")
                                        }
                                    }
                                } else {
                                    callback("Not enough coins to purchase Shield Booster.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    
  fun getUserById(userId: String, callback : (Users) -> Unit) {
        val userRef = db.getReference("users").child(userId)
        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                try {
                    val userDB = dataSnapshot.getValue(Users::class.java)
                    callback(userDB!!)
                } catch (e: Exception) {
                    Log.e("UserRepository", "Error parsing user data")
                }
            } else {
                Log.e("UserRepository", "No user data found")
            }
        }.addOnFailureListener { exception ->
            Log.e("UserRepository", exception.message ?: "Failed to get user data")
        }
    }

    fun updateUser (users: Users, callback: (Int, String) -> Unit) {
        val userRef = db.getReference("users").child(users.id)
        userRef.setValue(users).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(200, "Item deleted")
            } else {
                callback(400, it.exception?.message ?: "Failed to delete item")
            }
        }
    }

}