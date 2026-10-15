package cl.therionlabs.myapplication.ui

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulDuoc = Color(0xFF17365D)
private val AmarilloDuoc = Color(0xFFFFC900)
private val Fondo = Color(0xFFF7F8FA)

@Composable
fun RegistroCaptadorScreen(
    onVolverLogin: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var mostrarContrasena by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var registroValido by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Registro de captador",
            color = AzulDuoc,
            fontSize = 27.sp,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Crea tu cuenta",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Completa tus datos para poder captar alumnos en los eventos.",
            color = Color.DarkGray
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                mensaje = ""
            },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = rut,
            onValueChange = {
                rut = it
                mensaje = ""
            },
            label = { Text("RUT") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                mensaje = ""
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                mensaje = ""
            },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            )
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                mensaje = ""
            },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (mostrarContrasena) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(
                    onClick = {
                        mostrarContrasena = !mostrarContrasena
                    }
                ) {
                    Text(if (mostrarContrasena) "Ocultar" else "Mostrar")
                }
            }
        )

        Text(
            text = "Tu solicitud será revisada por un administrador antes de activar la cuenta.",
            color = AzulDuoc,
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = {
                mensaje = when {
                    nombre.isBlank() ||
                            rut.isBlank() ||
                            correo.isBlank() ||
                            telefono.isBlank() ||
                            contrasena.isBlank() ->
                        "Completa todos los campos."

                    !Patterns.EMAIL_ADDRESS
                        .matcher(correo.trim())
                        .matches() ->
                        "Ingresa un correo electrónico válido."

                    contrasena.length < 8 ->
                        "La contraseña debe tener al menos 8 caracteres."

                    else -> ""
                }

                registroValido = mensaje.isEmpty()

                if (registroValido) {
                    mensaje =
                        "Datos validados. Falta enviar la solicitud al servidor."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulDuoc,
                contentColor = Color.White
            )
        ) {
            Text("Solicitar registro")
        }

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = if (registroValido) {
                    AzulDuoc
                } else {
                    MaterialTheme.colorScheme.error
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        TextButton(
            onClick = onVolverLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                color = AzulDuoc
            )
        }
    }
}
