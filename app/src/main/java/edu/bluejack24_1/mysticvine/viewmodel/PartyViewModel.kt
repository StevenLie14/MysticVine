package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.mysticvine.model.PartyRoom
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.repositories.PartyRepository
import edu.bluejack24_1.mysticvine.repositories.UserRepository

class PartyViewModel (application: Application) : AndroidViewModel(application) {
    private val userRepository : UserRepository = UserRepository(application)
    private val partyRepository : PartyRepository = PartyRepository()

    private val _createPartyResult = MutableLiveData<String>()
    val createPartyResult = _createPartyResult
    fun createParty(userId : String) {
        partyRepository.createParty(userId) {
            _createPartyResult.value = it
        }
    }

    private val _partyHost = MutableLiveData<Users?>()
    val partyHost : LiveData<Users?> = _partyHost
    fun getPartyHost(partyCode: String) {
        partyRepository.getPartyHost(partyCode) {
            _partyHost.value = it
        }
    }

    private val _partyRoom = MutableLiveData<PartyRoom>()
    val partyRoom : LiveData<PartyRoom> = _partyRoom
    fun getPartyRoom(partyCode: String) {
        partyRepository.getPartyRoom(partyCode, _partyRoom)
    }

    fun changePartyStatus(partyCode: String, status: String) {
        partyRepository.updateGameStatus(partyCode, status) {

        }
    }

}