package com.kseniabl.tasksapp.utils

import com.kseniabl.tasksapp.models.FreelancerModel
import kotlinx.coroutines.flow.Flow

interface UserDataStoreInterface {
    val readUser: Flow<FreelancerModel>
    suspend fun writeUser(user: FreelancerModel)
}