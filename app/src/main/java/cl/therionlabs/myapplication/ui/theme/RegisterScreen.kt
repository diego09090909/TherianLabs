package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.ui.theme.Blanco
import cl.therionlabs.myapplication.viewmodels.RegistroViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: RegistroViewModel = viewModel()
) {
    // Se suscribe a los cambios de estado en vivo
    val estado by viewModel.estado.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Campo Nombre
        OutlinedTextField(
            value = estado.nombre,
            onValueChange = viewModel::onNombreChange,
            label = { Text("Nombre completo") },
            isError = estado.errores.nombre != null,
            supportingText = {
                estado.errores.nombre?.let { mensaje -> Text(mensaje) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Correo
        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo electrónico") },
            isError = estado.errores.correo != null,
            supportingText = {
                estado.errores.correo?.let { mensaje -> Text(mensaje) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Casilla de Términos
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = estado.aceptaTerminos,
                onCheckedChange = viewModel::onAceptarTerminosChange
            )
            Text("Acepto los términos")
        }
        estado.errores.aceptaTerminos?.let { mensaje -> Text(mensaje) }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Vista previa reactiva
        Text("Vista previa en tiempo real")
        Text("Nombre: ${estado.nombre}")
        Text("Correo: ${estado.correo}")
        Text("Términos aceptados: ${if (estado.aceptaTerminos) "Sí" else "No"}")

        // Botón Enviar
        Button(
            onClick = {
                if (viewModel.validar()) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Formulario enviado correctamente")
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Errores detectados. Revisa los campos.")
                    }
                }
            }
        ) {
            Text("Enviar")
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}