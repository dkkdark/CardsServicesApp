package com.kseniabl.tasksapp.models

import java.io.Serializable

data class FreelancerModel (
    val id: String,
    var username: String,
    var isFreelancer: Boolean,
    var additionalInfo: AdditionalInfo? = null,
    var profession: Profession? = null,
    var img: ImageModel? = null,
)