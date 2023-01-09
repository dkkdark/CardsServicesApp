package com.kseniabl.tasksapp.di

import androidx.recyclerview.widget.LinearLayoutManager
import com.kseniabl.tasksapp.adapters.BookCardsAdapter
import com.kseniabl.tasksapp.adapters.DatesDetailAdapter
import com.kseniabl.tasksapp.ui.BookedCardsFragment
import com.kseniabl.tasksapp.ui.CardDetailsFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class BookCardsModule {

    @Provides
    @FragmentScoped
    fun provideBookCardsFragment(): BookedCardsFragment = BookedCardsFragment()

    @Provides
    @FragmentScoped
    fun provideBookCardsAdapter(): BookCardsAdapter = BookCardsAdapter()

    @Provides
    @BookCardsAnnotation
    fun provideLinearLayoutManager(fragment: BookedCardsFragment): LinearLayoutManager
            = LinearLayoutManager(fragment.context)
}