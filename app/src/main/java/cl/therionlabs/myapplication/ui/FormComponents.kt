package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.therionlabs.myapplication.data.CarreraEntity
import cl.therionlabs.myapplication.model.OpcionForm
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextDecoration

// Componentes propios del formulario del cupón de admisión.
// Usan la misma paleta de DuocComponents.kt.

// ---------------------------------------------------------------------------
// Encabezado de sección, como las franjas del cupón: "DATOS PERSONALES"
// ---------------------------------------------------------------------------
@Composable
fun DuocSeccion(normal: String, destacado: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DuocAmarilloSuave)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(DuocYellow)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = buildAnnotatedString {
                append("$normal ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(destacado) }
            },
            color = DuocNavy,
            fontSize = 15.sp
        )
    }
}

// ---------------------------------------------------------------------------
// Elegir UNA opción (sexo, actividad, jornada, nivel de interés, afirmación)
// ---------------------------------------------------------------------------
@Composable
fun DuocOpcionUnica(
    etiqueta: String,
    opciones: List<OpcionForm>,
    seleccionada: String,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enColumna: Boolean = false
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (etiqueta.isNotEmpty()) {
            Text(etiqueta, color = DuocNavy, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        if (enColumna) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                opciones.forEach { op ->
                    ChipOpcion(op, op.valor == seleccionada, true, { onSeleccion(op.valor) }, Modifier.fillMaxWidth())
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                opciones.forEach { op ->
                    ChipOpcion(op, op.valor == seleccionada, false, { onSeleccion(op.valor) }, Modifier.weight(1f))
                }
            }
        }
        if (error != null) {
            Text(error, color = DuocError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ChipOpcion(
    opcion: OpcionForm,
    marcada: Boolean,
    alinearInicio: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(forma)
            .background(if (marcada) DuocNavy else Color.White)
            .border(1.dp, if (marcada) DuocNavy else DuocBordeCampo, forma)
            .selectable(selected = marcada, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = if (alinearInicio) Alignment.CenterStart else Alignment.Center
    ) {
        Column(horizontalAlignment = if (alinearInicio) Alignment.Start else Alignment.CenterHorizontally) {
            Text(
                text = opcion.titulo,
                color = if (marcada) Color.White else DuocNavy,
                fontSize = 15.sp,
                fontWeight = if (marcada) FontWeight.Bold else FontWeight.Medium,
                textAlign = if (alinearInicio) TextAlign.Start else TextAlign.Center
            )
            if (opcion.detalle.isNotEmpty()) {
                Text(
                    text = opcion.detalle,
                    color = if (marcada) Color.White.copy(alpha = 0.85f) else DuocGris,
                    fontSize = 13.sp,
                    textAlign = if (alinearInicio) TextAlign.Start else TextAlign.Center
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Casilla para elegir VARIAS opciones (sedes)
// ---------------------------------------------------------------------------
@Composable
fun DuocCasilla(
    texto: String,
    marcada: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    detalle: String = ""
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(value = marcada, role = Role.Checkbox, onValueChange = onCambio)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = marcada,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = DuocNavy,
                uncheckedColor = DuocBordeCampo,
                checkmarkColor = Color.White
            )
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = buildAnnotatedString {
                append(texto)
                if (detalle.isNotEmpty()) {
                    withStyle(SpanStyle(color = DuocGris, fontStyle = FontStyle.Italic, fontSize = 13.sp)) {
                        append("  ($detalle)")
                    }
                }
            },
            color = DuocNavy,
            fontSize = 15.sp
        )
    }
}

// ---------------------------------------------------------------------------
// Bloque de autorización de datos, igual al del cupón (fondo oscuro)
// ---------------------------------------------------------------------------
private val ConsentFondo = Color(0xFF151515)
private val ConsentFranja = Color(0xFF2C2C2C)
// ---------------------------------------------------------------------------
// Autorización de datos: tarjeta clara, en el mismo estilo del resto del formulario
// ---------------------------------------------------------------------------
private const val URL_CONSENTIMIENTO = "https://www.duoc.cl/consentimiento-informado"

@Composable
fun DuocBloqueConsentimiento(
    marcado: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    val enlaces = LocalUriHandler.current
    val forma = RoundedCornerShape(16.dp)
    val formaChip = RoundedCornerShape(14.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(forma)
            .background(Color.White)
            .border(1.dp, if (error != null) DuocError else DuocBordeCampo, forma)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Los datos e información que nos compartas se mantendrán bajo reserva conforme " +
                    "a lo estipulado en la Ley N°19.628, resguardando la confidencialidad. " +
                    "Serán utilizados por Duoc UC solo para efectos de mantener contacto.",
            color = DuocGris,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )

        // Enlace al Consentimiento Informado
        Text(
            text = buildAnnotatedString {
                append("Más información en ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = DuocNavy,
                        textDecoration = TextDecoration.Underline
                    )
                ) { append("duoc.cl/consentimiento-informado") }
            },
            color = DuocGris,
            fontSize = 13.sp,
            modifier = Modifier.clickable {
                try {
                    enlaces.openUri(URL_CONSENTIMIENTO)
                } catch (e: Exception) {
                    // sin navegador disponible: no pasa nada
                }
            }
        )

        // Opción "Sí, autorizo": se ve igual que los chips del formulario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(formaChip)
                .background(if (marcado) DuocNavy else Color.White)
                .border(1.dp, if (marcado) DuocNavy else DuocBordeCampo, formaChip)
                .toggleable(value = marcado, role = Role.Checkbox, onValueChange = onCambio)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = marcado,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = DuocYellow,
                    uncheckedColor = DuocBordeCampo,
                    checkmarkColor = DuocNavy
                )
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = "Sí, autorizo",
                    color = if (marcado) Color.White else DuocNavy,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Antes de autorizar, lee el Consentimiento Informado",
                    color = if (marcado) Color.White.copy(alpha = 0.85f) else DuocGris,
                    fontSize = 12.sp
                )
            }
        }

        if (error != null) {
            Text(error, color = DuocError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------------------------------------------------------------------
// Carreras
// ---------------------------------------------------------------------------

// Nombre con el que se guarda y se muestra la carrera. Las de Duoc Online llevan la marca
// porque algunas se llaman igual que las presenciales (por ejemplo "Ingeniería en Prevención de Riesgos").
fun CarreraEntity.tituloSeleccion(): String =
    if (escuela == "Duoc Online") "$nombre (Duoc Online)" else nombre

// Opción de la lista del buscador de carreras
data class OpcionBusqueda(val titulo: String, val detalle: String = "")

// Campo con búsqueda: escribes y aparecen las coincidencias debajo para elegir una.
@Composable
fun DuocBuscador(
    etiqueta: String,
    seleccionado: String,
    opciones: List<OpcionBusqueda>,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: String? = null
) {
    var texto by remember { mutableStateOf(seleccionado) }
    var enFoco by remember { mutableStateOf(false) }
    var escribiendo by remember { mutableStateOf(false) }

    // Sincroniza el texto cuando la selección cambia desde afuera (por ejemplo, al limpiar el formulario)
    LaunchedEffect(seleccionado) {
        if (seleccionado.isNotEmpty()) texto = seleccionado
        else if (!escribiendo) texto = ""
    }

    val filtradas = remember(texto, opciones) {
        val q = texto.trim()
        opciones.filter { q.isEmpty() || it.titulo.contains(q, ignoreCase = true) }.take(6)
    }
    val mostrarLista = enFoco && texto != seleccionado && filtradas.isNotEmpty()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(etiqueta, color = DuocNavy, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

        OutlinedTextField(
            value = texto,
            onValueChange = {
                texto = it
                escribiendo = true
                // Si escribe algo distinto a lo elegido, se descarta la selección
                if (seleccionado.isNotEmpty() && it != seleccionado) onSeleccion("")
            },
            placeholder = { Text(placeholder) },
            singleLine = true,
            isError = error != null,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { enFoco = it.isFocused }
        )

        if (mostrarLista) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, DuocBordeCampo, RoundedCornerShape(14.dp))
            ) {
                filtradas.forEach { op ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                escribiendo = false
                                texto = op.titulo
                                onSeleccion(op.titulo)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(op.titulo, color = DuocNavy, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        if (op.detalle.isNotEmpty()) {
                            Text(op.detalle, color = DuocGris, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        if (error != null) {
            Text(error, color = DuocError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}