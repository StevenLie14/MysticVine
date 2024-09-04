package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import android.support.v4.os.IResultReceiver._Parcel
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.repositories.PartyMemberRepository
import edu.bluejack24_1.mysticvine.repositories.PartyRepository
import edu.bluejack24_1.mysticvine.repositories.UserRepository

class PartyMemberViewModel (application: Application) : AndroidViewModel(application) {

    private val partyMemberRepository = PartyMemberRepository()
    private val partyRepository = PartyRepository()
    private val userRepository = UserRepository(application)

    private val _joinedMemberList = MutableLiveData<List<Users>>()
    val joinedMemberList = _joinedMemberList
    fun getPartyMember(partyCode: String) {
        partyMemberRepository.getPartyMember(partyCode) {
            userRepository.getUserByIds(it, _joinedMemberList)
        }
    }

    private val _joinPartyResult = MutableLiveData<String>()
    val joinPartyResult : LiveData<String> = _joinPartyResult

    fun joinParty(partyCode: String, userId: String, type: String) {
        partyRepository.checkPartyExistAndNotStarted(partyCode) {
            if (!it) {
                _joinPartyResult.value = "Party not exist"
                return@checkPartyExistAndNotStarted
            }else {
                partyMemberRepository.joinParty(partyCode, userId, type) {res ->
                    _joinPartyResult.value = res
                }
            }
        }
    }

}