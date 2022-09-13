package com.kseniabl.tasksapp.di

import android.content.Context
import com.kseniabl.tasksapp.db.TasksRepository
import com.kseniabl.tasksapp.db.TasksRepositoryInterface
import com.kseniabl.tasksapp.utils.UserSave
import com.kseniabl.tasksapp.utils.UserSaveInterface
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
    abstract fun bindSaveUser(userSave: UserSave): UserSaveInterface

    @Singleton
    @Binds
    abstract fun bindRepository(tasksRepository: TasksRepository): TasksRepositoryInterface
}