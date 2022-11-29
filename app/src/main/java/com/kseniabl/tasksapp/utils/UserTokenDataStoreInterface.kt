package com.kseniabl.tasksapp.utils

import kotlinx.coroutines.flow.Flow

interface UserTokenDataStoreInterface {
    val readToken: Flow<String>
    suspend fun saveToken(token: String)
}