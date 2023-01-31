package com.kseniabl.tasksapp.di

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import com.kseniabl.tasksapp.adapters.AllTasksAdapter
import com.kseniabl.tasksapp.adapters.FreelancersAdapter
import com.kseniabl.tasksapp.ui.AddCardsFragment
import com.kseniabl.tasksapp.ui.AllCardsFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @AllCardsScope
    fun provideFlexboxLayoutManager(fragment: AllCardsFragment): FlexboxLayoutManager
            = FlexboxLayoutManager(fragment.context)

    @Provides
    @FragmentScoped
    fun provideAllTasksAdapter(@ApplicationContext context: Context)
    : AllTasksAdapter = AllTasksAdapter(context)

    @Provides
    @FragmentScoped
    fun provideFreelancersAdapter(@ApplicationContext context: Context)
    : FreelancersAdapter = FreelancersAdapter(context)
}