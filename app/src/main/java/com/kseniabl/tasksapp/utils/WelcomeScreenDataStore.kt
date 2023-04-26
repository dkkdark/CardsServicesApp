package com.kseniabl.tasksapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WelcomeScreenDataStore @Inject constructor(
    @ApplicationContext var context: Context
): WelcomeScreenDataStoreInterface {

    override val readState: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[USER_FIRST_ENTER] ?: false
        }

    override suspend fun saveState(state: Boolean) {
        context.dataStore.edit { pref ->
            pref[USER_FIRST_ENTER] = state
        }
    }

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "State")
        val USER_FIRST_ENTER = booleanPreferencesKey("USER_FIRST_ENTER")
    }
}