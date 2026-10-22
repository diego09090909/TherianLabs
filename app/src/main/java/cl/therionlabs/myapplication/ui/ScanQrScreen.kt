package cl.therionlabs.myapplication.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.runtime.LaunchedEffect
import org.json.JSONObject
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.therionlabs.myapplication.model.EventoQr
import cl.therionlabs.myapplication.model.EventosDemo
import cl.therionlabs.myapplication.model.ResultadoEvento
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import kotlinx.coroutines.launch

private val FondoScan = Color(0xFF061633)
private val PanelScan = Color(0xFF0E2556)

@Composable
fun ScanQrScreen(mainViewModel: MainViewModel) {
    var codigo by remember { mutableStateOf("") }
    var buscando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // ----- Permiso de cámara -----
    fun tienePermiso() = ContextCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    var permitido by remember { mutableStateOf(tienePermiso()) }
    var rechazado by remember { mutableStateOf(false) }
    var ultimoQr by remember { mutableStateOf<String?>(null) }

    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permitido = concedido
        rechazado = !concedido
    }

    // Al abrir la pantalla se pide el permiso (si aún no está concedido)
    LaunchedEffect(Unit) {
        if (!permitido) pedirPermiso.launch(Manifest.permission.CAMERA)
    }

    // Si el usuario lo activó desde Ajustes y vuelve a la app
    LifecycleResumeEffect(Unit) {
        permitido = tienePermiso()
        onPauseOrDispose { }
    }

    fun abrirAjustes() {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", context.packageName, null))
        )
    }

    fun abrirEvento(evento: EventoQr) {
        mainViewModel.seleccionarEvento(evento)
        mainViewModel.navigateTo(Screen.EventoDetectado)
    }

    fun buscarEvento(texto: String) {
        scope.launch {
            buscando = true
            error = null
            when (val resultado = mainViewModel.resolverEvento(texto)) {
                is ResultadoEvento.Encontrado -> abrirEvento(resultado.evento)
                is ResultadoEvento.SinConexion -> abrirEvento(resultado.evento)
                ResultadoEvento.Cerrado -> error = "Este evento ya está cerrado. Consulta con la administradora."
                ResultadoEvento.NoExiste -> error = "Ese código de evento no existe. Revisa que esté bien escrito."
            }
            buscando = false
        }
    }

    // El QR del panel trae un JSON {"id": "...", "liceo": "...", ...}; también se acepta el código suelto.
    fun alLeerQr(texto: String) {
        if (buscando || texto == ultimoQr) return
        ultimoQr = texto
        val id = runCatching { JSONObject(texto).getString("id") }.getOrDefault(texto)
        codigo = id.trim().uppercase()
        buscarEvento(id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoScan)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { mainViewModel.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Text(
                text = "Escanear QR del evento",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Apunta al código QR",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Ubícalo dentro del recuadro. El código trae el ID del evento y los datos del liceo.",
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))

            if (permitido) {
                MarcoEscaner(conCamara = true, onClick = {}) {
                    CamaraQr(
                        activo = !buscando,
                        onQrLeido = ::alLeerQr,
                        modifier = Modifier.matchParentSize()
                    )
                }
                if (buscando) {
                    Spacer(Modifier.height(16.dp))
                    Text("Buscando evento…", color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp)
                }
            } else {
                MarcoEscaner(conCamara = false, onClick = { pedirPermiso.launch(Manifest.permission.CAMERA) })
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Necesitamos usar la cámara para escanear el QR del evento.",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { pedirPermiso.launch(Manifest.permission.CAMERA) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DuocYellow, contentColor = DuocNavy)
                ) {
                    Text("Activar cámara", fontWeight = FontWeight.Bold)
                }
                if (rechazado) {
                    Spacer(Modifier.height(8.dp))
                    // Si marcó "No volver a preguntar", solo se puede activar desde Ajustes
                    OutlinedButton(onClick = { abrirAjustes() }, shape = RoundedCornerShape(16.dp)) {
                        Text("Abrir ajustes de la app", color = Color.White)
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(PanelScan)
                .padding(16.dp)
        ) {
            Text(
                text = "¿No tienes el QR a mano?",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Escribe el código del evento que te dio el liceo.",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it; error = null },
                    placeholder = { Text("Ej: EVT-0042") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = DuocYellow,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = DuocYellow,
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { buscarEvento(codigo) },
                    enabled = codigo.isNotBlank() && !buscando,
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DuocYellow,
                        contentColor = DuocNavy,
                        disabledContainerColor = DuocYellow.copy(alpha = 0.4f),
                        disabledContentColor = DuocNavy.copy(alpha = 0.5f)
                    )
                ) {
                    Text(if (buscando) "Buscando…" else "Continuar", fontWeight = FontWeight.Bold)
                }
            }
            error?.let {
                Spacer(Modifier.height(10.dp))
                Text(text = it, color = Color(0xFFFF8A80), fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun MarcoEscaner(
    conCamara: Boolean,
    onClick: () -> Unit,
    contenido: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(260.dp)
            .clickable(enabled = !conCamara, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PanelScan),
            content = contenido
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val trazo = 6.dp.toPx()
            val largo = 44.dp.toPx()
            val margen = trazo / 2

            fun esquina(x: Float, y: Float, dx: Float, dy: Float) {
                drawLine(DuocYellow, Offset(x, y), Offset(x + dx * largo, y), trazo, StrokeCap.Round)
                drawLine(DuocYellow, Offset(x, y), Offset(x, y + dy * largo), trazo, StrokeCap.Round)
            }
            esquina(margen, margen, 1f, 1f)
            esquina(w - margen, margen, -1f, 1f)
            esquina(margen, h - margen, 1f, -1f)
            esquina(w - margen, h - margen, -1f, -1f)

            if (conCamara) return@Canvas

            val qr = Color.White.copy(alpha = 0.28f)
            val lado = w * 0.2f

            fun finder(x: Float, y: Float) {
                drawRect(qr, Offset(x, y), Size(lado, lado), style = Stroke(width = lado * 0.16f))
                drawRect(
                    qr,
                    Offset(x + lado * 0.32f, y + lado * 0.32f),
                    Size(lado * 0.36f, lado * 0.36f)
                )
            }
            finder(w * 0.24f, h * 0.24f)
            finder(w * 0.56f, h * 0.24f)
            finder(w * 0.24f, h * 0.56f)
            drawRect(qr, Offset(w * 0.52f, h * 0.58f), Size(lado * 0.4f, lado * 0.4f))
            drawRect(qr, Offset(w * 0.64f, h * 0.62f), Size(lado * 0.3f, lado * 0.5f))
            drawRect(qr, Offset(w * 0.50f, h * 0.70f), Size(lado * 0.5f, lado * 0.3f))

            drawLine(
                DuocYellow,
                Offset(w * 0.12f, h * 0.5f),
                Offset(w * 0.88f, h * 0.5f),
                3.dp.toPx(),
                StrokeCap.Round
            )
        }
    }
}