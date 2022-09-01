package com.kseniabl.tasksapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CardModel(
    @PrimaryKey var id: String,
    var title: String,
    var description: String,
    var date: String,
    var createTime: Long,
    var cost: Int,
    var active: Boolean,
    var agreement: Boolean,
    var user_id: String
)