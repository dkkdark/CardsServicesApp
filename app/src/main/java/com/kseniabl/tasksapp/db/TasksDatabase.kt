package com.kseniabl.tasksapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kseniabl.tasksapp.models.CardModel

@Database(entities = [CardModel::class], version = 1)
abstract class CardsTasksDatabase : RoomDatabase() {
    abstract fun addCardDao(): AddCardDao

    companion object {
        private var instance: CardsTasksDatabase? = null

        fun getInstance(context: Context): CardsTasksDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    CardsTasksDatabase::class.java,
                    "CardsTasksDatabase")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as CardsTasksDatabase
        }
    }
}
