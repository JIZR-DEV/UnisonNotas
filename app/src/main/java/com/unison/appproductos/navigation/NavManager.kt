package com.unison.appproductos.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.room.Room
import com.unison.appproductos.ViewModel.*
import com.unison.appproductos.room.AppDatabase
import com.unison.appproductos.room.NotaRepositorio
import com.unison.appproductos.views.*
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.navArgument

@Composable
fun GrafoNavegacion() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Inicializar ViewModels
    val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "notas-db"
    )
        .fallbackToDestructiveMigration()
        .build()
    val repositorio = NotaRepositorio(database.notaDao(), database.categoriaDao())
    val notaViewModel: NotaViewModel = viewModel(factory = NotaViewModelFactory(repositorio))
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(context))
    val autenticacionViewModel: AutenticacionViewModel = viewModel(factory = AutenticacionViewModelFactory(context))

    // Determinar el destino inicial basado en si el PIN está configurado
    var initialRoute by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val isPinConfigured = autenticacionViewModel.isPinConfigured()
            initialRoute = if (isPinConfigured) "autenticacion" else "configuracionPIN"
        }
    }

    // Mientras determinamos la ruta inicial, mostramos un indicador de carga
    if (initialRoute == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(navController = navController, startDestination = initialRoute!!) {
            composable("autenticacion") {
                PantallaAutenticacion(navController = navController, viewModel = autenticacionViewModel)
            }
            composable("configuracionPIN") {
                PantallaConfiguracionPIN(navController = navController, viewModel = autenticacionViewModel)
            }
            composable("inicio") {
                HomeScreen(navController = navController)
            }
            composable("listaNotas") {
                ListaNotasScreen(navController = navController, viewModel = notaViewModel)
            }
            composable(
                route = "formularioNota?notaId={notaId}",
                arguments = listOf(navArgument("notaId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val notaId = backStackEntry.arguments?.getInt("notaId") ?: -1
                FormularioNotaScreen(navController = navController, viewModel = notaViewModel, notaId = notaId)
            }
            composable(
                route = "detalleNota/{notaId}",
                arguments = listOf(navArgument("notaId") {
                    type = NavType.IntType
                })
            ) { backStackEntry ->
                val notaId = backStackEntry.arguments?.getInt("notaId") ?: -1
                DetalleNotaScreen(navController = navController, viewModel = notaViewModel, notaId = notaId)
            }
            composable("ajustes") {
                PantallaAjustes(navController = navController, themeViewModel = themeViewModel)
            }
            // Nueva ruta para gestión de categorías
            composable("gestionCategorias") {
                GestionCategoriasScreen(navController = navController, viewModel = notaViewModel)
            }
        }
    }
}
