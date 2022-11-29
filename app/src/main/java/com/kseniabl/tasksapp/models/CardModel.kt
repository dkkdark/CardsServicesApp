package com.kseniabl.tasksapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class CardModel(
    @PrimaryKey
    @SerializedName("id")
    var id: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("date")
    var date: String = "",
    @SerializedName("create_time")
    var createTime: Long = 0,
    @SerializedName("is_active")
    var active: Boolean = true,
    @SerializedName("user_id")
    var user_id: String = ""
)