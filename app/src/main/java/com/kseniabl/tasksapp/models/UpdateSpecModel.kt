package com.kseniabl.tasksapp.models

import com.google.gson.annotations.SerializedName

data class UpdateSpecModel (
    @SerializedName("id")
    var userId: String = "",
    @SerializedName("spec_id")
    var specId: String = "",
    @SerializedName("name")
    var specialization: String = "",
    @SerializedName("description")
    var description: String = ""
)