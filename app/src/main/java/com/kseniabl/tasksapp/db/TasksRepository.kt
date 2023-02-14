package com.kseniabl.tasksapp.db

import com.kseniabl.tasksapp.models.CardModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TasksRepository @Inject constructor(
    private val cardsDao: CardsDao
): TasksRepositoryInterface {

    override fun insertAddCard(card: CardModel) {
        CoroutineScope(Dispatchers.IO).launch {
            cardsDao.insertOneCard(card)
        }
    }

    override fun insertAllCards(cards: List<CardModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            cardsDao.insertAllCards(cards)
        }
    }

    override fun getAddCards(): Flow<List<CardModel>> {
        return cardsDao.loadAllCardsLive()
    }

    override fun allAddCards(): List<CardModel> {
        return cardsDao.loadAllCards()
    }

    override fun changeAddProdCard(card: CardModel) {
        CoroutineScope(Dispatchers.IO).launch {
            cardsDao.updateCards(card)
        }
    }

    override fun clearAddProdCards() {
        CoroutineScope(Dispatchers.IO).launch {
            cardsDao.deleteAllCards()
        }
    }
}
