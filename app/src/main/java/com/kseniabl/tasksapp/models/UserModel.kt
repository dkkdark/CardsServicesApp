package com.kseniabl.tasksapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@kotlinx.serialization.Serializable
data class UserModel(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("username")
    val username: String = "",
    @SerializedName("is_creator")
    val creator: Boolean = false,
    @SerializedName("image")
    val img: String = "",
    @SerializedName("role_name")
    val rolename: String = "",
    @SerializedName("specialization")
    val specialization: String = "",
    @SerializedName("add_inf")
    val addInf: String = ""
): Parcelable