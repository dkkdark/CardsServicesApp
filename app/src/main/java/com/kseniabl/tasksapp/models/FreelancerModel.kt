package com.kseniabl.tasksapp.models

@kotlinx.serialization.Serializable
data class FreelancerModel (
    val userInfo: UserModel? = null,
    val specialization: Specialization? = null,
    val additionalInfo: AdditionalInfo? = null
)