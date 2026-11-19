package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

val DuocNavy = Color(0xFF0A2250)          // barras, títulos, fondo del inicio
val DuocAzulAcento = Color(0xFF193E85)    // círculo decorativo
val DuocAzulSuave = Color(0xFFE1E9F7)     // tarjeta del evento
val DuocYellow = Color(0xFFFEC10E)        // botón principal
val DuocAmarilloSuave = Color(0xFFFEF5D4) // avisos informativos
val DuocAmarilloPill = Color(0xFFFDEFC0)  // etiqueta "Pendiente"
val DuocAmarilloTexto = Color(0xFF7A5200) // texto sobre amarillo suave (6,0:1)
val DuocFondo = Color(0xFFF4F5FA)         // fondo de las pantallas
val DuocGris = Color(0xFF5B6482)          // texto secundario (5,4:1 sobre el fondo)
val DuocBorde = Color(0xFFCDD3E4)         // bordes decorativos de tarjetas
val DuocBordeCampo = Color(0xFF868FAB)    // borde de campos de texto (3,2:1 sobre blanco)
val DuocPlaceholder = Color(0xFF6F7894)   // texto de ejemplo dentro de campos
val DuocError = Color(0xFFC62828)
val DuocErrorFondo = Color(0xFFFDE7E7)
val DuocErrorBorde = Color(0xFFF2B8B8)
val DuocErrorTexto = Color(0xFF8E1B1B)
val DuocVerde = Color(0xFF1B6B3A)         // texto sobre verde suave (5,6:1)
val DuocVerdeSuave = Color(0xFFDCF2E5)    // etiqueta "Sincronizado"
val DuocTurquesa = Color(0xFF3DE0C8)      // círculo pequeño de la cabecera, check, barra de progreso

val DuocDegradado = Brush.linearGradient(listOf(DuocNavy, DuocAzulAcento))




// Cabecera con degradado y los dos círculos decorativos
@Composable
fun DuocCabecera(
    modifier: Modifier = Modifier,
    circulo: Dp = 150.dp,
    contenido: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxWidth().clipToBounds().background(DuocDegradado)) {
        Box(
            Modifier.align(Alignment.TopEnd)
                .offset(x = circulo * 0.2f, y = -(circulo * 0.3f))
                .size(circulo).clip(CircleShape)
                .background(DuocYellow.copy(alpha = 0.92f))
        )
        Box(
            Modifier.align(Alignment.BottomEnd)
                .offset(x = -(circulo * 0.57f), y = circulo * 0.2f)
                .size(circulo * 0.44f).clip(CircleShape)
                .background(DuocTurquesa.copy(alpha = 0.85f))
        )
        contenido()
    }
}

// Tarjeta blanca con sombra suave
fun Modifier.tarjeta(radio: Dp = 18.dp): Modifier {
    val forma = RoundedCornerShape(radio)
    return this
        .shadow(6.dp, forma, ambientColor = DuocNavy.copy(alpha = 0.10f), spotColor = DuocNavy.copy(alpha = 0.10f))
        .clip(forma)
        .background(Color.White)
}

// Sube un bloque sobre el anterior (para las tarjetas que montan sobre la cabecera)
fun Modifier.solapar(arriba: Dp): Modifier = this.layout { medida, restricciones ->
    val p = medida.measure(restricciones)
    val a = arriba.roundToPx()
    layout(p.width, p.height - a) { p.place(0, -a) }
}
@Composable
fun DuocTopBar(titulo: String, onBack: () -> Unit) {
    DuocCabecera(circulo = 96.dp) {
        Row(
            modifier = Modifier.statusBarsPadding().padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text(titulo, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
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
fun DuocBotonAzul(
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
            containerColor = DuocNavy,
            contentColor = Color.White,
            disabledContainerColor = DuocNavy.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        )
    ) {
        Text(texto, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DuocBotonContorno(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = DuocNavy
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Text(texto, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DuocPill(texto: String, fondo: Color, colorTexto: Color, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        color = colorTexto,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
fun DuocBannerInfo(texto: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DuocAmarilloSuave)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = DuocAmarilloTexto
        )
        Spacer(Modifier.width(10.dp))
        Text(texto, color = DuocNavy, fontSize = 14.sp)
    }
}

@Composable
private fun colorCampo() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorContainerColor = Color.White,
    focusedBorderColor = DuocNavy,
    unfocusedBorderColor = DuocBordeCampo,
    errorBorderColor = DuocError,
    focusedTextColor = DuocNavy,
    unfocusedTextColor = DuocNavy,
    errorTextColor = DuocNavy,
    cursorColor = DuocNavy,
    focusedPlaceholderColor = DuocPlaceholder,
    unfocusedPlaceholderColor = DuocPlaceholder,
    errorPlaceholderColor = DuocPlaceholder
)

@Composable
fun DuocField(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    placeholder: String = "",
    error: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    modifier: Modifier = Modifier
) {
    val foco = LocalFocusManager.current

    var interno by remember { mutableStateOf(TextFieldValue(valor, TextRange(valor.length))) }

    val mostrado = if (interno.text == valor) {
        interno
    } else {
        interno.copy(text = valor, selection = TextRange(valor.length))
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(etiqueta, color = DuocNavy, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = mostrado,
            onValueChange = { nuevo ->
                interno = nuevo
                onCambio(nuevo.text)
            },
            placeholder = { Text(placeholder) },
            isError = error != null,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = teclado, imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { foco.moveFocus(FocusDirection.Down) },
                onDone = { foco.clearFocus() }
            ),
            colors = colorCampo(),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(error, color = DuocError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun DuocPasswordField(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    placeholder: String = "",
    error: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    modifier: Modifier = Modifier
) {
    val foco = LocalFocusManager.current
    var visible by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(etiqueta, color = DuocNavy, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            placeholder = { Text(placeholder) },
            isError = error != null,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (visible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { foco.moveFocus(FocusDirection.Down) },
                onDone = { foco.clearFocus() }
            ),
            trailingIcon = {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = DuocGris
                    )
                }
            },
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