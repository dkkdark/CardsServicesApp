package com.kseniabl.tasksapp.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kseniabl.tasksapp.TasksApplication
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.db.TasksRepository
import com.kseniabl.tasksapp.db.TasksRepositoryInterface
import com.kseniabl.tasksapp.utils.UserSave
import com.kseniabl.tasksapp.utils.UserSaveInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun provideSaveUser(@ApplicationContext context: Context): UserSaveInterface = UserSave(context)

    @Singleton
    @Provides
    fun provideRepository(@ApplicationContext context: Context): TasksRepositoryInterface = TasksRepository(context)

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseDbReference(): DatabaseReference = FirebaseDatabase.getInstance().reference
}