package com.kseniabl.tasksapp.di

import com.kseniabl.tasksapp.network.Repository
import com.kseniabl.tasksapp.network.RetrofitApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    // Set ip of this device (PC)
    @Provides
    //fun provideBaseUrl(): String = "http:///10.0.2.2"
    fun provideBaseUrl(): String = "http://192.168.1.64"

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL : String) : RetrofitApi = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
        .create(RetrofitApi::class.java)

    @Provides
    @Singleton
    fun provideMainRemoteData(mainService : RetrofitApi) : Repository = Repository(mainService)

}