package com.kseniabl.tasksapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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