package com.kseniabl.tasksapp.utils

import android.content.Context
import com.google.gson.Gson
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.models.UserModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSave @Inject constructor(
    @ApplicationContext var context: Context
): UserSaveInterface {

    override fun readSharedPref(): UserModel? {
        val sharedPref = context.getSharedPreferences("currentUserSave", Context.MODE_PRIVATE)
        val json = sharedPref.getString(context.getString(R.string.current_user_shared_pref), "")
        val gson = Gson()
        return try {
            gson.fromJson(json, UserModel::class.java)
        } catch (e: NullPointerException) {
            null
        }
    }

    override fun getLiveSharedPref(): SharedPreferenceLiveData<String> {
        val sharedPref = context.getSharedPreferences("currentUserSave", Context.MODE_PRIVATE)
        return sharedPref.stringLiveData(context.getString(R.string.current_user_shared_pref), "")
    }

    override fun jsonToUserModel(json: String): UserModel? {
        val gson = Gson()
        return try {
            gson.fromJson(json, UserModel::class.java)
        } catch (e: NullPointerException) {
            null
        }
    }

    override fun saveCurrentUser(currentUser: UserModel) {
        val gson = Gson()
        val json = gson.toJson(currentUser)
        val sharedPref = context.getSharedPreferences("currentUserSave", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(context.getString(R.string.current_user_shared_pref), json)
            apply()
        }
    }
}