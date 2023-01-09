package com.kseniabl.tasksapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookDate(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("date")
    var date: String = "",
    @SerializedName("card_id")
    var cardId: String = "",
    @SerializedName("user_id")
    var userId: String = ""
): Parcelable
