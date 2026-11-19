package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.viewmodels.MainViewModel

@Composable
fun EventoDetectadoScreen(mainViewModel: MainViewModel) {
    val evento by mainViewModel.evento.collectAsState()
    val ev = evento

    LaunchedEffect(ev) {
        if (ev == null) mainViewModel.popBackStack()
    }

    if (ev != null) {
        Scaffold(
            topBar = { DuocTopBar("Evento detectado") { mainViewModel.popBackStack() } },
            containerColor = DuocFondo
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(DuocVerdeSuave),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DuocVerde,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Text(
                        text = "QR leído correctamente",
                        color = DuocNavy,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Confirma que estos datos corresponden al evento donde estás.",
                        color = DuocGris,
                        textAlign = TextAlign.Center
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(1.dp, DuocBorde.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    ) {
                        DatoEvento("Liceo / Colegio", ev.liceo, Modifier.fillMaxWidth().padding(16.dp))
                        HorizontalDivider(color = DuocBorde.copy(alpha = 0.6f))
                        DatoEvento("Evento", ev.nombre, Modifier.fillMaxWidth().padding(16.dp))
                        HorizontalDivider(color = DuocBorde.copy(alpha = 0.6f))
                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                            DatoEvento("ID del evento", ev.id, Modifier.weight(1f).padding(16.dp))
                            VerticalDivider(color = DuocBorde.copy(alpha = 0.6f))
                            DatoEvento("Fecha", ev.fecha, Modifier.weight(1f).padding(16.dp))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, DuocBorde.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = DuocNavy
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Estos datos se guardarán junto a cada alumno que registres en este evento.",
                            color = DuocGris,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                DuocBotonAmarillo(
                    texto = "Comenzar a registrar alumnos",
                    onClick = { mainViewModel.navigateTo(Screen.Register) }
                )
                Spacer(Modifier.height(12.dp))
                DuocBotonContorno(
                    texto = "Escanear otro código",
                    onClick = { mainViewModel.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun DatoEvento(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(etiqueta, color = DuocGris, fontSize = 14.sp)
        Text(valor, color = DuocNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
