package cl.therionlabs.myapplication.model

import cl.therionlabs.myapplication.data.EventoDto
import java.text.SimpleDateFormat
import java.util.Locale

data class EventoQr(
    val id: String,
    val liceo: String,
    val nombre: String,
    val fecha: String
)

object EventosDemo {
    val feriaVocacional = EventoQr(
        id = "EVT-0042",
        liceo = "Liceo Bicentenario",
        nombre = "Feria Vocacional 2026",
        fecha = "14 oct 2026"
    )

    private val todos = listOf(
        feriaVocacional,
        EventoQr("EVT-0043", "Colegio San Mateo", "Feria de Carreras", "21 oct 2026"),
        EventoQr("EVT-0044", "Liceo Industrial de Maipú", "Charla Vocacional", "28 oct 2026"),
        EventoQr("EVT-0045", "Liceo Bicentenario Providencia", "Expo Duoc", "04 nov 2026")
    )

    fun desdeCodigo(codigo: String): EventoQr {
        val id = codigo.trim().uppercase()
        return todos.firstOrNull { it.id == id }
            ?: EventoQr(id = id, liceo = "Por confirmar", nombre = "Evento $id", fecha = "-")
    }
}

fun EventoDto.aEventoQr(): EventoQr {
    val fechaBonita = try {
        val origen = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha)
        SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es-CL")).format(origen!!)
    } catch (e: Exception) {
        fecha
    }
    return EventoQr(id = id, liceo = liceo, nombre = nombre, fecha = fechaBonita)
}