package com.kseniabl.tasksapp.di

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import com.kseniabl.tasksapp.adapters.AddTasksAdapter
import com.kseniabl.tasksapp.adapters.CreatorsCardsAdapter
import com.kseniabl.tasksapp.ui.AddCardsFragment
import com.kseniabl.tasksapp.ui.AllCardsFragment
import com.kseniabl.tasksapp.ui.FreelancerCardsFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class CreatorCardsModule {

    @Provides
    @FragmentScoped
    fun provideCreatorCardsFragment(): FreelancerCardsFragment = FreelancerCardsFragment()

    @Provides
    @FragmentScoped
    fun provideCreatorCardsAdapter(): CreatorsCardsAdapter = CreatorsCardsAdapter()

    @Provides
    @CreatorCardsScope
    fun provideLinearLayoutManager(fragment: FreelancerCardsFragment): LinearLayoutManager
            = LinearLayoutManager(fragment.context)
}