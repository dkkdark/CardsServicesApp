package com.kseniabl.tasksapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CardModel(
    @PrimaryKey var id: String = "",
    var title: String = "",
    var description: String = "",
    var date: String = "",
    var createTime: Long = 0,
    var cost: Int = 0,
    var active: Boolean = false,
    var agreement: Boolean = false,
    var user_id: String = ""
)