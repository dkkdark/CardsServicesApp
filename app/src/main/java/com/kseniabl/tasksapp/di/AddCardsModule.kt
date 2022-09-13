package com.kseniabl.tasksapp.di

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.core.Context
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.adapters.AllCardsAdapterInterface
import com.kseniabl.tasksapp.ui.AddCardsFragment
import com.kseniabl.tasksapp.ui.AllCardsFragment
import com.kseniabl.tasksapp.viewmodels.AddCardsViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class AddCardsModule {

    @Provides
    @FragmentScoped
    fun provideAddCardsFragment(): AddCardsFragment = AddCardsFragment()

    @Provides
    @FragmentScoped
    fun provideAddTasksAdapter(): AddTasksAdapter = AddTasksAdapter()

    @Provides
    @FragmentScoped
    @AddCardsScope
    fun provideLinearLayoutManager(fragment: AddCardsFragment): LinearLayoutManager
            = LinearLayoutManager(fragment.context)
}