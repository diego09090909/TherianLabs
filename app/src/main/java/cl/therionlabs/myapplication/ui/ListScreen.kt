package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.data.Estado
import cl.therionlabs.myapplication.viewmodels.RegistroViewModel

// RF-04: el captador solo ve Nombre y RUT. Correo y teléfono nunca se muestran aquí.
@Composable
fun ListScreen(viewModel: RegistroViewModel = viewModel()) {
    val lista by viewModel.prospectos.collectAsState()
    val pendientes by viewModel.pendientes.collectAsState()

    Scaffold { padding ->
    Column(modifier = Modifier.padding(padding).padding(16.dp)) {
        Text("Registrados: ${lista.size}", style = MaterialTheme.typography.headlineSmall)
        Text("Pendientes de sincronizar: $pendientes")

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            items(lista, key = { it.id }) { p ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(p.nombreCompleto, style = MaterialTheme.typography.titleMedium)
                            Text(p.rut)
                        }
                        val sincronizado = p.estado == Estado.SINCRONIZADO
                        Text(
                            if (sincronizado) "Sincronizado" else "Pendiente",
                            color = if (sincronizado) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
    }
}
