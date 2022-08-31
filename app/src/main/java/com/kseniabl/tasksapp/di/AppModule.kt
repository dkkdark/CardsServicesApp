package com.kseniabl.tasksapp.di

import android.content.Context
import com.kseniabl.tasksapp.TasksApplication
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
}