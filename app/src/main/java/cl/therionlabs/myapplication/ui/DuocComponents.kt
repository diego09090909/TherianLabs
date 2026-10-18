package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val DuocNavy = Color(0xFF0B2552)
val DuocYellow = Color(0xFFFFC20E)
val DuocFondo = Color(0xFFF3F5FB)
val DuocGris = Color(0xFF6B7490)
val DuocBorde = Color(0xFFCDD3E4)
val DuocError = Color(0xFFC62828)

@Composable
fun DuocTopBar(titulo: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DuocNavy)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }
        Text(
            text = titulo,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DuocBotonAmarillo(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DuocYellow,
            contentColor = DuocNavy,
            disabledContainerColor = DuocYellow.copy(alpha = 0.5f),
            disabledContentColor = DuocNavy.copy(alpha = 0.5f)
        )
    ) {
        Text(texto, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DuocBotonContorno(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, DuocNavy),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = DuocNavy)
    ) {
        Text(texto, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun colorCampo() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorContainerColor = Color.White,
    focusedBorderColor = DuocNavy,
    unfocusedBorderColor = DuocBorde,
    errorBorderColor = DuocError,
    focusedTextColor = DuocNavy,
    unfocusedTextColor = DuocNavy,
    errorTextColor = DuocNavy
)

@Composable
fun DuocField(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    placeholder: String = "",
    error: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(etiqueta, color = DuocNavy, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            placeholder = { Text(placeholder) },
            isError = error != null,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            colors = colorCampo(),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(error, color = DuocError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuocDropdown(
    etiqueta: String,
    seleccionado: String,
    opciones: List<String>,
    error: String?,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var abierto by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(etiqueta, color = DuocNavy, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
            OutlinedTextField(
                value = seleccionado,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Selecciona") },
                isError = error != null,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = colorCampo(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                opciones.forEach { op ->
                    DropdownMenuItem(
                        text = { Text(op) },
                        onClick = {
                            onSeleccion(op)
                            abierto = false
                        }
                    )
                }
            }
        }
        if (error != null) {
            Text(error, color = DuocError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}
