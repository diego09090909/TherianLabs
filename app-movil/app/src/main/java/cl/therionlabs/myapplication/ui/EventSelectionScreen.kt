package cl.therionlabs.myapplication.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.ui.components.ManualEventSearchBox
import cl.therionlabs.myapplication.viewmodels.EventViewModel
import cl.therionlabs.myapplication.viewmodels.EventUiState

@Composable
fun EventSelectionScreen(
    onEventConfirmed: (String) -> Unit,
    viewModel: EventViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { viewModel.buscarEventoPorCodigo("EVT-0042") }) {
                    Text("Simular Lectura QR (EVT-0042)")
                }
            }
        }


        ManualEventSearchBox(
            onSearchSubmitted = { codigo ->
                viewModel.buscarEventoPorCodigo(codigo)
            }
        )


        when (uiState) {
            is EventUiState.Loading -> CircularProgressIndicator()
            is EventUiState.Success -> {
                val evento = (uiState as EventUiState.Success).evento
                LaunchedEffect(evento) {
                    onEventConfirmed(evento.id)
                }
            }
            is EventUiState.Error -> {
                Text(
                    text = (uiState as EventUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            EventUiState.Idle -> {}
        }
    }
}