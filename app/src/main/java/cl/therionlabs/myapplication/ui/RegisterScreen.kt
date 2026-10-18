package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.model.Catalogos
import cl.therionlabs.myapplication.model.EventoQr
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import cl.therionlabs.myapplication.viewmodels.RegistroViewModel

@Composable
fun RegisterScreen(
    mainViewModel: MainViewModel,
    viewModel: RegistroViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val evento by mainViewModel.evento.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(evento) {
        evento?.let {
            viewModel.onEventoChange(it.id)
            viewModel.onColegioChange(it.liceo)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.mensajes.collect { snackbarHostState.showSnackbar(it) }
    }

    // Alumno guardado: se abre la pantalla "Prospecto guardado en el dispositivo"
    LaunchedEffect(Unit) {
        viewModel.guardados.collect { resumen ->
            mainViewModel.registrarGuardado(resumen)
            mainViewModel.navigateTo(Screen.Guardado)
        }
    }

    val e = estado.errores
    val cantidadErrores = listOfNotNull(
        e.nombre, e.rut, e.correo, e.telefono, e.nivel, e.carrera, e.aceptaTerminos
    ).size

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { DuocTopBar("Nuevo prospecto") { mainViewModel.popBackStack() } },
        containerColor = DuocFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Con errores se muestra el aviso rojo; sin errores, la tarjeta del evento
            if (cantidadErrores > 0) {
                BannerErrores(cantidadErrores)
            } else {
                evento?.let { TarjetaEvento(it) }
            }

            DuocField(
                etiqueta = "Nombre completo del alumno",
                valor = estado.nombre,
                onCambio = viewModel::onNombreChange,
                placeholder = "Nombre y apellidos",
                error = e.nombre
            )
            DuocField(
                etiqueta = "RUT",
                valor = estado.rut,
                onCambio = viewModel::onRutChange,
                placeholder = "12.345.678-9",
                error = e.rut
            )
            DuocField(
                etiqueta = "Correo",
                valor = estado.correo,
                onCambio = viewModel::onCorreoChange,
                placeholder = "nombre@correo.cl",
                error = e.correo,
                teclado = KeyboardType.Email
            )
            DuocField(
                etiqueta = "Teléfono",
                valor = estado.telefono,
                onCambio = viewModel::onTelefonoChange,
                placeholder = "9XXXXXXXX",
                error = e.telefono,
                teclado = KeyboardType.Phone
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DuocDropdown(
                    etiqueta = "Curso",
                    seleccionado = estado.nivel,
                    opciones = Catalogos.niveles,
                    error = e.nivel,
                    onSeleccion = viewModel::onNivelChange,
                    modifier = Modifier.weight(1f)
                )
                DuocDropdown(
                    etiqueta = "Carrera de interés",
                    seleccionado = estado.carrera,
                    opciones = Catalogos.carreras,
                    error = e.carrera,
                    onSeleccion = viewModel::onCarreraChange,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaTerminos,
                    onCheckedChange = viewModel::onAceptarTerminosChange,
                    colors = CheckboxDefaults.colors(checkedColor = DuocNavy)
                )
                Text(
                    text = "El alumno autoriza que Duoc UC lo contacte.",
                    color = DuocNavy,
                    fontSize = 15.sp
                )
            }
            e.aceptaTerminos?.let {
                Text(it, color = DuocError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            DuocBotonAmarillo(
                texto = if (cantidadErrores > 0) "Corregir y guardar" else "Validar y guardar",
                onClick = { viewModel.guardar() }
            )

            TextButton(
                onClick = { mainViewModel.navigateTo(Screen.Lista) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver registrados", color = DuocGris)
            }
        }
    }
}

@Composable
private fun BannerErrores(cantidad: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFDE7E7))
            .border(1.dp, Color(0xFFF2B8B8), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Default.Warning, contentDescription = null, tint = DuocError)
        Text(
            text = if (cantidad == 1) {
                "Corrige 1 campo para poder guardar al alumno."
            } else {
                "Corrige $cantidad campos para poder guardar al alumno."
            },
            color = Color(0xFF8E1B1B),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TarjetaEvento(evento: EventoQr) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE1E9F7))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(evento.liceo, color = DuocNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(evento.nombre, color = DuocGris, fontSize = 14.sp)
        }
        Text(
            text = evento.id,
            color = DuocNavy,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
