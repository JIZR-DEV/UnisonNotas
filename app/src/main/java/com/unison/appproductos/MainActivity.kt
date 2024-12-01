package com.unison.appproductos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.unison.appproductos.navigation.GrafoNavegacion
import com.unison.appproductos.ui.theme.AppProductosTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unison.appproductos.ViewModel.ThemeViewModel
import com.unison.appproductos.ViewModel.ThemeViewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(applicationContext))
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)
            AppProductosTheme(darkTheme = isDarkTheme) {
                GrafoNavegacion()
            }
        }
    }
}
