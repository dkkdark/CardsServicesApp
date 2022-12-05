package com.kseniabl.tasksapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kseniabl.tasksapp.models.FreelancerModel
import com.kseniabl.tasksapp.models.UserModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserTokenDataStore @Inject constructor(
    @ApplicationContext var context: Context
): UserTokenDataStoreInterface {

    override val readToken: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN] ?: ""
        }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { pref ->
            pref[USER_TOKEN] = token
        }
    }

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Token")
        val USER_TOKEN = stringPreferencesKey("token")
    }
}


@Singleton
class UserDataStore @Inject constructor(
    @ApplicationContext var context: Context
): UserDataStoreInterface {

    override val readUser: Flow<FreelancerModel> = context.userDataStore.data
        .map { user ->
            user
        }

    override suspend fun writeUser(user: FreelancerModel) {
        context.userDataStore.updateData {
            user
        }
    }

    companion object {
        val Context.userDataStore: DataStore<FreelancerModel> by dataStore(
            fileName = "settings.pb",
            serializer = UsersSerializer
        )
    }
}

@Suppress("BlockingMethodInNonBlockingContext")
object UsersSerializer : Serializer<FreelancerModel> {

    override val defaultValue: FreelancerModel
        get() = FreelancerModel()

    override suspend fun readFrom(input: InputStream): FreelancerModel {
        return try {
            Json.decodeFromString(
                deserializer = FreelancerModel.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: FreelancerModel, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = FreelancerModel.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}