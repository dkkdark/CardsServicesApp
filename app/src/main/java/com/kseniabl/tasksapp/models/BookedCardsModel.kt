package com.kseniabl.tasksapp.models

import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface

data class BookedCardsModel (
    val adapterValue: AllCardsAdapterInterface? = null,
    val cardsList: ArrayList<CardModel> = arrayListOf(),
    val bookedInfoList: ArrayList<BookInfoModel> = arrayListOf()
)