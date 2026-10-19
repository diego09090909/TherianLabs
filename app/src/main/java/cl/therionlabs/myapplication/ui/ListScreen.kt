package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.data.Estado
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import cl.therionlabs.myapplication.viewmodels.RegistroViewModel

@Composable
fun ListScreen(mainViewModel: MainViewModel, viewModel: RegistroViewModel = viewModel()) {
    val lista by viewModel.prospectos.collectAsState()
    val pendientes by viewModel.pendientes.collectAsState()

    Scaffold(
        topBar = { DuocTopBar("Prospectos y sincronización") { mainViewModel.popBackStack() } },
        containerColor = DuocFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaResumen(
                    numero = lista.size,
                    etiqueta = "Prospectos guardados",
                    colorNumero = DuocNavy,
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumen(
                    numero = pendientes,
                    etiqueta = "Pendientes de sincronizar",
                    colorNumero = if (pendientes > 0) DuocAmarilloTexto else DuocVerde,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Registrados",
                color = DuocNavy,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (lista.isEmpty()) {
                Text(
                    text = "Aún no has registrado alumnos.",
                    color = DuocGris,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, DuocBorde.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                ) {
                    itemsIndexed(lista, key = { _, p -> p.id }) { indice, p ->
                        if (indice > 0) HorizontalDivider(color = DuocBorde.copy(alpha = 0.6f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = p.nombreCompleto,
                                    color = DuocNavy,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${p.rut} · ${p.eventoId}",
                                    color = DuocGris,
                                    fontSize = 13.sp
                                )
                            }
                            if (p.estado == Estado.SINCRONIZADO) {
                                DuocPill("Sincronizado", DuocVerdeSuave, DuocVerde)
                            } else {
                                DuocPill("Pendiente", DuocAmarilloPill, DuocAmarilloTexto)
                            }
                        }
                    }
                }
            }

            Text(
                text = "Sin conexión, los datos se mantienen en el dispositivo y se suben solos al volver la red.",
                color = DuocGris,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TarjetaResumen(
    numero: Int,
    etiqueta: String,
    colorNumero: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, DuocBorde.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = numero.toString(),
            color = colorNumero,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = etiqueta, color = DuocGris, fontSize = 13.sp)
    }
}
