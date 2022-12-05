package com.kseniabl.tasksapp.network

import com.kseniabl.tasksapp.models.*
import javax.inject.Inject

class Repository @Inject constructor(private val retrofitApi: RetrofitApi) {
    /** Authentication **/
    suspend fun loginUser(userLoginModel: UserLoginModel) = retrofitApi.login(userLoginModel)
    suspend fun addUser(addUserModel: AddUserModel) = retrofitApi.addUser(addUserModel)

    /** Get info **/
    suspend fun getTasks(token: String) = retrofitApi.getTasks(token)
    suspend fun getUsers(token: String) = retrofitApi.getUsers(token)
    suspend fun getUser(token: String) = retrofitApi.getUser(token)
    suspend fun getSpec(token: String, body: IdBody) = retrofitApi.getSpec(token, body)
    suspend fun getAddInf(token: String, body: IdBody) = retrofitApi.getAddInf(token, body)

    /** Set info **/
    suspend fun updateSpec(token: String, body: UpdateSpecModel) = retrofitApi.updateSpec(token, body)
    suspend fun updateAddInf(token: String, body: UpdateAddInfModel) = retrofitApi.updateAddInf(token, body)
    suspend fun updateCreatorState(token: String, body: UpdateCreatorStatus) = retrofitApi.updateCreatorStatus(token, body)
}