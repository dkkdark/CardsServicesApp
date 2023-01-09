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

data class ImageModel (
    val id: Int = 0,
    val img: String = "",
    val name: String = "",
    val mimeType: String = ""
)