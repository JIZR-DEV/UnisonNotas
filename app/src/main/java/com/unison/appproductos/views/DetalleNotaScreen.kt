package com.unison.appproductos.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.unison.appproductos.ViewModel.NotaViewModel
import com.unison.appproductos.pdf.PdfExporter
import java.io.File
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleNotaScreen(navController: NavController, viewModel: NotaViewModel, notaId: Int) {
    val estadoUi by viewModel.estadoUi.collectAsState()
    val nota = estadoUi.notas.find { it.id == notaId }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = estadoUi.mensaje) {
        estadoUi.mensaje?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.clearMensaje()
            if (mensaje == "Nota eliminada" || mensaje == "Nota actualizada") {
                navController.navigate("listaNotas") {
                    popUpTo("listaNotas") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    if (nota == null) {
        LaunchedEffect(Unit) {
            navController.navigate("listaNotas") {
                popUpTo("listaNotas") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    if (nota != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Información de la Nota") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                val archivoPdf = PdfExporter.exportarNotaAPdf(navController.context, nota)
                                archivoPdf?.let { pdf ->
                                    PdfExporter.compartirPdf(navController.context, pdf)
                                } ?: run {
                                    snackbarHostState.showSnackbar("Error al exportar la nota a PDF.")
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Compartir PDF")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            content = { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    if (nota.uriImagen != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = File(nota.uriImagen)),
                            contentDescription = "Imagen de fondo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(android.graphics.Color.parseColor(nota.colorFondo)))
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = nota.titulo,
                            onValueChange = {},
                            label = { Text("Título", color = MaterialTheme.colorScheme.onBackground) },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        OutlinedTextField(
                            value = nota.contenido,
                            onValueChange = {},
                            label = { Text("Descripción", color = MaterialTheme.colorScheme.onBackground) },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(bottom = 16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate("formularioNota?notaId=$notaId")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Modificar", color = MaterialTheme.colorScheme.onPrimary)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = { showDeleteDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text(text = "Confirmar Eliminación") },
                            text = { Text("¿Estás seguro de que deseas eliminar esta nota? Esta acción no se puede deshacer.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.eliminarNota(nota)
                                    showDeleteDialog = false
                                }) {
                                    Text("Eliminar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}
