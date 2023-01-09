package com.kseniabl.tasksapp.di

import androidx.recyclerview.widget.LinearLayoutManager
import com.kseniabl.tasksapp.adapters.BookDateAdapter
import com.kseniabl.tasksapp.adapters.DatesDetailAdapter
import com.kseniabl.tasksapp.ui.CardDetailsFragment
import com.kseniabl.tasksapp.ui.CreateAndChangeTaskFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class CardDetailsModule {

    @Provides
    @FragmentScoped
    fun provideCardDetailsFragment(): CardDetailsFragment = CardDetailsFragment()

    @Provides
    @FragmentScoped
    fun provideCardDetailsAdapter(): DatesDetailAdapter = DatesDetailAdapter()

    @Provides
    @CardDetailsAnnotation
    fun provideLinearLayoutManager(fragment: CardDetailsFragment): LinearLayoutManager
            = LinearLayoutManager(fragment.context)
}