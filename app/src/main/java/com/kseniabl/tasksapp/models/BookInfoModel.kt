package com.kseniabl.tasksapp.models

import com.google.gson.annotations.SerializedName

data class BookInfoModel (
    @SerializedName("id")
    val id: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("username")
    val username: String,
)