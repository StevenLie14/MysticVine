package edu.bluejack24_1.mysticvine.repositories

import android.util.Log
import androidx.appcompat.view.ActionMode.Callback
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack24_1.mysticvine.model.PartyRoom
import edu.bluejack24_1.mysticvine.model.PartyRoomMember
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.utils.SharedPrefUtils
import edu.bluejack24_1.mysticvine.utils.Utils

class PartyRepository {

    private val db = FirebaseDatabase.getInstance()

    fun checkPartyExist (partyCode : String, callback: (Boolean) -> Unit) {
        val partyRef = db.getReference("parties").child(partyCode)
        partyRef.get().addOnSuccessListener {
            if (!it.exists()) {
                callback(false)
            } else {
                callback(true)
            }
        }.addOnFailureListener {
            callback(false)
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