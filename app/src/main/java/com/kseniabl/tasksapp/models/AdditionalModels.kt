package com.kseniabl.tasksapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@kotlinx.serialization.Serializable
@Parcelize
data class AdditionalInfo (
    @SerializedName("id")
    val id: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("country")
    val country: String = "",
    @SerializedName("city")
    val city: String = "",
    @SerializedName("type_of_work")
    val typeOfWork: String = ""
): Parcelable

@kotlinx.serialization.Serializable
@Parcelize
data class Specialization(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val specialization: String = "",
    @SerializedName("description")
    val description: String = "",
): Parcelable

@kotlinx.serialization.Serializable
@Parcelize
data class ImageModel (
    val filename: String = "",
    val content: String? = null,
    val type: String = "",
): Parcelable