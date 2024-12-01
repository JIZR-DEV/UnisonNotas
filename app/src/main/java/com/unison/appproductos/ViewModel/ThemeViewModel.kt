package com.unison.appproductos.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeViewModel(private val context: Context) : ViewModel() {
    companion object {
        private val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "theme_settings")
    }
    private val THEME_KEY = booleanPreferencesKey("dark_theme")

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: false
    }
    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = enabled
        }
    }
}
