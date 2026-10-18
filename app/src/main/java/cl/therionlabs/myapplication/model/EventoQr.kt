package cl.therionlabs.myapplication.model

data class EventoQr(
    val id: String,
    val liceo: String,
    val nombre: String,
    val fecha: String
)

object EventosDemo {
    val feriaVocacional = EventoQr(
        id = "EVT-0042",
        liceo = "Liceo Bicentenario Ejemplo",
        nombre = "Feria Vocacional 2026",
        fecha = "14 oct 2026"
    )

    fun desdeCodigo(codigo: String): EventoQr {
        val id = codigo.trim().uppercase()
        return if (id == feriaVocacional.id) {
            feriaVocacional
        } else {
            EventoQr(id = id, liceo = "Por confirmar", nombre = "Evento $id", fecha = "-")
        }
    }
}
