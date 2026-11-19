package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.therionlabs.myapplication.viewmodels.MainViewModel

@Composable
fun EventHistoryScreen(
    mainViewModel: MainViewModel
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezado y Botón Manual de Sincronización
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historial de Eventos",
                style = MaterialTheme.typography.titleLarge
            )
            Button(
                onClick = { /* Invoca SyncWorker o petición manual */ }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sincronizar")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Buscador para filtrar la lista
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar evento o colegio...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista en tarjetas de los eventos asistidos por el captador
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Aquí se itera sobre la lista del ViewModel según el filtro de búsqueda
        }
    }
}