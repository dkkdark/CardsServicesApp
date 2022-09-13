package com.kseniabl.tasksapp.di

import androidx.recyclerview.widget.LinearLayoutManager
import com.kseniabl.tasksapp.adapters.AllTasksAdapter
import com.kseniabl.tasksapp.adapters.FreelancersAdapter
import com.kseniabl.tasksapp.ui.AddCardsFragment
import com.kseniabl.tasksapp.ui.AllCardsFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class AllCardsModule {

    @Provides
    @FragmentScoped
    fun provideAllCardsFragment(): AllCardsFragment = AllCardsFragment()

    @Provides
    @AllCardsScope
    fun provideLinearLayoutManager(fragment: AllCardsFragment): LinearLayoutManager
            = LinearLayoutManager(fragment.context)

    @Provides
    @FragmentScoped
    fun provideAllTasksAdapter(): AllTasksAdapter = AllTasksAdapter()

    @Provides
    @FragmentScoped
    fun provideFreelancersAdapter(): FreelancersAdapter = FreelancersAdapter()
}