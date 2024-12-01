package com.unison.appproductos.ViewModel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AutenticacionViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AutenticacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AutenticacionViewModel(context) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
