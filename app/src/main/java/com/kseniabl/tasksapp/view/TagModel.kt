package com.kseniabl.tasksapp.view

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TagsModel(
    @SerializedName("name")
    val name: String
): Parcelable