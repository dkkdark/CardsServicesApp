package com.kseniabl.tasksapp.models

import java.io.Serializable

data class UserModel(
    val id: String,
    var username: String,
    var email: String,
    var isFreelancer: Boolean,
    var token: String,

    var img: String,
    var additionalInfo: AdditionalInfo,
    var profession: Profession
): Serializable