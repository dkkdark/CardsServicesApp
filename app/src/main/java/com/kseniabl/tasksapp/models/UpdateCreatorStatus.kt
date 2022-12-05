package com.kseniabl.tasksapp.models

import com.google.gson.annotations.SerializedName

data class UpdateCreatorStatus(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_name")
    val username: String
)
