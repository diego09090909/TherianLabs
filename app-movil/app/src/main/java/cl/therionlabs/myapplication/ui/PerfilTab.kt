package cl.therionlabs.myapplication.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.therionlabs.myapplication.viewmodels.DashboardUiState

@Composable
fun PerfilTab(
    estado: DashboardUiState,
    nombre: String,
    correo: String?,
    onGuardarNombre: (String, (String?) -> Unit) -> Unit,
    onCambiarClave: (String, String, (String?) -> Unit) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    var editandoNombre by remember { mutableStateOf(false) }
    var cambiandoClave by remember { mutableStateOf(false) }
    val correoVisible = correo ?: "Modo prueba (sin sesión)"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DuocNavy)
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(DuocYellow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    nombre.take(2).uppercase(),
                    color = DuocNavy,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                nombre,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(correoVisible, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))
            DuocPill("Captador activo", DuocVerdeSuave, DuocVerde)
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DatoPerfil(estado.total.toString(), "Registros", Modifier.weight(1f))
                DatoPerfil(estado.eventos.size.toString(), "Eventos", Modifier.weight(1f))
                DatoPerfil(estado.porEnviar.toString(), "Por enviar", Modifier.weight(1f))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                FilaPerfil(Icons.Default.Person, "Nombre", nombre, accion = "Editar") { editandoNombre = true }
                FilaPerfil(Icons.Default.Email, "Correo", correoVisible)
                FilaPerfil(
                    Icons.Default.Sync,
                    "Estado de sincronización",
                    if (estado.porEnviar == 0) "Todo enviado al servidor" else "${estado.porEnviar} registros por enviar"
                )
            }

            DuocBotonAzul(texto = "Cambiar contraseña", onClick = { cambiandoClave = true })
            DuocBotonContorno(texto = "Cerrar sesión", onClick = onCerrarSesion, color = DuocError)

            Spacer(Modifier.height(96.dp))
        }
    }

    if (editandoNombre) {
        DialogoNombre(
            nombreActual = nombre,
            onCerrar = { editandoNombre = false },
            onConfirmar = { nuevo, cb ->
                onGuardarNombre(nuevo) { msg ->
                    if (msg == null) Toast.makeText(context, "Nombre actualizado", Toast.LENGTH_SHORT).show()
                    cb(msg)
                }
            }
        )
    }

    if (cambiandoClave) {
        DialogoClave(
            onCerrar = { cambiandoClave = false },
            onConfirmar = { actual, nueva, cb ->
                onCambiarClave(actual, nueva) { msg ->
                    if (msg == null) Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                    cb(msg)
                }
            }
        )
    }
}

@Composable
private fun DialogoNombre(
    nombreActual: String,
    onCerrar: () -> Unit,
    onConfirmar: (String, (String?) -> Unit) -> Unit
) {
    var texto by remember { mutableStateOf(nombreActual) }
    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!cargando) onCerrar() },
        containerColor = Color.White,
        title = { Text("Editar nombre", color = DuocNavy, fontWeight = FontWeight.Bold) },
        text = {
            DuocField(
                etiqueta = "Nombre",
                valor = texto,
                onCambio = { texto = it; error = null },
                placeholder = "Tu nombre",
                error = error,
                imeAction = ImeAction.Done
            )
        },
        confirmButton = {
            TextButton(
                enabled = !cargando,
                onClick = {
                    cargando = true
                    onConfirmar(texto) { msg ->
                        cargando = false
                        if (msg == null) onCerrar() else error = msg
                    }
                }
            ) { Text(if (cargando) "Guardando..." else "Guardar", color = DuocNavy, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(enabled = !cargando, onClick = onCerrar) { Text("Cancelar", color = DuocGris) }
        }
    )
}

@Composable
private fun DialogoClave(
    onCerrar: () -> Unit,
    onConfirmar: (String, String, (String?) -> Unit) -> Unit
) {
    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var repite by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!cargando) onCerrar() },
        containerColor = Color.White,
        title = { Text("Cambiar contraseña", color = DuocNavy, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DuocPasswordField(
                    etiqueta = "Contraseña actual",
                    valor = actual,
                    onCambio = { actual = it; error = null },
                    imeAction = ImeAction.Next
                )
                DuocPasswordField(
                    etiqueta = "Nueva contraseña",
                    valor = nueva,
                    onCambio = { nueva = it; error = null },
                    placeholder = "Mínimo 8 caracteres",
                    imeAction = ImeAction.Next
                )
                DuocPasswordField(
                    etiqueta = "Repite la nueva contraseña",
                    valor = repite,
                    onCambio = { repite = it; error = null },
                    imeAction = ImeAction.Done
                )
                error?.let {
                    Text(it, color = DuocError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !cargando,
                onClick = {
                    val problema = when {
                        actual.isBlank() -> "Ingresa tu contraseña actual."
                        nueva.length < 8 -> "La nueva contraseña debe tener al menos 8 caracteres."
                        nueva != repite -> "Las contraseñas nuevas no coinciden."
                        nueva == actual -> "La nueva contraseña debe ser distinta de la actual."
                        else -> null
                    }
                    if (problema != null) {
                        error = problema
                    } else {
                        cargando = true
                        onConfirmar(actual, nueva) { msg ->
                            cargando = false
                            if (msg == null) onCerrar() else error = msg
                        }
                    }
                }
            ) { Text(if (cargando) "Guardando..." else "Guardar", color = DuocNavy, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(enabled = !cargando, onClick = onCerrar) { Text("Cancelar", color = DuocGris) }
        }
    )
}

@Composable
private fun DatoPerfil(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, color = DuocNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(etiqueta, color = DuocGris, fontSize = 12.sp)
    }
}

@Composable
private fun FilaPerfil(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    accion: String? = null,
    onAccion: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DuocAmarilloSuave),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = DuocNavy, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(etiqueta, color = DuocGris, fontSize = 12.sp)
            Text(valor, color = DuocNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        if (accion != null) {
            TextButton(onClick = onAccion) { Text(accion, color = DuocNavy, fontWeight = FontWeight.Bold) }
        }
    }
}