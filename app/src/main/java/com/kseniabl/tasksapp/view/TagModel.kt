package com.kseniabl.tasksapp.view

import com.google.gson.annotations.SerializedName

data class TagsModel(
    @SerializedName("name")
    val name: String
)