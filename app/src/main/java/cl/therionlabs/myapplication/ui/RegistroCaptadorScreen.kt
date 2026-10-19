package cl.therionlabs.myapplication.ui

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Solo cambia el aspecto (según el boceto). La lógica de validación es la misma de antes.
// La barra superior con la flecha y el título "Registro de captador" la pone MainActivity.
// OJO: lo que falta (enviar la solicitud al servidor) es del Diego,Jose.
@Composable
fun RegistroCaptadorScreen(
    onVolverLogin: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }
    var registroValido by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuocFondo)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Crea tu cuenta",
            color = DuocNavy,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Completa tus datos para poder captar alumnos en los eventos.",
            color = DuocGris
        )

        DuocField(
            etiqueta = "Nombre completo",
            valor = nombre,
            onCambio = {
                nombre = it
                mensaje = ""
            },
            placeholder = "Nombre y apellidos"
        )
        DuocField(
            etiqueta = "RUT",
            valor = rut,
            onCambio = {
                rut = it
                mensaje = ""
            },
            placeholder = "12.345.678-9"
        )
        DuocField(
            etiqueta = "Correo",
            valor = correo,
            onCambio = {
                correo = it
                mensaje = ""
            },
            placeholder = "nombre@correo.cl",
            teclado = KeyboardType.Email
        )
        DuocField(
            etiqueta = "Teléfono",
            valor = telefono,
            onCambio = {
                telefono = it
                mensaje = ""
            },
            placeholder = "+56 9 1234 5678",
            teclado = KeyboardType.Phone
        )
        DuocPasswordField(
            etiqueta = "Contraseña",
            valor = contrasena,
            onCambio = {
                contrasena = it
                mensaje = ""
            },
            placeholder = "Mínimo 8 caracteres",
            imeAction = ImeAction.Done
        )

        DuocBannerInfo("Tu solicitud será revisada por un administrador antes de activar la cuenta.")

        DuocBotonAmarillo(
            texto = "Solicitar registro",
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
                    mensaje = "Datos validados. Falta enviar la solicitud al servidor."
                }
            }
        )

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = if (registroValido) DuocNavy else DuocError,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿Ya tienes cuenta?", color = DuocGris, fontSize = 14.sp)
            TextButton(onClick = onVolverLogin) {
                Text("Inicia sesión", color = DuocNavy, fontWeight = FontWeight.Bold)
            }
        }
    }
}
