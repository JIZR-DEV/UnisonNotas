package com.unison.appproductos.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unison.appproductos.ViewModel.AutenticacionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracionPIN(navController: NavController, viewModel: AutenticacionViewModel) {
    val context = LocalContext.current
    var nuevoPIN by remember { mutableStateOf("") }
    var confirmarPIN by remember { mutableStateOf("") }
    var pinAnterior by remember { mutableStateOf("") }
    var isPinConfigured by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Verificar si ya hay un PIN configurado al iniciar la pantalla
    LaunchedEffect(Unit) {
        isPinConfigured = viewModel.isPinConfigured()
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título
                Text(
                    text = if (isPinConfigured) "Configurar PIN" else "Establecer PIN Nuevo",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para el PIN anterior (solo si ya hay un PIN configurado)
                if (isPinConfigured) {
                    OutlinedTextField(
                        value = pinAnterior,
                        onValueChange = { pinAnterior = it },
                        label = { Text("PIN Anterior") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Campo de texto para el nuevo PIN
                OutlinedTextField(
                    value = nuevoPIN,
                    onValueChange = { nuevoPIN = it },
                    label = { Text("Nuevo PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de texto para confirmar el nuevo PIN
                OutlinedTextField(
                    value = confirmarPIN,
                    onValueChange = { confirmarPIN = it },
                    label = { Text("Confirmar PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para guardar el PIN
                Button(
                    onClick = {
                        if (nuevoPIN != confirmarPIN) {
                            Toast.makeText(context, "Los nuevos PIN no coinciden", Toast.LENGTH_SHORT).show()
                        } else if (nuevoPIN.isEmpty() || confirmarPIN.isEmpty()) {
                            Toast.makeText(context, "El PIN no puede estar vacío", Toast.LENGTH_SHORT).show()
                        } else {
                            scope.launch {
                                val cambioExitoso = if (isPinConfigured) {
                                    viewModel.cambiarPIN(pinAnterior, nuevoPIN)
                                } else {
                                    viewModel.cambiarPIN("", nuevoPIN)
                                }
                                if (cambioExitoso) {
                                    Toast.makeText(context, "PIN configurado con éxito", Toast.LENGTH_SHORT).show()
                                    navController.navigate("autenticacion") {
                                        popUpTo("autenticacion") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "El PIN anterior es incorrecto", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Guardar PIN")
                }
            }
        }
    }
}
