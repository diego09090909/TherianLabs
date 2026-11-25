package cl.therionlabs.myapplication.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.data.Estado
import cl.therionlabs.myapplication.data.Prospecto
import cl.therionlabs.myapplication.util.EdicionProspectoValidator
import cl.therionlabs.myapplication.viewmodels.RegistroViewModel

@Composable
fun ProspectosTab(viewModel: RegistroViewModel = viewModel()) {
    val lista by viewModel.prospectos.collectAsState()
    val pendientes by viewModel.pendientes.collectAsState()
    val rechazados = lista.count { it.estado == Estado.RECHAZADO }
    val sincronizando by viewModel.sincronizando.collectAsState()
    val hayRed = rememberHayConexion()
    val snackbar = remember { SnackbarHostState() }
    var porEditar by remember { mutableStateOf<Prospecto?>(null) }
    var porEliminar by remember { mutableStateOf<Prospecto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.mensajes.collect { snackbar.showSnackbar(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            DuocCabecera {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding()
                        .padding(horizontal = 20.dp).padding(top = 20.dp, bottom = 60.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Prospectos", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Registros y sincronización",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                    Button(
                        onClick = { viewModel.sincronizarAhora() },
                        enabled = !sincronizando,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DuocYellow,
                            contentColor = DuocNavy,
                            disabledContainerColor = DuocYellow.copy(alpha = 0.7f),
                            disabledContentColor = DuocNavy
                        )
                    ) {
                        if (sincronizando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = DuocNavy
                            )
                        } else {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(6.dp))
                        Text("Sincronizar", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 16.dp).solapar(36.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TarjetaCifra(
                    lista.size.toString(), "Guardados", Icons.Default.Group,
                    DuocAzulSuave, DuocAzulAcento, Modifier.weight(1f)
                )
                TarjetaCifra(
                    pendientes.toString(), "Por enviar", Icons.Default.Schedule,
                    DuocAmarilloPill, DuocAmarilloTexto, Modifier.weight(1f)
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, top = 14.dp, bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { EstadoConexion(sincronizando, hayRed, pendientes, rechazados) }
                item {
                    Text(
                        "Registrados",
                        color = DuocNavy,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                if (lista.isEmpty()) {
                    item {
                        Text(
                            "Aún no has registrado alumnos. Toca el botón + para comenzar.",
                            color = DuocGris,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(lista, key = { it.id }) { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .tarjeta(16.dp)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(DuocAzulSuave),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    p.nombreCompleto.take(1).uppercase(),
                                    color = DuocNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    p.nombreCompleto,
                                    color = DuocNavy,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "${p.rut} · ${p.eventoId} · ${horaOFecha(p.fechaCaptura)}",
                                    color = DuocGris,
                                    fontSize = 12.sp
                                )
                                if (p.estado == Estado.RECHAZADO && p.motivoRechazo.isNotBlank()) {
                                    Text(
                                        p.motivoRechazo,
                                        color = DuocErrorTexto,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            when (p.estado) {
                                Estado.SINCRONIZADO -> DuocPill("Sincronizado", DuocVerdeSuave, DuocVerde)
                                Estado.RECHAZADO -> DuocPill("Rechazado", DuocErrorFondo, DuocErrorTexto)
                                else -> DuocPill("Pendiente", DuocAmarilloPill, DuocAmarilloTexto)
                            }
                            IconButton(onClick = { porEditar = p }, modifier = Modifier.size(34.dp)) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar prospecto",
                                    tint = DuocAzulAcento,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(onClick = { porEliminar = p }, modifier = Modifier.size(34.dp)) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar prospecto",
                                    tint = DuocErrorTexto,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        porEditar?.let { p ->
            DialogEditarProspecto(p, viewModel, onCerrar = { porEditar = null })
        }

        porEliminar?.let { p ->
            AlertDialog(
                onDismissRequest = { porEliminar = null },
                title = { Text("Eliminar prospecto") },
                text = {
                    Text(
                        if (p.estado == Estado.SINCRONIZADO) {
                            "Se borrará a ${p.nombreCompleto} del teléfono y del servidor. Necesitas conexión."
                        } else {
                            "Se borrará a ${p.nombreCompleto} del teléfono. Aún no estaba en el servidor."
                        }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.eliminarProspecto(p)
                        porEliminar = null
                    }) { Text("Eliminar", color = DuocErrorTexto) }
                },
                dismissButton = { TextButton(onClick = { porEliminar = null }) { Text("Cancelar") } }
            )
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun TarjetaCifra(
    valor: String, etiqueta: String, icono: ImageVector,
    fondoIcono: Color, colorIcono: Color, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.tarjeta(18.dp).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(fondoIcono),
            contentAlignment = Alignment.Center
        ) { Icon(icono, contentDescription = null, tint = colorIcono) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(valor, color = DuocNavy, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(etiqueta, color = DuocGris, fontSize = 12.sp)
        }
    }
}

@Composable
private fun EstadoConexion(sincronizando: Boolean, hayRed: Boolean, pendientes: Int, rechazados: Int) {
    val fondo: Color
    val colorIcono: Color
    val icono: ImageVector
    val titulo: String
    val detalle: String
    when {
        sincronizando -> {
            fondo = DuocAzulSuave; colorIcono = DuocNavy; icono = Icons.Default.Sync
            titulo = "Sincronizando"; detalle = "Enviando los registros al servidor…"
        }
        !hayRed -> {
            fondo = DuocAmarilloSuave; colorIcono = DuocAmarilloTexto; icono = Icons.Default.CloudOff
            titulo = "Sin conexión"
            detalle = "Los registros están guardados en el teléfono y se enviarán al volver la red."
        }
        pendientes > 0 -> {
            fondo = DuocAzulSuave; colorIcono = DuocNavy; icono = Icons.Default.Wifi
            titulo = "Conexión detectada"
            detalle = "$pendientes por enviar. Si no suben solos, toca Sincronizar."
        }
        rechazados > 0 -> {
            fondo = DuocErrorFondo; colorIcono = DuocErrorTexto; icono = Icons.Default.Warning
            titulo = if (rechazados == 1) "Hay un registro rechazado" else "Hay registros rechazados"
            detalle = if (rechazados == 1) {
                "1 no fue aceptado por el servidor. El motivo aparece en el registro."
            } else {
                "$rechazados no fueron aceptados por el servidor. El motivo aparece en cada registro."
            }
        }
        else -> {
            fondo = DuocVerdeSuave; colorIcono = DuocVerde; icono = Icons.Default.CloudDone
            titulo = "Todo al día"; detalle = "Todos los prospectos ya están en el servidor."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(fondo)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = colorIcono)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(titulo, color = DuocNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(detalle, color = DuocGris, fontSize = 13.sp)
            }
        }
        if (sincronizando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = DuocTurquesa,
                trackColor = Color.White
            )
        }
    }
}

// Se actualiza sola cuando el teléfono gana o pierde conexión
@Composable
internal fun rememberHayConexion(): Boolean {
    val context = LocalContext.current
    var hayRed by remember { mutableStateOf(hayInternet(context)) }

    DisposableEffect(context) {
        val cm = context.getSystemService(ConnectivityManager::class.java)
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { hayRed = true }
            override fun onLost(network: Network) { hayRed = hayInternet(context) }
        }
        cm.registerDefaultNetworkCallback(callback)
        onDispose { cm.unregisterNetworkCallback(callback) }
    }
    return hayRed
}

internal fun hayInternet(context: Context): Boolean {
    val cm = context.getSystemService(ConnectivityManager::class.java)
    val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
// Formulario de edicion: correo, celular y nivel de interes (UPDATE contra ms-prospectos).
@Composable
private fun DialogEditarProspecto(p: Prospecto, viewModel: RegistroViewModel, onCerrar: () -> Unit) {
    val (correoInicial, telefonoInicial) = remember(p.id) { viewModel.contactoDe(p) }
    var correo by remember { mutableStateOf(correoInicial) }
    var telefono by remember { mutableStateOf(telefonoInicial) }
    var nivel by remember { mutableStateOf(p.nivelInteres) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Editar prospecto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(p.nombreCompleto, color = DuocGris, fontSize = 13.sp)
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Celular") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Text("Nivel de interés", color = DuocNavy, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EdicionProspectoValidator.NIVELES.forEach { n ->
                        Row(
                            modifier = Modifier.clickable { nivel = n }.padding(end = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = nivel == n, onClick = { nivel = n })
                            Text(n, fontSize = 13.sp)
                        }
                    }
                }
                error?.let { Text(it, color = DuocErrorTexto, fontSize = 12.sp) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val problema = EdicionProspectoValidator.validar(correo, telefono, nivel)
                if (problema != null) {
                    error = problema
                } else {
                    viewModel.editarProspecto(p, correo, telefono, nivel)
                    onCerrar()
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}
