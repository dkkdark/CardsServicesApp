package com.kseniabl.tasksapp.network

import com.kseniabl.tasksapp.models.*
import okhttp3.MultipartBody
import javax.inject.Inject

class Repository @Inject constructor(private val retrofitApi: RetrofitApi) {
    /** Authentication **/
    suspend fun loginUser(userLoginModel: UserLoginModel) = retrofitApi.login(userLoginModel)
    suspend fun addUser(addUserModel: AddUserModel) = retrofitApi.addUser(addUserModel)

    /** Get info **/
    suspend fun getTasks(token: String) = retrofitApi.getTasks(token)
    suspend fun getUsersCards(token: String) = retrofitApi.getUsersCards(token)
    suspend fun getCardsById(token: String, id: String) = retrofitApi.getUsersCardsById(token, id)
    suspend fun getBookedCards(token: String) = retrofitApi.getBookedCards(token)
    suspend fun getBookedInfoCards(token: String) = retrofitApi.getBookedInfoCards(token)
    suspend fun getUsers(token: String) = retrofitApi.getUsers(token)
    suspend fun getUserById(token: String, id: String) = retrofitApi.getUserById(token, id)
    suspend fun getUserByToken(token: String) = retrofitApi.getUserByToken(token)

    /** Set info **/
    suspend fun updateSpec(token: String, body: UpdateSpecModel) = retrofitApi.updateSpec(token, body)
    suspend fun updateAddInf(token: String, body: UpdateAddInfModel) = retrofitApi.updateAddInf(token, body)
    suspend fun updateCreatorState(token: String, body: UpdateCreatorStatus) = retrofitApi.updateCreatorStatus(token, body)
    suspend fun updateBookDateUser(token: String, body: UpdateBookDateUser) = retrofitApi.updateBookDateUser(token, body)
    suspend fun updateCard(token: String, body: CardModel) = retrofitApi.updateCard(token, body)
    suspend fun uploadImage(token: String, part: MultipartBody.Part) = retrofitApi.uploadImage(token, part)
    suspend fun getImage(token: String) = retrofitApi.getImage(token)
}