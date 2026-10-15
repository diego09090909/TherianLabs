
package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.ui.Alignment
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.model.Catalogos
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import cl.therionlabs.myapplication.viewmodels.RegistroViewModel

@Composable
fun RegisterScreen(
    mainViewModel: MainViewModel,
    viewModel: RegistroViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val pendientes by viewModel.pendientes.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Muestra los mensajes que emite el ViewModel (guardado OK, RUT duplicado)
    LaunchedEffect(Unit) {
        viewModel.mensajes.collect { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Nuevo prospecto", style = MaterialTheme.typography.headlineSmall)
            Text("Pendientes de sincronizar: $pendientes")

            Campo("Evento / Colegio (ID)", estado.eventoId, null) { viewModel.onEventoChange(it) }
            Campo("Nombre completo", estado.nombre, estado.errores.nombre) { viewModel.onNombreChange(it) }
            Campo("RUT (12345678-5)", estado.rut, estado.errores.rut) { viewModel.onRutChange(it) }
            Campo("Correo electrónico", estado.correo, estado.errores.correo, KeyboardType.Email) {
                viewModel.onCorreoChange(it)
            }
            Campo("Teléfono (9XXXXXXXX)", estado.telefono, estado.errores.telefono, KeyboardType.Phone) {
                viewModel.onTelefonoChange(it)
            }
            Campo("Colegio de origen", estado.colegio, estado.errores.colegio) { viewModel.onColegioChange(it) }

            Desplegable("Nivel educativo", estado.nivel, Catalogos.niveles, estado.errores.nivel) {
                viewModel.onNivelChange(it)
            }
            Desplegable("Carrera / Área de interés", estado.carrera, Catalogos.carreras, estado.errores.carrera) {
                viewModel.onCarreraChange(it)
            }

            Text("Modalidad", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Catalogos.modalidades.forEach { m ->
                    FilterChip(
                        selected = estado.modalidad == m,
                        onClick = { viewModel.onModalidadChange(m) },
                        label = { Text(m) }
                    )
                }
            }
            estado.errores.modalidad?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Campo("Observaciones (opcional)", estado.observaciones, null) {
                viewModel.onObservacionesChange(it)
            }

            // Casilla de términos: consentimiento para tratar los datos personales
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaTerminos,
                    onCheckedChange = viewModel::onAceptarTerminosChange
                )
                Text("Acepto el tratamiento de mis datos personales")
            }
            estado.errores.aceptaTerminos?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(onClick = { viewModel.guardar() }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar")
            }
            OutlinedButton(
                onClick = { mainViewModel.navigateTo(Screen.Lista) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver registrados")
            }
            OutlinedButton(
                onClick = {mainViewModel.popBackStack()},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}

@Composable
private fun Campo(
    etiqueta: String,
    valor: String,
    error: String?,
    teclado: KeyboardType = KeyboardType.Text,
    onCambio: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Desplegable(
    etiqueta: String,
    seleccionado: String,
    opciones: List<String>,
    error: String?,
    onSeleccion: (String) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
        OutlinedTextField(
            value = seleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { op ->
                DropdownMenuItem(
                    text = { Text(op) },
                    onClick = {
                        onSeleccion(op)
                        abierto = false
                    }
                )
            }
        }
    }
}
