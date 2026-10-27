package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.therionlabs.myapplication.sync.SyncScheduler
import cl.therionlabs.myapplication.viewmodels.DashboardUiState
import cl.therionlabs.myapplication.viewmodels.EventoResumen

@Composable
fun EventosTab(estado: DashboardUiState, onEscanear: () -> Unit) {
    val context = LocalContext.current
    var consulta by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableIntStateOf(0) } // 0 todos, 1 pendientes, 2 sincronizados

    val lista = remember(estado.eventos, consulta, filtro) {
        val q = consulta.trim()
        estado.eventos.filter { e ->
            val coincide = q.isBlank() ||
                    e.colegio.contains(q, ignoreCase = true) ||
                    e.eventoId.contains(q, ignoreCase = true)
            val pasa = when (filtro) {
                1 -> e.pendientes > 0
                2 -> e.pendientes == 0
                else -> true
            }
            coincide && pasa
        }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Eventos", color = DuocNavy, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            OutlinedButton(
                onClick = { SyncScheduler.programarAhora(context) },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, DuocNavy),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DuocNavy)
            ) {
                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Sincronizar", fontWeight = FontWeight.SemiBold)
            }
        }

        OutlinedTextField(
            value = consulta,
            onValueChange = { consulta = it },
            placeholder = { Text("Buscar evento o colegio...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = DuocNavy,
                unfocusedBorderColor = DuocBordeCampo,
                focusedTextColor = DuocNavy,
                unfocusedTextColor = DuocNavy,
                cursorColor = DuocNavy,
                focusedPlaceholderColor = DuocPlaceholder,
                unfocusedPlaceholderColor = DuocPlaceholder,
                focusedLeadingIconColor = DuocGris,
                unfocusedLeadingIconColor = DuocGris
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todos", "Pendientes", "Sincronizados").forEachIndexed { i, texto ->
                ChipFiltro(texto, seleccionado = filtro == i) { filtro = i }
            }
        }

        if (estado.porEnviar > 0) {
            DuocBannerInfo(
                texto = "${estado.porEnviar} registros esperan para enviarse al servidor.",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (estado.eventos.isEmpty()) {
            EstadoVacio(onEscanear)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lista, key = { it.eventoId }) { TarjetaEventoHistorial(it) }
            }
        }
    }
}

@Composable
private fun ChipFiltro(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (seleccionado) DuocNavy else Color.White)
            .border(1.dp, if (seleccionado) DuocNavy else DuocBorde, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            texto,
            color = if (seleccionado) Color.White else DuocNavy,
            fontSize = 13.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun TarjetaEventoHistorial(e: EventoResumen) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (e.pendientes > 0) DuocAmarilloSuave else DuocAzulSuave),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = DuocNavy)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(e.colegio, color = DuocNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${e.eventoId} · ${fechaCorta(e.ultimaFecha)}", color = DuocGris, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.size(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Group, contentDescription = null, tint = DuocGris, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("${e.total} prospectos", color = DuocGris, fontSize = 13.sp)
            }
            if (e.pendientes > 0) {
                DuocPill("${e.pendientes} por enviar", DuocAmarilloPill, DuocAmarilloTexto)
            } else {
                DuocPill("Al día", DuocVerdeSuave, DuocVerde)
            }
        }
    }
}

@Composable
private fun EstadoVacio(onEscanear: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(DuocAmarilloSuave),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = DuocNavy, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.size(14.dp))
        Text("Aún no tienes eventos", color = DuocNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(6.dp))
        Text(
            "Escanea el QR de un evento para empezar a registrar prospectos.",
            color = DuocGris,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.size(16.dp))
        DuocBotonAmarillo(texto = "Escanear QR", onClick = onEscanear)
    }
}