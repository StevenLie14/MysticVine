package edu.bluejack24_1.mysticvine.repositories

import android.util.Log
import androidx.appcompat.view.ActionMode.Callback
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack24_1.mysticvine.model.PartyRoom
import edu.bluejack24_1.mysticvine.model.PartyRoomMember
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.utils.SharedPrefUtils
import edu.bluejack24_1.mysticvine.utils.Utils

class PartyRepository {

    private val db = FirebaseDatabase.getInstance()

    fun checkPartyExistAndNotStarted (partyCode : String, callback: (Boolean) -> Unit) {
        val partyRef = db.getReference("parties").child(partyCode)
        partyRef.get().addOnSuccessListener {
            if (it.exists()) {
                val partyRoom = it.getValue(PartyRoom::class.java)
                if (partyRoom == null || partyRoom.partyStatus != "Waiting") {
                    callback(false)
                }else {
                    callback(true)
                }
            } else {
                callback(false)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun getPartyRoom (partyCode: String, partyRoom: MutableLiveData<PartyRoom>) {
        val partyRef = db.getReference("parties").child(partyCode)
        partyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val partyRoomDB = snapshot.getValue(PartyRoom::class.java)
                    partyRoom.postValue(partyRoomDB)
                }catch (e: Exception){
                    Log.e("Party Repository", "Error parsing party data")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Party Repository", error.message)
            }

        })
    }

    fun updateGameStatus (partyCode: String, status: String, callback: (String) -> Unit) {
        val partyRef = db.getReference("parties").child(partyCode)
        partyRef.get().addOnSuccessListener {
            if (it.exists()) {
                val partyRoom = it.getValue(PartyRoom::class.java)
                if (partyRoom != null) {
                    partyRoom.partyStatus = status
                    partyRef.setValue(partyRoom).addOnCompleteListener {
                        if (it.isSuccessful) {
                            callback("Success game Status")
                        } else {
                            callback(it.exception?.message ?: "Failed to update game status")
                        }
                    }
                } else {
                    callback("Party not found")
                }
            } else {
                callback("Party not found")
            }
        }.addOnFailureListener {
            callback("Failed to update game status")
        }
    }
    private fun generateCode(callback: (String) -> Unit) {
        val partyRef = db.getReference("parties")

        fun checkAndGenerate() {
            val newCode = Utils.generateCode()
            partyRef.child(newCode).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    checkAndGenerate()
                } else {
                    callback(newCode)
                }
            }.addOnFailureListener {
            }
        }
        checkAndGenerate()
    }
    fun createParty (userId: String, status: String = "Waiting", callback: (String) -> Unit) {
        generateCode {code ->
            val partyRoom = PartyRoom(code, userId, status)
            val partyRef = db.getReference("parties").child(partyRoom.partyCode)
            partyRef.setValue(partyRoom).addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(code)
                } else {
                    callback(it.exception?.message ?: "Failed to create party")
                }
            }
        }
    }

    fun getPartyHost (partyCode: String, callback: (Users?) -> Unit) {
        val partyRef = db.getReference("parties").child(partyCode)
        partyRef.get().addOnSuccessListener {
            val partyRoom = it.getValue(PartyRoom::class.java)
            if (partyRoom != null) {
                val userRef = db.getReference("users").child(partyRoom.partyOwnerId)
                userRef.get().addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(Users::class.java)
                    callback(user)
                }.addOnFailureListener {
                    callback(null)
                }
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    }

}