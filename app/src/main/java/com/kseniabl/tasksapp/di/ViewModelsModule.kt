package com.kseniabl.tasksapp.di

import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.viewmodels.AddCardsViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelsModule {

    @Binds
    @ViewModelScoped
    abstract fun bindAddCardsViewModel(addCardsViewModel: AddCardsViewModel): AddTasksAdapter.Listener
}