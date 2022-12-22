package com.kseniabl.tasksapp.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.kseniabl.tasksapp.view.TagsModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Entity
@Parcelize
data class CardModel(
    @PrimaryKey
    @SerializedName("card_id")
    var id: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("cost")
    var cost: Float = 0F,
    @SerializedName("is_agreement")
    var agreement: Boolean = false,
    @SerializedName("is_prepayment")
    var prepayment: Boolean = false,
    @SerializedName("tags_list")
    var tags: ArrayList<TagsModel> = arrayListOf(),
    @SerializedName("create_time")
    var createTime: Long = 0,
    @SerializedName("is_active")
    var active: Boolean = true,
    @SerializedName("user_id")
    var user_id: String = ""
): Parcelable