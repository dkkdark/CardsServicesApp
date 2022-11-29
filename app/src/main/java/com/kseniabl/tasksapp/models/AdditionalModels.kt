package com.kseniabl.tasksapp.models

import com.google.gson.annotations.SerializedName

data class AdditionalInfo (
    var description: String = "",
    var country: String = "",
    var city: String = "",
    var typeOfWork: String = ""
)

data class Specialization(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("name")
    var specialization: String = "",
    @SerializedName("description")
    var description: String = "",
)

data class ImageModel (
    val id: Int = 0,
    val img: String = "",
    val name: String = "",
    val mimeType: String = ""
)