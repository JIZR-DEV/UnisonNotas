package com.unison.appproductos.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unison.appproductos.ViewModel.ThemeViewModel
import kotlinx.coroutines.launch

@Composable
fun PantallaAjustes(navController: NavController, themeViewModel: ThemeViewModel) {
    // Observar el estado del tema desde el ViewModel
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    // Aplicar el MaterialTheme
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Título de la pantalla
                Text(
                    "Ajustes",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Configuración del modo oscuro
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Modo oscuro",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                themeViewModel.setDarkTheme(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para cambiar el PIN
                Button(
                    onClick = { navController.navigate("configuracionPIN") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Cambiar PIN",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para gestionar categorías
                Button(
                    onClick = { navController.navigate("gestionCategorias") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Gestionar Categorías",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
