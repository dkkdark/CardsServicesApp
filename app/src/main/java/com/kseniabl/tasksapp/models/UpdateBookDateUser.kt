package com.kseniabl.tasksapp.models

import com.google.gson.annotations.SerializedName

data class UpdateBookDateUser (
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("book_id")
    val bookId: String = ""
)