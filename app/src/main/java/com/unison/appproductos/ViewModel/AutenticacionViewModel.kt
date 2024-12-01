package com.unison.appproductos.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AutenticacionViewModel(private val context: Context) : ViewModel() {

    companion object {
        private val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "settings")
    }

    private val PIN_KEY = stringPreferencesKey("pin_key")

    val pinGuardado: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PIN_KEY] ?: ""
    }

    /**
     * Verifica si el PIN ingresado coincide con el guardado.
     */
    suspend fun verificarPIN(pinIngresado: String): Boolean {
        val pin = pinGuardado.first()
        return pinIngresado == pin
    }

    /**
     * Cambia el PIN si el PIN anterior es válido.
     */
    suspend fun cambiarPIN(pinAntiguo: String, nuevoPIN: String): Boolean {
        val pinActual = pinGuardado.first()
        return if (pinAntiguo == pinActual) {
            context.dataStore.edit { preferences ->
                preferences[PIN_KEY] = nuevoPIN
            }
            true // Cambio exitoso
        } else {
            false // PIN antiguo incorrecto
        }
    }

    /**
     * Verifica si hay un PIN configurado.
     */
    suspend fun isPinConfigured(): Boolean {
        val pin = pinGuardado.first()
        return pin.isNotEmpty()
    }
}
