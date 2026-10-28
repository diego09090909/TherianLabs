package cl.therionlabs.myapplication.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.therionlabs.myapplication.viewmodels.DashboardUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal fun horaOFecha(ms: Long): String {
    val ahora = Calendar.getInstance()
    val d = Calendar.getInstance().apply { timeInMillis = ms }
    val mismoDia = ahora.get(Calendar.YEAR) == d.get(Calendar.YEAR) &&
            ahora.get(Calendar.DAY_OF_YEAR) == d.get(Calendar.DAY_OF_YEAR)
    return if (mismoDia) {
        SimpleDateFormat("HH:mm", Locale.US).format(Date(ms))
    } else {
        SimpleDateFormat("d MMM", Locale.forLanguageTag("es-CL")).format(Date(ms))
    }
}

internal fun fechaCorta(ms: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es-CL")).format(Date(ms))

@Composable
fun InicioTab(estado: DashboardUiState, nombre: String, onVerProspectos: () -> Unit = {}) {
    var guiaAbierta by rememberSaveable { mutableStateOf(true) }
    val hayRed = rememberHayConexion()
    val tintes = listOf(DuocAzulSuave, DuocVerdeSuave, DuocAmarilloPill)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        DuocCabecera {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 64.dp)
            ) {
                Text(
                    "Hola, $nombre",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 48.dp)
                )
                Text(
                    "Listo para captar prospectos",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
                EstadoRed(hayRed)
            }
        }

        // Las tres tarjetas montan sobre la cabecera
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).solapar(40.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TarjetaDato(estado.hoy.toString(), "Hoy", Icons.Default.Person,
                DuocAzulSuave, DuocAzulAcento, Modifier.weight(1f))
            TarjetaDato(estado.porEnviar.toString(), "Por enviar", Icons.Default.Sync,
                DuocAmarilloPill, DuocAmarilloTexto, Modifier.weight(1f), onVerProspectos)
            TarjetaDato(estado.eventos.size.toString(), "Eventos", Icons.Default.DateRange,
                DuocVerdeSuave, DuocVerde, Modifier.weight(1f))
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TarjetaGuia(abierta = guiaAbierta, onToggle = { guiaAbierta = !guiaAbierta })

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Últimos registros", color = DuocNavy, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "Ver todos",
                    color = DuocAzulAcento,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onVerProspectos() }.padding(vertical = 8.dp)
                )
            }

            if (estado.recientes.isEmpty()) {
                Text(
                    "Todavía no registras prospectos. Toca el botón + para comenzar.",
                    color = DuocGris,
                    fontSize = 14.sp
                )
            } else {
                Column(modifier = Modifier.fillMaxWidth().tarjeta(20.dp)) {
                    estado.recientes.forEachIndexed { i, r ->
                        if (i > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                color = DuocBorde.copy(alpha = 0.5f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(tintes[i % tintes.size]),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    r.nombre.take(1).uppercase(),
                                    color = DuocNavy,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(r.nombre, color = DuocNavy, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${r.colegio} · ${horaOFecha(r.fecha)}", color = DuocGris, fontSize = 12.sp)
                            }
                            if (r.pendiente) {
                                DuocPill("Pendiente", DuocAmarilloPill, DuocAmarilloTexto)
                            } else {
                                DuocPill("Enviado", DuocVerdeSuave, DuocVerde)
                            }
                        }
                    }
                }
            }

            // Espacio para que el botón + no tape el último registro
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun EstadoRed(hayRed: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(start = 9.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (hayRed) Color(0xFF3DE0C8) else DuocYellow)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            if (hayRed) "En línea" else "Sin conexión",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TarjetaDato(
    valor: String,
    etiqueta: String,
    icono: ImageVector,
    fondoIcono: Color,
    colorIcono: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .tarjeta(18.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(fondoIcono),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(valor, color = DuocNavy, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(etiqueta, color = DuocGris, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TarjetaGuia(abierta: Boolean, onToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .tarjeta(20.dp)
            .clickable { onToggle() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(DuocAzulSuave),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = DuocAzulAcento)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("¿Cómo usar la app?", color = DuocNavy, fontWeight = FontWeight.Bold)
                Text("3 pasos, menos de un minuto", color = DuocGris, fontSize = 12.sp)
            }
            Icon(
                imageVector = if (abierta) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (abierta) "Ocultar guía" else "Mostrar guía",
                tint = DuocGris
            )
        }
        AnimatedVisibility(visible = abierta) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PasoGuia(1, "Escanea el QR del evento", "Toca el botón + y apunta al código del liceo.")
                PasoGuia(2, "Registra al alumno", "Completa el cupón. Funciona sin internet.")
                PasoGuia(3, "Sincroniza", "Los datos suben solos cuando hay conexión.")
            }
        }
    }
}

@Composable
private fun PasoGuia(numero: Int, titulo: String, detalle: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape).background(DuocAzulAcento),
            contentAlignment = Alignment.Center
        ) {
            Text(numero.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(titulo, color = DuocNavy, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(detalle, color = DuocGris, fontSize = 12.sp)
        }
    }
}