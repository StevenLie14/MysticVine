package edu.bluejack24_1.mysticvine.model

data class PartyRoom(
    val partyCode: String = "",
    val partyOwnerId: String = "",
    var partyStatus: String = "",
    var partyIndex : Int = 0,
    var partyQuestionID : String = ""
)
