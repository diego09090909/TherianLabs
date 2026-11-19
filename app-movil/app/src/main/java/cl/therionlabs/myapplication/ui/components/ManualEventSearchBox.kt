package cl.therionlabs.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ManualEventSearchBox(
    onSearchSubmitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var eventCode by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "¿No tienes el QR a mano?",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Escribe el código del evento que te dio el liceo. Ej: EVT-0042",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = eventCode,
            onValueChange = { eventCode = it },
            label = { Text("Código del evento") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { if (eventCode.isNotBlank()) onSearchSubmitted(eventCode) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }
    }
}