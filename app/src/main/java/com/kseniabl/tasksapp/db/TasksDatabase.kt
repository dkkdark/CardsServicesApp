package com.kseniabl.tasksapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kseniabl.tasksapp.models.CardModel

@Database(entities = [CardModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class CardsTasksDatabase : RoomDatabase() {

    abstract fun addCardDao(): CardsDao

    companion object {
        const val DATABASE_NAME = "CardsTasksDatabase"
    }
}
