package com.kseniabl.tasksapp.models

import com.google.gson.annotations.SerializedName

data class AddUserModel(
    @SerializedName("master_password")
    val masterPassword: String,
    @SerializedName("user_name")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role_name")
    val role_name: String,
    @SerializedName("email")
    val email: String
)
