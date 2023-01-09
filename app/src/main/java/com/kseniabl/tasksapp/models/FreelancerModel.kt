package com.kseniabl.tasksapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@kotlinx.serialization.Serializable
@Parcelize
data class FreelancerModel (
    val userInfo: UserModel? = null,
    val specialization: Specialization? = null,
    val additionalInfo: AdditionalInfo? = null
): Parcelable