package com.kseniabl.tasksapp.di

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.di.scopes.AddCardsScope
import com.kseniabl.tasksapp.ui.AddCardsFragment
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
    fun provideAddTasksAdapter(@ApplicationContext context: Context)
    : AddTasksAdapter = AddTasksAdapter(context)

    @Provides
    @AddCardsScope
    fun provideLinearLayoutManager(fragment: AddCardsFragment): LinearLayoutManager
            = LinearLayoutManager(fragment.context)
}