package com.kseniabl.tasksapp.di

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.kseniabl.tasksapp.adapters.BookCardsAdapter
import com.kseniabl.tasksapp.adapters.BookedUsersCardsAdapter
import com.kseniabl.tasksapp.ui.BookedCardsFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class BookCardsModule {

    @Provides
    @FragmentScoped
    fun provideBookCardsFragment(): BookedCardsFragment = BookedCardsFragment()

    @Provides
    @FragmentScoped
    fun provideBookCardsAdapter(@ApplicationContext context: Context)
    : BookCardsAdapter = BookCardsAdapter(context)

    @Provides
    @FragmentScoped
    fun provideBookedUsersCards(): BookedUsersCardsAdapter = BookedUsersCardsAdapter()

    @Provides
    @BookCardsAnnotation
    fun provideLinearLayoutManager(fragment: BookedCardsFragment): LinearLayoutManager
            = LinearLayoutManager(fragment.context)
}