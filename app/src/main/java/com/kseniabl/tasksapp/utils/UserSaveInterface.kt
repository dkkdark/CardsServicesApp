package com.kseniabl.tasksapp.utils

import com.kseniabl.tasksapp.models.UserModel

interface UserSaveInterface {
    fun readSharedPref(): UserModel?
    fun saveCurrentUser(currentUser: UserModel)
    fun getLiveSharedPref(): SharedPreferenceLiveData<String>
    fun jsonToUserModel(json: String): UserModel?
}