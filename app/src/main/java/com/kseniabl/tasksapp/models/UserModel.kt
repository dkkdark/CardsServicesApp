package com.kseniabl.tasksapp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserModel(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("username")
    var username: String = "",
    @SerializedName("is_creator")
    var creator: Boolean = false,
    @SerializedName("image")
    var img: String = "",
    @SerializedName("role_name")
    var rolename: String = "",
    @SerializedName("specialization")
    var specialization: String = "",
    @SerializedName("add_inf")
    var addInf: String = ""
): Serializable