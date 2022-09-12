package com.kseniabl.tasksapp.models

import java.io.Serializable

data class AdditionalInfo (
    var description: String = "",
    var country: String = "",
    var city: String = "",
    var typeOfWork: String = ""
)

data class Profession(
    var description: String = "",
    var specialization: String = "",
    var tags: List<String> = listOf()
)

data class ImageModel (
    val id: Int = 0,
    val img: String = "",
    val name: String = "",
    val mimeType: String = ""
)