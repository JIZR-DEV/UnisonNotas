package com.unison.appproductos.views

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.unison.appproductos.Models.Categoria
import com.unison.appproductos.Models.Nota
import com.unison.appproductos.ViewModel.NotaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unison.appproductos.pdf.PdfExporter
import com.unison.appproductos.recordatorio.RecordatorioWorker

import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import com.unison.appproductos.recordatorio.WorkManagerUtils


import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioNotaScreen(navController: NavController, viewModel: NotaViewModel, notaId: Int?) {
    val estadoUi by viewModel.estadoUi.collectAsStateWithLifecycle()
    val notaExistente = estadoUi.notas.find { it.id == notaId }

    // Estados de los campos
    var titulo by rememberSaveable { mutableStateOf(notaExistente?.titulo ?: "") }
    var contenido by rememberSaveable { mutableStateOf(notaExistente?.contenido ?: "") }
    var colorFondo by rememberSaveable { mutableStateOf(notaExistente?.colorFondo ?: "#FFFFFF") }
    var uriImagen by rememberSaveable { mutableStateOf(notaExistente?.uriImagen) }
    var categoriaSeleccionada by rememberSaveable { mutableStateOf<Categoria?>(null) }
    // Estado para mostrar el diálogo de exportación
    var showExportDialog by remember { mutableStateOf(false) }
    // Estado para el menú desplegable de categoría
    var expandedCategoria by remember { mutableStateOf(false) }

    // Si la nota existe, inicializar la categoría seleccionada
    LaunchedEffect(notaExistente) {
        if (notaExistente?.categoriaId != null) {
            categoriaSeleccionada = estadoUi.categorias.find { it.id == notaExistente.categoriaId }
        }
    }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Función para ocultar el teclado
    fun hideKeyboard() {
        focusManager.clearFocus()
    }

    // Obtener el SnackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }

    // Lanzador para tomar foto con la cámara
    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        coroutineScope.launch {
            bitmap?.let {
                val rutaArchivo = guardarBitmapEnRuta(context, it)
                if (rutaArchivo != null) {
                    uriImagen = rutaArchivo  // Almacena la ruta del archivo
                } else {
                    // Mostrar error si no se pudo guardar la imagen
                    snackbarHostState.showSnackbar("Error al guardar la imagen.")
                }
            }
        }
    }

    // Lanzador para seleccionar imagen de la galería
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        coroutineScope.launch {
            uri?.let {
                val rutaArchivo = copiarUriAArchivo(context, it)
                if (rutaArchivo != null) {
                    uriImagen = rutaArchivo  // Almacena la ruta del archivo copiado
                } else {
                    // Mostrar error si no se pudo copiar la imagen
                    snackbarHostState.showSnackbar("Error al seleccionar la imagen.")
                }
            }
        }
    }

    // Observar mensajes de la ViewModel
    LaunchedEffect(key1 = estadoUi.mensaje) {
        estadoUi.mensaje?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.clearMensaje() // Limpiar el mensaje después de mostrar el Snackbar
            navController.navigate("listaNotas") {
                popUpTo("listaNotas") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (notaExistente != null) "Editar Nota" else "Nueva Nota") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
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
                    .clickable { hideKeyboard() }
            ) {
                // Mostrar imagen de fondo si existe
                if (uriImagen != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = File(uriImagen!!)),
                        contentDescription = "Imagen de fondo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Mostrar color de fondo
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(android.graphics.Color.parseColor(colorFondo)))
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    // Campo de Título
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título", color = obtenerColorContraste(colorFondo)) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = obtenerColorContraste(colorFondo),
                            cursorColor = obtenerColorContraste(colorFondo),
                            unfocusedLabelColor = obtenerColorContraste(colorFondo)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // Campo de Descripción
                    OutlinedTextField(
                        value = contenido,
                        onValueChange = { contenido = it },
                        label = { Text("Descripción", color = obtenerColorContraste(colorFondo)) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = obtenerColorContraste(colorFondo),
                            cursorColor = obtenerColorContraste(colorFondo),
                            unfocusedLabelColor = obtenerColorContraste(colorFondo)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(bottom = 16.dp)
                    )

                    // Selector de Categoría
                    ExposedDropdownMenuBox(
                        expanded = expandedCategoria,
                        onExpandedChange = { expandedCategoria = !expandedCategoria }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = categoriaSeleccionada?.nombre ?: "Selecccionar Categoria",
                            onValueChange = {},
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategoria,
                            onDismissRequest = { expandedCategoria = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sin Categoría") },
                                onClick = {
                                    categoriaSeleccionada = null
                                    expandedCategoria = false
                                }
                            )
                            estadoUi.categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.nombre) },
                                    onClick = {
                                        categoriaSeleccionada = categoria
                                        expandedCategoria = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para tomar foto
                    Button(
                        onClick = { photoLauncher.launch() },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50), // Verde llamativo
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar Foto para Fondo", fontWeight = FontWeight.Bold)
                    }

                    // Botón para seleccionar imagen de la galería
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3), // Azul llamativo
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar Imagen de la Galería", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de color
                    Text(
                        "Seleccionar color:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = obtenerColorContraste(
                                colorFondo
                            )
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SelectorColor(
                        colorSeleccionado = colorFondo,
                        onColorSeleccionado = { color ->
                            colorFondo = color
                            uriImagen = null  // Eliminar imagen al seleccionar un color
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selección de Recordatorio

                    Text(
                        text = "Establecer Recordatorio:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    var delayInMinutes by remember { mutableStateOf(10L) } // Valor por defecto

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Minutos: ")
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = delayInMinutes.toString(),
                            onValueChange = {
                                delayInMinutes = it.toLongOrNull() ?: 10L
                            },
                            modifier = Modifier.width(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    // Botón de Crear o Actualizar
                    Button(
                        onClick = {
                            val nuevaNota = Nota(
                                titulo = titulo,
                                contenido = contenido,
                                colorFondo = colorFondo,
                                uriImagen = uriImagen,
                                categoriaId = categoriaSeleccionada?.id
                            )
                            if (notaExistente != null) {
                                viewModel.actualizarNota(nuevaNota.copy(id = notaExistente.id))
                            } else {
                                viewModel.agregarNota(nuevaNota)
                            }


                            // Programar el recordatorio
                            WorkManagerUtils.programarRecordatorio(
                                context = context,
                                titulo = "Recordatorio: ${nuevaNota.titulo}",
                                contenido = nuevaNota.contenido,
                                delayInMinutes = delayInMinutes
                            )


                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EA), // Morado oscuro
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (notaExistente != null) "Actualizar Nota" else "Crear Nota",
                            fontWeight = FontWeight.Bold
                        )

                    }
                    Spacer(modifier = Modifier.height(16.dp))
// Botón para Exportar a PDF
                    Button(
                        onClick = { showExportDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF03A9F4), // Azul claro
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exportar a PDF", fontWeight = FontWeight.Bold)
                    }

                    // Diálogo de confirmación para exportar a PDF
                    if (showExportDialog) {
                        AlertDialog(
                            onDismissRequest = { showExportDialog = false },
                            title = { Text("Exportar a PDF") },
                            text = { Text("¿Deseas exportar esta nota como PDF?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        val nuevaNota = Nota(
                                            titulo = titulo,
                                            contenido = contenido,
                                            colorFondo = colorFondo,
                                            uriImagen = uriImagen,
                                            categoriaId = categoriaSeleccionada?.id
                                        )
                                        val archivoPdf =
                                            PdfExporter.exportarNotaAPdf(context, nuevaNota)
                                        archivoPdf?.let { pdf ->
                                            PdfExporter.compartirPdf(context, pdf)
                                            snackbarHostState.showSnackbar("Nota exportada y compartida exitosamente.")
                                        } ?: run {
                                            snackbarHostState.showSnackbar("Error al exportar la nota a PDF.")
                                        }
                                    }
                                    showExportDialog = false
                                }) {
                                    Text("Exportar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showExportDialog = false }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
            }

            })
        }


/**
 * Composable para seleccionar un color de una lista predefinida.
 */
@Composable
fun SelectorColor(colorSeleccionado: String, onColorSeleccionado: (String) -> Unit) {
    val colores = listOf("#4A90E2", "#7986CB", "#AED581", "#FFCC80", "#FF0000")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        colores.forEach { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(android.graphics.Color.parseColor(color)), shape = CircleShape)
                    .border(
                        width = if (color == colorSeleccionado) 2.dp else 1.dp,
                        color = Color.Black,
                        shape = CircleShape
                    )
                    .clickable { onColorSeleccionado(color) }
            )
        }
    }
}

/**
 * Función para guardar el Bitmap como una ruta de archivo y obtener su ruta absoluta.
 */
suspend fun guardarBitmapEnRuta(context: android.content.Context, bitmap: Bitmap): String? {
    return withContext(Dispatchers.IO) {
        val carpetaImagenes = File(context.cacheDir, "imagenes")
        try {
            carpetaImagenes.mkdirs()
            val archivo = File(carpetaImagenes, "${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(archivo)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            archivo.absolutePath  // Retorna la ruta absoluta
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

/**
 * Función para copiar el contenido de una URI a un archivo en el directorio de la aplicación.
 */
suspend fun copiarUriAArchivo(context: android.content.Context, uri: Uri): String? {
    return withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        try {
            val inputStream = resolver.openInputStream(uri) ?: return@withContext null
            val carpetaImagenes = File(context.cacheDir, "imagenes")
            carpetaImagenes.mkdirs()
            val archivo = File(carpetaImagenes, "${System.currentTimeMillis()}.png")
            val outputStream = FileOutputStream(archivo)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            archivo.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}





