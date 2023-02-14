package com.kseniabl.tasksapp.view

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class TagsModel(
    @SerializedName("name")
    val name: String
): Parcelable