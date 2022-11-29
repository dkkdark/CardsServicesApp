package com.kseniabl.tasksapp.network

import com.kseniabl.tasksapp.models.AddUserModel
import com.kseniabl.tasksapp.models.SpecBody
import com.kseniabl.tasksapp.models.UserLoginModel
import javax.inject.Inject

class Repository @Inject constructor(private val retrofitApi: RetrofitApi) {
    suspend fun loginUser(userLoginModel: UserLoginModel) = retrofitApi.login(userLoginModel)
    suspend fun addUser(addUserModel: AddUserModel) = retrofitApi.addUser(addUserModel)
    suspend fun getTasks(token: String) = retrofitApi.getTasks(token)
    suspend fun getUsers(token: String) = retrofitApi.getUsers(token)
    suspend fun getSpec(token: String, body: SpecBody) = retrofitApi.getSpec(token, body)
}