package com.unison.appproductos.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unison.appproductos.Models.Categoria
import com.unison.appproductos.ViewModel.NotaViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionCategoriasScreen(navController: NavController, viewModel: NotaViewModel) {
    val categorias by viewModel.estadoUi.map { it.categorias }.collectAsState(initial = emptyList())
    var nombreCategoria by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Categorías") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // Vinculación de SnackbarHost
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Título
                Text(
                    "Agregar Nueva Categoría",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Campo de entrada para el nombre de la categoría
                OutlinedTextField(
                    value = nombreCategoria,
                    onValueChange = { nombreCategoria = it },
                    label = { Text("Nombre de la categoría") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón para agregar categoría
                Button(
                    onClick = {
                        if (nombreCategoria.isNotBlank()) {
                            val nuevaCategoria = Categoria(nombre = nombreCategoria)
                            viewModel.agregarCategoria(nuevaCategoria)
                            nombreCategoria = ""
                        } else {
                            // Mostrar Snackbar si el nombre está vacío
                            scope.launch {
                                snackbarHostState.showSnackbar("El nombre de la categoría no puede estar vacío")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar Categoría")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de categorías
                Text(
                    "Categorías Existentes",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn {
                    items(categorias) { categoria ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(categoria.nombre, style = MaterialTheme.typography.bodyLarge)
                            IconButton(
                                onClick = {
                                    viewModel.eliminarCategoria(categoria)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar categoría",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
