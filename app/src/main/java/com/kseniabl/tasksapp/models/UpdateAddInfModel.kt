package com.kseniabl.tasksapp.models

import com.google.gson.annotations.SerializedName

data class UpdateAddInfModel (
    @SerializedName("id")
    var userId: String = "",
    @SerializedName("add_inf_id")
    var addInfId: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("country")
    var country: String = "",
    @SerializedName("city")
    var city: String = "",
    @SerializedName("type_of_work")
    var typeOfWork: String = ""
)