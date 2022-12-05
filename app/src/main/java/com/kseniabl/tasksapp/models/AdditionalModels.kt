package com.kseniabl.tasksapp.models

import com.google.gson.annotations.SerializedName

@kotlinx.serialization.Serializable
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
)

@kotlinx.serialization.Serializable
data class Specialization(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val specialization: String = "",
    @SerializedName("description")
    val description: String = "",
)

data class ImageModel (
    val id: Int = 0,
    val img: String = "",
    val name: String = "",
    val mimeType: String = ""
)