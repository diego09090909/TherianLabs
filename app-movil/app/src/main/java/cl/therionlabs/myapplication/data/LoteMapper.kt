package cl.therionlabs.myapplication.data

data class ResultadoLote(
    val sincronizados: List<Int>,
    val rechazados: Map<Int, String>
)

object LoteMapper {

    private const val PREFIJO_OTRA = "Otra: "

    fun aDto(prospecto: Prospecto, correo: String, telefono: String): ProspectoDto {
        val (nombres, apellidos) = nombresYApellidos(prospecto)
        val (actividad, actividadOtra) = separarActividad(prospecto.nivelEducativo)
        return ProspectoDto(
            eventoId = prospecto.eventoId,
            captadorId = prospecto.captadorId,
            rut = prospecto.rut,
            nombres = nombres,
            apellidos = apellidos,
            sexo = prospecto.sexo,
            fechaNacimiento = prospecto.fechaNacimiento,
            comunaResidencia = prospecto.comunaResidencia,
            celular = telefono,
            email = correo,
            actividad = actividad,
            actividadOtra = actividadOtra,
            establecimiento = prospecto.colegio,
            comunaEstablecimiento = prospecto.comunaColegio,
            jornada = prospecto.jornada,
            sedes = prospecto.sedes,
            nivelInteres = prospecto.nivelInteres,
            afirmacion = prospecto.afirmacion,
            carrera1 = prospecto.carrera,
            carrera2 = prospecto.carrera2,
            carrera3 = prospecto.carrera3,
            autorizaContacto = prospecto.autorizaDatos,
            fechaCaptura = prospecto.fechaCaptura
        )
    }

    fun mismoRut(a: String?, b: String?): Boolean {
        fun limpio(r: String?) = r.orEmpty().filter { it.isLetterOrDigit() }.uppercase()
        return limpio(a).isNotEmpty() && limpio(a) == limpio(b)
    }

    fun nombresYApellidos(prospecto: Prospecto): Pair<String, String> {
        if (prospecto.nombres.isNotBlank() && prospecto.apellidos.isNotBlank()) {
            return prospecto.nombres to prospecto.apellidos
        }
        val partes = prospecto.nombreCompleto.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (partes.size <= 1) {
            val unico = partes.firstOrNull().orEmpty()
            return unico to unico
        }
        val corte = partes.size / 2
        return partes.take(corte).joinToString(" ") to partes.drop(corte).joinToString(" ")
    }

    fun separarActividad(nivelEducativo: String): Pair<String, String> {
        return if (nivelEducativo.startsWith(PREFIJO_OTRA)) {
            "Otra" to nivelEducativo.removePrefix(PREFIJO_OTRA)
        } else {
            nivelEducativo to ""
        }
    }

    fun interpretar(lote: List<Prospecto>, respuesta: RespuestaLoteDto): ResultadoLote? {
        if (respuesta.resultados.size != lote.size) return null
        val sincronizados = mutableListOf<Int>()
        val rechazados = mutableMapOf<Int, String>()
        lote.zip(respuesta.resultados).forEach { (prospecto, resultado) ->
            when (resultado.estado) {
                "CREADO", "DUPLICADO" -> sincronizados.add(prospecto.id)
                "INVALIDO" -> rechazados[prospecto.id] = resultado.motivo ?: "El servidor rechazó el registro"
            }
        }
        return ResultadoLote(sincronizados, rechazados)
    }
}
