package com.kseniabl.tasksapp.utils

import kotlinx.coroutines.flow.Flow

interface WelcomeScreenDataStoreInterface {
    val readState: Flow<Boolean>
    suspend fun saveState(state: Boolean)
}
