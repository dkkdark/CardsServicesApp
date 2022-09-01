package com.kseniabl.tasksapp.db

import androidx.lifecycle.LiveData
import com.kseniabl.tasksapp.models.CardModel
import kotlinx.coroutines.flow.Flow

interface TasksRepositoryInterface {
    fun insertAddCard(card: CardModel)
    fun getAddCards(): Flow<List<CardModel>>
    fun allAddCards(): List<CardModel>
    fun changeAddProdCard(card: CardModel)
}