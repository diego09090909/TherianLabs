package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.viewmodels.RegisterUiState
import cl.therionlabs.myapplication.viewmodels.RegisterUserViewModel

@Composable
fun RegistroCaptadorScreen(
    onVolverLogin: () -> Unit = {},
    viewModel: RegisterUserViewModel = viewModel() // 👈 ViewModel inyectado
) {
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }


    val uiState by viewModel.uiState.collectAsState()


    LaunchedEffect(uiState) {
        when (uiState) {
            is RegisterUiState.Success -> {

                onVolverLogin()
                viewModel.resetearEstado()
            }
            else -> {}
        }
    }

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
                if (uiState is RegisterUiState.Error) viewModel.resetearEstado()
            },
            placeholder = "Nombre y apellidos"
        )
        DuocField(
            etiqueta = "RUT",
            valor = rut,
            onCambio = {
                rut = it
                if (uiState is RegisterUiState.Error) viewModel.resetearEstado()
            },
            placeholder = "12.345.678-9"
        )
        DuocField(
            etiqueta = "Correo",
            valor = correo,
            onCambio = {
                correo = it
                if (uiState is RegisterUiState.Error) viewModel.resetearEstado()
            },
            placeholder = "nombre@correo.cl",
            teclado = KeyboardType.Email
        )
        DuocField(
            etiqueta = "Teléfono",
            valor = telefono,
            onCambio = {
                telefono = it
                if (uiState is RegisterUiState.Error) viewModel.resetearEstado()
            },
            placeholder = "+56 9 1234 5678",
            teclado = KeyboardType.Phone
        )
        DuocPasswordField(
            etiqueta = "Contraseña",
            valor = contrasena,
            onCambio = {
                contrasena = it
                if (uiState is RegisterUiState.Error) viewModel.resetearEstado()
            },
            placeholder = "Mínimo 6 caracteres",
            imeAction = ImeAction.Next
        )
        DuocPasswordField(
            etiqueta = "Confirmar Contraseña",
            valor = confirmarContrasena,
            onCambio = {
                confirmarContrasena = it
                if (uiState is RegisterUiState.Error) viewModel.resetearEstado()
            },
            placeholder = "Repite tu contraseña",
            imeAction = ImeAction.Done
        )

        DuocBannerInfo("Tu solicitud creará inmediatamente tu cuenta de acceso a la plataforma.")


        if (uiState is RegisterUiState.Error) {
            Text(
                text = (uiState as RegisterUiState.Error).mensaje,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }


        DuocBotonAmarillo(
            texto = if (uiState is RegisterUiState.Loading) "Registrando..." else "Solicitar registro",
            onClick = {
                // Llama al método del ViewModel para validar e interactuar con Firebase
                viewModel.registrarCaptador(
                    correo = correo,
                    clave = contrasena,
                    confirmarClave = confirmarContrasena
                )
            }
        )

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