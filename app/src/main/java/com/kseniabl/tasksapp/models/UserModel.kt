package com.kseniabl.tasksapp.models

import java.io.Serializable

data class UserModel(
    val id: String = "",
    var username: String = "",
    var email: String = "",
    var freelancer: Boolean = false,
    var token: String = "",

    var img: String = "",
    var additionalInfo: AdditionalInfo = AdditionalInfo(),
    var profession: Profession = Profession(),

    var cards: CardsList = CardsList()
): Serializable