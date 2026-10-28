package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.model.Catalogos
import cl.therionlabs.myapplication.model.EventoQr
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import cl.therionlabs.myapplication.viewmodels.RegistroViewModel

// Formulario del alumno, igual al cupón de admisión 2027 de Duoc UC:
// datos personales, actividad actual, sede, nivel de interés, 3 carreras y autorización de datos.
@Composable
fun RegisterScreen(
    mainViewModel: MainViewModel,
    viewModel: RegistroViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val carrerasBd by viewModel.carreras.collectAsState()
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

    // Las opciones salen de la tabla "carreras" de la base de datos
    val opcionesCarrera = remember(carrerasBd) {
        carrerasBd.map { OpcionBusqueda(titulo = it.tituloSeleccion(), detalle = it.escuela) }
    }
    val e = estado.errores
    val cantidadErrores = e.cantidad()

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
            if (cantidadErrores > 0) {
                BannerErrores(cantidadErrores)
            } else {
                evento?.let { TarjetaEvento(it) }
            }

            // ---------------- DATOS PERSONALES ----------------
            DuocSeccion("DATOS", "PERSONALES")
            DuocField(
                etiqueta = "RUT",
                valor = estado.rut,
                onCambio = viewModel::onRutChange,
                placeholder = "12345678-9",
                error = e.rut
            )
            DuocOpcionUnica(
                etiqueta = "Sexo",
                opciones = Catalogos.sexos,
                seleccionada = estado.sexo,
                onSeleccion = viewModel::onSexoChange
            )
            DuocField(
                etiqueta = "Nombre",
                valor = estado.nombre,
                onCambio = viewModel::onNombreChange,
                placeholder = "Nombre",
                error = e.nombre
            )
            DuocField(
                etiqueta = "Apellidos",
                valor = estado.apellidos,
                onCambio = viewModel::onApellidosChange,
                placeholder = "Apellido paterno y materno",
                error = e.apellidos
            )
            DuocField(
                etiqueta = "Fecha de nacimiento",
                valor = estado.fechaNacimiento,
                onCambio = viewModel::onFechaNacimientoChange,
                placeholder = "dd-mm-aaaa",
                error = e.fechaNacimiento,
                teclado = KeyboardType.Number
            )
            DuocField(
                etiqueta = "Comuna donde vives",
                valor = estado.comunaResidencia,
                onCambio = viewModel::onComunaResidenciaChange,
                placeholder = "Comuna",
                error = e.comunaResidencia
            )
            DuocField(
                etiqueta = "Celular (+56)",
                valor = estado.telefono,
                onCambio = viewModel::onTelefonoChange,
                placeholder = "9XXXXXXXX",
                error = e.telefono,
                teclado = KeyboardType.Phone
            )
            DuocField(
                etiqueta = "E-mail",
                valor = estado.correo,
                onCambio = viewModel::onCorreoChange,
                placeholder = "nombre@correo.cl",
                error = e.correo,
                teclado = KeyboardType.Email
            )

            // ---------------- ACTIVIDAD ACTUAL ----------------
            DuocSeccion("ACTIVIDAD", "ACTUAL")
            DuocOpcionUnica(
                etiqueta = "",
                opciones = Catalogos.actividades,
                seleccionada = estado.actividad,
                onSeleccion = viewModel::onActividadChange,
                error = e.actividad
            )
            if (estado.actividad == Catalogos.ACTIVIDAD_OTRA) {
                DuocField(
                    etiqueta = "Otra: ¿cuál?",
                    valor = estado.actividadOtra,
                    onCambio = viewModel::onActividadOtraChange,
                    placeholder = "Actividad actual",
                    error = e.actividadOtra
                )
            }
            DuocField(
                etiqueta = "Establecimiento",
                valor = estado.colegio,
                onCambio = viewModel::onColegioChange,
                placeholder = "Nombre del establecimiento",
                error = e.colegio
            )
            DuocField(
                etiqueta = "Comuna de tu establecimiento",
                valor = estado.comunaColegio,
                onCambio = viewModel::onComunaColegioChange,
                placeholder = "Comuna",
                imeAction = ImeAction.Done
            )
            DuocOpcionUnica(
                etiqueta = "¿En qué jornada te gustaría estudiar? Te mostramos los rangos horarios",
                opciones = Catalogos.jornadas,
                seleccionada = estado.jornada,
                onSeleccion = viewModel::onJornadaChange,
                error = e.jornada,
                enColumna = true
            )

            // ---------------- SEDE DE INTERÉS ----------------
            DuocSeccion("SELECCIONA TU", "SEDE DE INTERÉS")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, if (e.sedes != null) DuocError else DuocBorde, RoundedCornerShape(16.dp))
                    .padding(vertical = 8.dp)
            ) {
                Catalogos.sedesPorRegion.forEach { region ->
                    Text(
                        text = region.region,
                        color = DuocNavy,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 2.dp)
                    )
                    region.sedes.forEach { sede ->
                        DuocCasilla(
                            texto = sede.nombre,
                            detalle = sede.comuna,
                            marcada = sede.nombre in estado.sedes,
                            onCambio = { viewModel.onSedeToggle(sede.nombre) }
                        )
                    }
                }
            }
            e.sedes?.let {
                Text(it, color = DuocError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            // ---------------- NIVEL DE INTERÉS ----------------
            DuocSeccion("MARCA TU", "NIVEL DE INTERÉS")
            DuocOpcionUnica(
                etiqueta = "Nivel de interés por estudiar en Duoc UC",
                opciones = Catalogos.nivelesInteres,
                seleccionada = estado.nivelInteres,
                onSeleccion = viewModel::onNivelInteresChange
            )
            DuocOpcionUnica(
                etiqueta = "De las siguientes afirmaciones, ¿con cuál te sientes más identificado?",
                opciones = Catalogos.afirmaciones,
                seleccionada = estado.afirmacion,
                onSeleccion = viewModel::onAfirmacionChange,
                enColumna = true
            )

            // ---------------- CARRERAS (3, por prioridad) ----------------
            DuocSeccion("SELECCIONA", "3 CARRERAS DE TU INTERÉS")
            Text(
                text = "Escribe para buscar y elige las 3 carreras en orden de prioridad (1 = la que más le interesa).",
                color = DuocGris,
                fontSize = 13.sp
            )
            // Cada buscador oculta las carreras ya elegidas en las otras dos opciones
            val c1 = estado.carrera1
            val c2 = estado.carrera2
            val c3 = estado.carrera3
            DuocBuscador(
                etiqueta = "1ª opción (prioridad 1)",
                seleccionado = c1,
                opciones = opcionesCarrera.filter { it.titulo != c2 && it.titulo != c3 },
                error = e.carrera1,
                onSeleccion = { viewModel.onCarreraChange(1, it) },
                placeholder = "Busca tu 1ª carrera"
            )
            DuocBuscador(
                etiqueta = "2ª opción (prioridad 2)",
                seleccionado = c2,
                opciones = opcionesCarrera.filter { it.titulo != c1 && it.titulo != c3 },
                error = e.carrera2,
                onSeleccion = { viewModel.onCarreraChange(2, it) },
                placeholder = "Busca tu 2ª carrera"
            )
            DuocBuscador(
                etiqueta = "3ª opción (prioridad 3)",
                seleccionado = c3,
                opciones = opcionesCarrera.filter { it.titulo != c1 && it.titulo != c2 },
                error = e.carrera3,
                onSeleccion = { viewModel.onCarreraChange(3, it) },
                placeholder = "Busca tu 3ª carrera"
            )

            // ---------------- AUTORIZACIÓN DE DATOS ----------------
            DuocSeccion("AUTORIZACIÓN DE", "DATOS")
            DuocBloqueConsentimiento(
                marcado = estado.aceptaTerminos,
                onCambio = viewModel::onAceptarTerminosChange,
                error = e.aceptaTerminos
            )

            Spacer(Modifier.height(4.dp))
            DuocBotonAmarillo(
                texto = if (cantidadErrores > 0) "Corregir y guardar" else "Validar y guardar",
                onClick = { viewModel.guardar() }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BannerErrores(cantidad: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DuocErrorFondo)
            .border(1.dp, DuocErrorBorde, RoundedCornerShape(16.dp))
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
            color = DuocErrorTexto,
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
            .background(DuocAzulSuave)
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