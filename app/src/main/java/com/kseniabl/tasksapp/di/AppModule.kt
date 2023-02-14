package com.kseniabl.tasksapp.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kseniabl.tasksapp.db.CardsDao
import com.kseniabl.tasksapp.db.CardsTasksDatabase
import com.kseniabl.tasksapp.db.CardsTasksDatabase.Companion.DATABASE_NAME
import com.kseniabl.tasksapp.db.TasksRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CardsTasksDatabase =
        Room.databaseBuilder(
            context,
            CardsTasksDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideDao(database: CardsTasksDatabase): CardsDao = database.addCardDao()

    @Provides
    @Singleton
    fun provideDatabaseRepository(dao: CardsDao): TasksRepository =
        TasksRepository(dao)
}