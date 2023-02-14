package com.kseniabl.tasksapp.di

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.kseniabl.tasksapp.adapters.CreatorsCardsAdapter
import com.kseniabl.tasksapp.di.scopes.CreatorCardsScope
import com.kseniabl.tasksapp.ui.FreelancerCardsFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class CreatorCardsModule {

    @Provides
    @FragmentScoped
    fun provideCreatorCardsFragment(): FreelancerCardsFragment = FreelancerCardsFragment()

    @Provides
    @FragmentScoped
    fun provideCreatorCardsAdapter(@ApplicationContext context: Context)
    : CreatorsCardsAdapter = CreatorsCardsAdapter(context)

    @Provides
    @CreatorCardsScope
    fun provideLinearLayoutManager(fragment: FreelancerCardsFragment): LinearLayoutManager
            = LinearLayoutManager(fragment.context)
}