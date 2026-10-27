package cl.therionlabs.myapplication.ui

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import cl.therionlabs.myapplication.viewmodels.LoginUiState
import cl.therionlabs.myapplication.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }


    val uiState by viewModel.uiState.collectAsState()


    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {

                onLoginSuccess()
                viewModel.resetearEstado()
            }
            else -> {}
        }
    }

    fun ingresar() {
        errorCorreo = when {
            email.isBlank() -> "Ingresa tu correo."
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                "Ingresa un correo válido, por ejemplo nombre@correo.cl."
            else -> null
        }
        errorClave = if (password.isBlank()) "Ingresa tu contraseña." else null


        if (errorCorreo == null && errorClave == null) {
            viewModel.iniciarSesion(email, password)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuocFondo)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Bienvenido, captador",
            color = DuocNavy,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ingresa con tu cuenta para registrar alumnos en los eventos de los liceos.",
            color = DuocGris
        )

        Spacer(Modifier.height(4.dp))

        DuocField(
            etiqueta = "Correo",
            valor = email,
            onCambio = {
                email = it
                errorCorreo = null
                if (uiState is LoginUiState.Error) viewModel.resetearEstado()
            },
            placeholder = "nombre@correo.cl",
            error = errorCorreo,
            teclado = KeyboardType.Email
        )

        DuocPasswordField(
            etiqueta = "Contraseña",
            valor = password,
            onCambio = {
                password = it
                errorClave = null
                if (uiState is LoginUiState.Error) viewModel.resetearEstado()
            },
            placeholder = "Tu contraseña",
            error = errorClave,
            imeAction = ImeAction.Done
        )


        if (uiState is LoginUiState.Error) {
            Text(
                text = (uiState as LoginUiState.Error).mensaje,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(4.dp))


        DuocBotonAzul(
            texto = if (uiState is LoginUiState.Loading) "Autenticando..." else "Ingresar",
            onClick = { ingresar() }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿Aún no tienes cuenta?", color = DuocGris, fontSize = 14.sp)
            TextButton(onClick = onNavigateToRegister) {
                Text("Regístrate", color = DuocNavy, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = DuocGris
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Tu sesión se mantiene en el dispositivo para que puedas trabajar sin conexión.",
                color = DuocGris,
                fontSize = 13.sp
            )
        }
    }
}