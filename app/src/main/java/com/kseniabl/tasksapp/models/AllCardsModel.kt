package com.kseniabl.tasksapp.models

import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface

data class AllCardsModel (
    val cardsList: ArrayList<CardModel> = arrayListOf(),
    val creatorList: ArrayList<FreelancerModel> = arrayListOf(),
    val adapterValue: AllCardsAdapterInterface? = null
)