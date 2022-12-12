package com.kseniabl.tasksapp.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.kseniabl.tasksapp.models.CardModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/*
@Singleton
class TasksRepository @Inject constructor(
    @ApplicationContext val context: Context
    ): TasksRepositoryInterface {
    private var db = CardsTasksDatabase.getInstance(context)
    private val addCardDao: AddCardDao = db.addCardDao()

    override fun insertAddCard(card: CardModel) {
        CoroutineScope(Dispatchers.IO).launch {
            addCardDao.insertOneCard(card)
        }
    }

    override fun insertAllCards(cards: List<CardModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            addCardDao.insertAllCards(cards)
        }
    }

    override fun getAddCards(): Flow<List<CardModel>> {
        return addCardDao.loadAllCardsLive()
    }

    override fun allAddCards(): List<CardModel> {
        return addCardDao.loadAllCards()
    }

    override fun changeAddProdCard(card: CardModel) {
        CoroutineScope(Dispatchers.IO).launch {
            addCardDao.updateCards(card)
        }
    }

    override fun clearAddProdCards() {
        CoroutineScope(Dispatchers.IO).launch {
            addCardDao.deleteAllCards()
        }
    }
}*/
