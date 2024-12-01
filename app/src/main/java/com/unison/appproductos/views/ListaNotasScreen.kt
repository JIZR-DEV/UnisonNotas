package com.unison.appproductos.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unison.appproductos.ViewModel.NotaViewModel
import com.unison.appproductos.Models.Nota
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.rememberAsyncImagePainter
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaNotasScreen(navController: NavController, viewModel: NotaViewModel) {
    val estadoUi by viewModel.estadoUi.collectAsState()
    var query by remember { mutableStateOf("") }
    val categorias = estadoUi.categorias
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Notas") },
                actions = {
                    IconButton(onClick = { navController.navigate("ajustes") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("formularioNota")
                },
                containerColor = MaterialTheme.colorScheme.primary, // Usando el color primario del tema
                contentColor = MaterialTheme.colorScheme.onPrimary // Asegurando que el color del ícono sea el adecuado
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Nota")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background) // Usando el fondo del tema
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.buscarNotas(it)
                },
                label = { Text("Buscar notas") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary, // Color de borde enfocado según el tema
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface, // Borde no enfocado
                    focusedLabelColor = MaterialTheme.colorScheme.primary, // Color de la etiqueta cuando está enfocada
                    cursorColor = MaterialTheme.colorScheme.primary // Color del cursor
                )
            )

            // Selector de Categoría
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = categoriaSeleccionada?.nombre ?: "Todas las categorias",
                    onValueChange = {},
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todas las categorías") },
                        onClick = {
                            viewModel.seleccionarCategoria(null)
                            expanded = false
                        }
                    )
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombre) },
                            onClick = {
                                viewModel.seleccionarCategoria(categoria)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Lista de Notas Filtradas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(estadoUi.notas) { nota ->
                    // Filtrar notas según categoría seleccionada
                    if (categoriaSeleccionada == null || categoriaSeleccionada?.id == nota.categoriaId) {
                        ItemNota(
                            nota = nota,
                            onClick = {
                                navController.navigate("detalleNota/${nota.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemNota(nota: Nota, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        if (nota.uriImagen != null) {
            // Mostrar imagen de fondo
            Image(
                painter = rememberAsyncImagePainter(model = File(nota.uriImagen)),
                contentDescription = "Imagen de fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Agregar una superposición para mejorar la legibilidad
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)) // Superposición con color negro semitransparente
            )
        } else {
            // Mostrar color de fondo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(android.graphics.Color.parseColor(nota.colorFondo)))
            )
        }

        // Contenido de la nota
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = nota.titulo,
                style = MaterialTheme.typography.titleMedium, // Usando la tipografía del tema
                color = MaterialTheme.colorScheme.onSurface // Asegurando que el texto sea legible según el tema
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = nota.contenido,
                style = MaterialTheme.typography.bodyMedium, // Usando la tipografía del tema
                color = MaterialTheme.colorScheme.onSurface, // Asegurando que el texto sea legible según el tema
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


// Función para obtener el color de contraste
fun obtenerColorContraste(colorFondo: String): Color {
    val color = android.graphics.Color.parseColor(colorFondo)
    val r = android.graphics.Color.red(color)
    val g = android.graphics.Color.green(color)
    val b = android.graphics.Color.blue(color)
    val luminancia = (0.299 * r + 0.587 * g + 0.114 * b) / 255
    return if (luminancia > 0.5) Color.Black else Color.White
}
