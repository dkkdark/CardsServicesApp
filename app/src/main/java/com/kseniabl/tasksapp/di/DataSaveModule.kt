package com.kseniabl.tasksapp.di

import android.content.Context
import com.kseniabl.tasksapp.db.TasksRepository
import com.kseniabl.tasksapp.db.TasksRepositoryInterface
import com.kseniabl.tasksapp.utils.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSaveModule {

    @Singleton
    @Binds
    abstract fun bindSaveUserToken(userSave: UserTokenDataStore): UserTokenDataStoreInterface

    @Singleton
    @Binds
    abstract fun bindSaveUser(userSave: UserDataStore): UserDataStoreInterface

    /*@Singleton
    @Binds
    abstract fun bindSaveUser(userSave: UserSave): UserSaveInterface*/

    @Singleton
    @Binds
    abstract fun bindRepository(tasksRepository: TasksRepository): TasksRepositoryInterface
}