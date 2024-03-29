package com.kseniabl.tasksapp.network

import com.kseniabl.tasksapp.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApi {
    @POST("login")
    suspend fun login(@Body userLoginModel: UserLoginModel): Response<TokenModel>

    @POST("add-user")
    suspend fun addUser(@Body addUserModel: AddUserModel): Response<Void>

    @GET("cards")
    suspend fun getTasks(@Header("Authorization") token: String): Response<ArrayList<CardModel>>

    @GET("cards-by-token")
    suspend fun getUsersCards(@Header("Authorization") token: String): Response<ArrayList<CardModel>>

    @GET("cards-by-id/{id}")
    suspend fun getUsersCardsById(@Header("Authorization") token: String, @Path("id") id: String): Response<ArrayList<CardModel>>

    @GET("booked-cards")
    suspend fun getBookedCards(@Header("Authorization") token: String): Response<ArrayList<CardModel>>

    @GET("cards-was-booked")
    suspend fun getBookedInfoCards(@Header("Authorization") token: String): Response<ArrayList<BookInfoModel>>

    @GET("users")
    suspend fun getUsers(@Header("Authorization") token: String): Response<ArrayList<UserModel>>

    @GET("user/{id}")
    suspend fun getUserById(@Header("Authorization") token: String, @Path("id") id: String): Response<UserModel>

    @GET("user")
    suspend fun getUserByToken(@Header("Authorization") token: String): Response<UserModel>

    @PATCH("spec")
    suspend fun getSpec(@Header("Authorization") token: String, @Body id: IdBody): Response<Specialization>

    @PATCH("add-inf")
    suspend fun getAddInf(@Header("Authorization") token: String, @Body id: IdBody): Response<AdditionalInfo>

    @POST("update-spec")
    suspend fun updateSpec(@Header("Authorization") token: String, @Body specialization: UpdateSpecModel): Response<Void>

    @POST("update-add-inf")
    suspend fun updateAddInf(@Header("Authorization") token: String, @Body additionalInfo: UpdateAddInfModel): Response<Void>

    @POST("update-creator-status")
    suspend fun updateCreatorStatus(@Header("Authorization") token: String, @Body updateCreatorStatus: UpdateCreatorStatus): Response<Void>

    @POST("update-book-date-user")
    suspend fun updateBookDateUser(@Header("Authorization") token: String, @Body updateBookDateUser: UpdateBookDateUser): Response<Void>

    @POST("update-card")
    suspend fun updateCard(@Header("Authorization") token: String, @Body updateCardModel: CardModel): Response<Void>

    @Multipart
    @POST("upload-image")
    suspend fun uploadImage(@Header("Authorization") token: String, @Part image: MultipartBody.Part): Response<Void>

    @GET("get-image")
    suspend fun getImage(@Header("Authorization") token: String): Response<ImageModel>
}