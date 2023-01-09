package com.kseniabl.tasksapp.di

import androidx.recyclerview.widget.LinearLayoutManager
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.adapters.BookDateAdapter
import com.kseniabl.tasksapp.ui.AddCardsFragment
import com.kseniabl.tasksapp.ui.CreateAndChangeTaskFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class CreateAndChangeTaskModule {

    @Provides
    @FragmentScoped
    fun provideCreateAndChangeTaskCardsFragment(): CreateAndChangeTaskFragment = CreateAndChangeTaskFragment()

    @Provides
    @FragmentScoped
    fun provideCreateAndChangeTaskAdapter(): BookDateAdapter = BookDateAdapter()

    @Provides
    @CreateAndChangeTaskAnnotation
    fun provideLinearLayoutManager(fragment: CreateAndChangeTaskFragment): LinearLayoutManager
            = LinearLayoutManager(fragment.context)
}