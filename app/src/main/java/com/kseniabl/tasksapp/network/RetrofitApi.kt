package com.kseniabl.tasksapp.network

import com.kseniabl.tasksapp.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface RetrofitApi {
    @POST("login")
    suspend fun login(@Body userLoginModel: UserLoginModel): Response<TokenModel>

    @POST("add-user")
    suspend fun addUser(@Body addUserModel: AddUserModel): Response<Void>

    @GET("cards")
    suspend fun getTasks(@Header("Authorization") token: String): Response<ArrayList<CardModel>>

    @GET("users")
    suspend fun getUsers(@Header("Authorization") token: String): Response<ArrayList<UserModel>>

    @PATCH("spec")
    suspend fun getSpec(@Header("Authorization") token: String, @Body id: SpecBody): Response<Specialization>
}