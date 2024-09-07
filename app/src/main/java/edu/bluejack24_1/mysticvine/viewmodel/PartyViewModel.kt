package edu.bluejack24_1.mysticvine.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack24_1.mysticvine.model.PartyRoom
import edu.bluejack24_1.mysticvine.model.Users
import edu.bluejack24_1.mysticvine.repositories.CustomQuestionRepository
import edu.bluejack24_1.mysticvine.repositories.PartyMemberRepository
import edu.bluejack24_1.mysticvine.repositories.PartyRepository
import edu.bluejack24_1.mysticvine.repositories.UserRepository
import java.util.UUID

class PartyViewModel (application: Application) : AndroidViewModel(application) {

    private val partyRepository : PartyRepository = PartyRepository()
    private val customQuestionRepository : CustomQuestionRepository = CustomQuestionRepository()
    private val partyMemberRepository : PartyMemberRepository = PartyMemberRepository()

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



    private val _changePartyStatusResult = MutableLiveData<Int>()
    val changePartyStatusResult = _changePartyStatusResult
    fun changePartyStatus(partyCode: String, status: String) {
        partyRepository.updateGameStatus(partyCode, status, UUID.randomUUID().toString()) { status, message ->
            Log.d("PartyViewModel", "Status: $status, Message: $message")
            _changePartyStatusResult.value = status
        }
    }

    fun deleteParty(partyCode: String) {
        partyRepository.deleteParty(partyCode) {
            partyMemberRepository.deleteParty(partyCode) {
                customQuestionRepository.deleteQuestions(partyCode) {
                }
            }
        }
    }

    fun nextAnswer(partyRoom: PartyRoom) {
        var changeRoom = partyRoom.copy(partyIndex = partyRoom.partyIndex + 1)
        partyRepository.nextAnswer(changeRoom)
    }

    fun resetAll() {
        _createPartyResult.value = ""
        _partyHost.value = null
        _partyRoom.value = PartyRoom()
    }

}