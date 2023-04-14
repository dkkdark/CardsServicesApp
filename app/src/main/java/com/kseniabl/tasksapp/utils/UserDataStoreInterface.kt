package com.kseniabl.tasksapp.utils

import com.kseniabl.tasksapp.models.UserModel
import kotlinx.coroutines.flow.Flow

interface UserDataStoreInterface {
    val readUser: Flow<UserModel>
    suspend fun writeUser(user: UserModel)
}