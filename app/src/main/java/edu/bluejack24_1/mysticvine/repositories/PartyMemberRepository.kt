package edu.bluejack24_1.mysticvine.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack24_1.mysticvine.model.FlashCard
import edu.bluejack24_1.mysticvine.model.Users

class PartyMemberRepository {

    private val db = FirebaseDatabase.getInstance()

    fun joinParty(partyCode: String, userId: String, type: String, callback: (String) -> Unit) {
        val partyRef = db.getReference("party members").child(partyCode).child(userId)
        partyRef.setValue(userId).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(partyCode)
            } else {
                callback(it.exception?.message ?: "Failed to join party")
            }
        }
    }

    fun getPartyMember (partyCode: String, callback: (List<String>) -> Unit) {
        val partyRef = db.getReference("party members").child(partyCode)
        partyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.v("Party Member Repository", snapshot.toString())
                    val joinedMemberDB : List<String> = snapshot.children.map { it.getValue(String::class.java)!! }
                    callback(joinedMemberDB)
                }catch (e: Exception){
                    Log.e("Party Member Repository", "Error parsing user data")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Party Member Repository", error.message)
            }

        })
    }
}