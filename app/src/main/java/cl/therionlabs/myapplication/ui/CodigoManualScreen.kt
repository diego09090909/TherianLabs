package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.therionlabs.myapplication.model.EventoQr
import cl.therionlabs.myapplication.model.ResultadoEvento
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import kotlinx.coroutines.launch

// Pantalla solo para escribir el código del evento (alternativa al escaneo de QR).
@Composable
fun CodigoManualScreen(mainViewModel: MainViewModel) {
    var codigo by remember { mutableStateOf("") }
    var buscando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun abrirEvento(evento: EventoQr) {
        mainViewModel.seleccionarEvento(evento)
        mainViewModel.navigateTo(Screen.EventoDetectado)
    }

    fun buscarEvento() {
        if (codigo.isBlank() || buscando) return
        scope.launch {
            buscando = true
            error = null
            when (val resultado = mainViewModel.resolverEvento(codigo)) {
                is ResultadoEvento.Encontrado -> abrirEvento(resultado.evento)
                is ResultadoEvento.SinConexion -> abrirEvento(resultado.evento)
                ResultadoEvento.Cerrado -> error = "Este evento ya está cerrado. Consulta con la administradora."
                ResultadoEvento.NoExiste -> error = "Ese código de evento no existe. Revisa que esté bien escrito."
            }
            buscando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuocFondo)
            .imePadding()
    ) {
        DuocTopBar("Ingresar código del evento") { mainViewModel.popBackStack() }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Escribe el código del evento",
                color = DuocNavy,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Es el código que te dio el liceo, por ejemplo EVT-0042.",
                color = DuocGris,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(4.dp))
            DuocField(
                etiqueta = "Código del evento",
                valor = codigo,
                onCambio = {
                    codigo = it
                    error = null
                },
                placeholder = "Ej: EVT-0042",
                error = error,
                imeAction = ImeAction.Done,
                modifier = Modifier.fillMaxWidth()
            )
            DuocBotonAmarillo(
                texto = if (buscando) "Buscando…" else "Continuar",
                onClick = { buscarEvento() },
                enabled = codigo.isNotBlank() && !buscando
            )
        }
    }
}