package com.kseniabl.tasksapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.models.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AddCardDao {
    @Query("SELECT * FROM CardModel")
    fun loadAllCardsLive(): Flow<List<CardModel>>

    @Query("SELECT * FROM CardModel")
    fun loadAllCards(): List<CardModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCards(cards: List<CardModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOneCard(card: CardModel)

    @Update
    fun updateCards(card: CardModel)

    @Query("DELETE FROM CardModel")
    fun deleteAllCards()

    @Delete
    fun deleteCard(card: CardModel)
}
