package cl.therionlabs.myapplication.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.model.Catalogos
import cl.therionlabs.myapplication.model.Comunas
import cl.therionlabs.myapplication.model.EventoQr
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import cl.therionlabs.myapplication.viewmodels.PASOS_FORMULARIO
import cl.therionlabs.myapplication.viewmodels.RegistroViewModel

private val TITULOS_PASO = listOf(
    "¿Quién eres?",
    "Más sobre ti",
    "Contacto",
    "Tu colegio",
    "Tu interés",
    "Sede y carreras",
    "Casi listo"
)
private val SUBTITULOS_PASO = listOf(
    "Con tu RUT quedas registrado en el evento.",
    "Unos datos para conocerte mejor.",
    "Para poder escribirte.",
    "Cuéntanos dónde estudias.",
    "Cuéntanos cuánto te interesa Duoc UC.",
    "Elige dónde y qué te gustaría estudiar.",
    "Revisa y confirma tu ficha."
)

@Composable
fun RegisterScreen(
    mainViewModel: MainViewModel,
    viewModel: RegistroViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val carrerasBd by viewModel.carreras.collectAsState()
    val evento by mainViewModel.evento.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var paso by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(evento) {
        evento?.let {
            viewModel.onEventoChange(it.id)
            viewModel.onColegioChange(it.liceo)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.mensajes.collect { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.guardados.collect { resumen ->
            paso = 0
            mainViewModel.registrarGuardado(resumen)
            mainViewModel.navigateTo(Screen.Guardado)
        }
    }

    val volver: () -> Unit = {
        if (paso > 0) paso-- else mainViewModel.popBackStack()
    }
    BackHandler(enabled = paso > 0) { paso-- }

    val opcionesCarrera = remember(carrerasBd) {
        carrerasBd.map { OpcionBusqueda(titulo = it.tituloSeleccion(), detalle = it.escuela) }
    }
    val e = estado.errores
    val esUltimo = paso == PASOS_FORMULARIO - 1

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { CabeceraFormulario(paso = paso, onBack = volver) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (paso > 0) {
                    DuocBotonContorno(
                        texto = "Atrás",
                        onClick = { paso-- },
                        modifier = Modifier.weight(0.4f)
                    )
                }
                DuocBotonAmarillo(
                    texto = if (esUltimo) "Enviar ficha" else "Siguiente",
                    onClick = {
                        if (!esUltimo) {
                            if (viewModel.validarPaso(paso)) paso++
                        } else {
                            val malo = viewModel.primerPasoInvalido()
                            if (malo == null) {
                                viewModel.guardar()
                            } else {
                                paso = malo
                                viewModel.validarPaso(malo)
                            }
                        }
                    },
                    modifier = Modifier.weight(0.6f)
                )
            }
        },
        containerColor = DuocFondo
    ) { padding ->
        AnimatedContent(
            targetState = paso,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()) togetherWith
                        (slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "pasoFormulario"
        ) { p ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = TITULOS_PASO[p],
                        color = DuocNavy,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(text = SUBTITULOS_PASO[p], color = DuocGris, fontSize = 15.sp)
                }

                when (p) {
                    0 -> {
                        evento?.let { TarjetaEvento(it) }
                        TarjetaPaso {
                            DuocField(
                                etiqueta = "RUT",
                                valor = estado.rut,
                                onCambio = viewModel::onRutChange,
                                placeholder = "12345678-9",
                                error = e.rut
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
                                error = e.apellidos,
                                imeAction = ImeAction.Done
                            )
                        }
                    }

                    1 -> TarjetaPaso {
                        DuocOpcionUnica(
                            etiqueta = "Sexo",
                            opciones = Catalogos.sexos,
                            seleccionada = estado.sexo,
                            onSeleccion = viewModel::onSexoChange
                        )
                        DuocField(
                            etiqueta = "Fecha de nacimiento",
                            valor = estado.fechaNacimiento,
                            onCambio = viewModel::onFechaNacimientoChange,
                            placeholder = "dd-mm-aaaa",
                            error = e.fechaNacimiento,
                            teclado = KeyboardType.Number
                        )
                        DuocBuscador(
                            etiqueta = "Comuna donde vives",
                            seleccionado = estado.comunaResidencia,
                            opciones = Comunas.opciones,
                            onSeleccion = viewModel::onComunaResidenciaChange,
                            placeholder = "Busca tu comuna",
                            error = e.comunaResidencia
                        )
                    }

                    2 -> TarjetaPaso {
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
                            teclado = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        )
                    }

                    3 -> TarjetaPaso {
                        DuocOpcionUnica(
                            etiqueta = "¿Qué haces hoy?",
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
                        val etiquetaColegio = when (estado.actividad) {
                            Catalogos.ACTIVIDAD_EGRESADO -> "Colegio donde egresaste"
                            Catalogos.ACTIVIDAD_OTRA -> "Establecimiento (opcional)"
                            else -> "Establecimiento"
                        }
                        val etiquetaComuna = when (estado.actividad) {
                            Catalogos.ACTIVIDAD_EGRESADO -> "Comuna del colegio donde egresaste"
                            Catalogos.ACTIVIDAD_OTRA -> "Comuna del establecimiento (opcional)"
                            else -> "Comuna de tu establecimiento"
                        }
                        DuocField(
                            etiqueta = etiquetaColegio,
                            valor = estado.colegio,
                            onCambio = viewModel::onColegioChange,
                            placeholder = "Nombre del establecimiento",
                            error = e.colegio
                        )
                        DuocBuscador(
                            etiqueta = etiquetaComuna,
                            seleccionado = estado.comunaColegio,
                            opciones = Comunas.opciones,
                            onSeleccion = viewModel::onComunaColegioChange,
                            placeholder = "Busca la comuna"
                        )
                        DuocOpcionUnica(
                            etiqueta = "¿En qué jornada te gustaría estudiar?",
                            opciones = Catalogos.jornadas,
                            seleccionada = estado.jornada,
                            onSeleccion = viewModel::onJornadaChange,
                            error = e.jornada,
                            enColumna = true
                        )
                    }

                    4 -> TarjetaPaso {
                        DuocOpcionUnica(
                            etiqueta = "Nivel de interés por estudiar en Duoc UC",
                            opciones = Catalogos.nivelesInteres,
                            seleccionada = estado.nivelInteres,
                            onSeleccion = viewModel::onNivelInteresChange
                        )
                        DuocOpcionUnica(
                            etiqueta = "¿Con cuál afirmación te sientes más identificado?",
                            opciones = Catalogos.afirmaciones,
                            seleccionada = estado.afirmacion,
                            onSeleccion = viewModel::onAfirmacionChange,
                            enColumna = true
                        )
                    }

                    5 -> {
                        TarjetaPaso {
                            DuocSeccion("SELECCIONA TU", "SEDE DE INTERÉS")
                            SedesChips(
                                seleccionadas = estado.sedes,
                                error = e.sedes,
                                onToggle = viewModel::onSedeToggle
                            )
                        }
                        TarjetaPaso {
                            DuocSeccion("ELIGE", "3 CARRERAS")
                            Text(
                                text = "Escribe para buscar. La 1ª es la que más te interesa.",
                                color = DuocGris,
                                fontSize = 13.sp
                            )
                            val c1 = estado.carrera1
                            val c2 = estado.carrera2
                            val c3 = estado.carrera3
                            DuocBuscador(
                                etiqueta = "1ª opción",
                                seleccionado = c1,
                                opciones = opcionesCarrera.filter { it.titulo != c2 && it.titulo != c3 },
                                error = e.carrera1,
                                onSeleccion = { viewModel.onCarreraChange(1, it) },
                                placeholder = "Busca tu 1ª carrera"
                            )
                            DuocBuscador(
                                etiqueta = "2ª opción",
                                seleccionado = c2,
                                opciones = opcionesCarrera.filter { it.titulo != c1 && it.titulo != c3 },
                                error = e.carrera2,
                                onSeleccion = { viewModel.onCarreraChange(2, it) },
                                placeholder = "Busca tu 2ª carrera"
                            )
                            DuocBuscador(
                                etiqueta = "3ª opción",
                                seleccionado = c3,
                                opciones = opcionesCarrera.filter { it.titulo != c1 && it.titulo != c2 },
                                error = e.carrera3,
                                onSeleccion = { viewModel.onCarreraChange(3, it) },
                                placeholder = "Busca tu 3ª carrera"
                            )
                        }
                    }

                    else -> {
                        ResumenFinal(
                            nombre = "${estado.nombre} ${estado.apellidos}".trim(),
                            rut = estado.rut,
                            carrera = estado.carrera1
                        )
                        DuocBloqueConsentimiento(
                            marcado = estado.aceptaTerminos,
                            onCambio = viewModel::onAceptarTerminosChange,
                            error = e.aceptaTerminos
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CabeceraFormulario(paso: Int, onBack: () -> Unit) {
    DuocCabecera(circulo = 84.dp) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Text(
                    "Nuevo prospecto",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.height(6.dp))
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "PASO ${paso + 1} DE $PASOS_FORMULARIO",
                    color = DuocTurquesa,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(PASOS_FORMULARIO) { i ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (i <= paso) DuocTurquesa else Color.White.copy(alpha = 0.28f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaPaso(contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .tarjeta()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = contenido
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SedesChips(
    seleccionadas: Set<String>,
    error: String?,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Catalogos.sedesPorRegion.forEach { region ->
            Text(
                text = region.region,
                color = DuocGris,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                region.sedes.forEach { sede ->
                    val marcada = sede.nombre in seleccionadas
                    val forma = RoundedCornerShape(50)
                    Box(
                        modifier = Modifier
                            .heightIn(min = 44.dp)
                            .clip(forma)
                            .background(if (marcada) DuocAzulAcento else Color.White)
                            .border(
                                1.dp,
                                when {
                                    marcada -> DuocAzulAcento
                                    error != null -> DuocError
                                    else -> DuocBordeCampo
                                },
                                forma
                            )
                            .toggleable(
                                value = marcada,
                                role = Role.Checkbox,
                                onValueChange = { onToggle(sede.nombre) }
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sede.nombre,
                            color = if (marcada) Color.White else DuocAzulAcento,
                            fontSize = 15.sp,
                            fontWeight = if (marcada) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
        error?.let {
            Text(it, color = DuocError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ResumenFinal(nombre: String, rut: String, carrera: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .tarjeta()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(nombre, color = DuocNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(rut, color = DuocGris, fontSize = 14.sp)
        if (carrera.isNotBlank()) {
            Text(carrera, color = DuocGris, fontSize = 14.sp)
        }
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
