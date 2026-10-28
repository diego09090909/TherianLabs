package cl.therionlabs.myapplication.model

// Estado del formulario del alumno. Los campos y su orden siguen el cupón de admisión 2027.
data class RegistroUiState(
    val eventoId: String = "EVENTO-001",

    // DATOS PERSONALES
    val rut: String = "",
    val sexo: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val fechaNacimiento: String = "",
    val comunaResidencia: String = "",
    val telefono: String = "",
    val correo: String = "",

    // ACTIVIDAD ACTUAL
    val actividad: String = "",          // "4° medio", "Egresado" u "Otra"
    val actividadOtra: String = "",      // se pide solo si eligió "Otra"
    val colegio: String = "",            // Establecimiento
    val comunaColegio: String = "",
    val jornada: String = "",

    // SEDE DE INTERÉS (puede marcar varias)
    val sedes: Set<String> = emptySet(),

    // NIVEL DE INTERÉS y afirmación con la que se identifica
    val nivelInteres: String = "",
    val afirmacion: String = "",

    // 3 CARRERAS de interés, en orden de prioridad (las tres son obligatorias)
    val carrera1: String = "",
    val carrera2: String = "",
    val carrera3: String = "",

    // "Sí, autorizo" (Consentimiento Informado)
    val aceptaTerminos: Boolean = false,

    val errores: RegistroErrores = RegistroErrores()
)

data class RegistroErrores(
    val rut: String? = null,
    val nombre: String? = null,
    val apellidos: String? = null,
    val fechaNacimiento: String? = null,
    val comunaResidencia: String? = null,
    val telefono: String? = null,
    val correo: String? = null,
    val actividad: String? = null,
    val actividadOtra: String? = null,
    val colegio: String? = null,
    val jornada: String? = null,
    val sedes: String? = null,
    val carrera1: String? = null,
    val carrera2: String? = null,
    val carrera3: String? = null,
    val aceptaTerminos: String? = null
) {
    fun cantidad(): Int = listOfNotNull(
        rut, nombre, apellidos, fechaNacimiento, comunaResidencia, telefono, correo,
        actividad, actividadOtra, colegio, jornada, sedes, carrera1, carrera2, carrera3, aceptaTerminos
    ).size
}

// Opción con un valor que se guarda y un texto que se muestra
data class OpcionForm(val valor: String, val titulo: String, val detalle: String = "")

data class Sede(val nombre: String, val comuna: String = "")

data class RegionSedes(val region: String, val sedes: List<Sede>)

// Listas fijas del cupón. Las carreras NO están aquí: salen de la base de datos (tabla "carreras").
object Catalogos {
    val sexos = listOf(
        OpcionForm("Femenino", "Femenino"),
        OpcionForm("Masculino", "Masculino"),
        OpcionForm("No indica", "No indica")
    )

    const val ACTIVIDAD_OTRA = "Otra"
    val actividades = listOf(
        OpcionForm("4° medio", "4° Medio"),
        OpcionForm("Egresado", "Egresado Enseñanza Media"),
        OpcionForm(ACTIVIDAD_OTRA, "Otra")
    )

    val jornadas = listOf(
        OpcionForm("Diurna", "Diurna", "L a V: 08.30 a 19.00 hrs"),
        OpcionForm("Vespertina", "Vespertina", "L a V: 19.00 a 22.45 · S: 08.30 a 16.00 hrs")
    )

    val sedesPorRegion = listOf(
        RegionSedes(
            "Región Metropolitana", listOf(
                Sede("Alameda", "Santiago Centro"),
                Sede("Antonio Varas", "Providencia"),
                Sede("Maipú"),
                Sede("Melipilla"),
                Sede("Padre Alonso de Ovalle", "Santiago Centro"),
                Sede("Plaza Norte", "Huechuraba"),
                Sede("Plaza Oeste", "Cerrillos"),
                Sede("Plaza Vespucio", "La Florida"),
                Sede("Puente Alto"),
                Sede("San Bernardo"),
                Sede("San Carlos de Apoquindo", "Las Condes"),
                Sede("San Joaquín")
            )
        ),
        RegionSedes("Región de Valparaíso", listOf(Sede("Valparaíso"), Sede("Viña del Mar"))),
        RegionSedes(
            "Región del Biobío",
            listOf(Sede("Concepción"), Sede("Campus Arauco"), Sede("Campus Nacimiento"))
        ),
        RegionSedes("Región de la Araucanía", listOf(Sede("Campus Villarrica"))),
        RegionSedes("Región de los Lagos", listOf(Sede("Puerto Montt")))
    )

    val nivelesInteres = listOf(
        OpcionForm("Alto", "Interés Alto"),
        OpcionForm("Medio", "Interés Medio"),
        OpcionForm("Bajo", "Interés Bajo")
    )

    // "¿Con cuál te sientes más identificado?" (se guarda el número)
    val afirmaciones = listOf(
        OpcionForm(
            "1",
            "Estudiar es una inversión para aumentar mis ingresos y progresar en la vida, tanto yo como mi familia."
        ),
        OpcionForm(
            "2",
            "Estudiar es seguir mi vocación para encontrar mi lugar en el mundo laboral. Los ingresos que recibiré no son lo central."
        ),
        OpcionForm(
            "3",
            "Estudiar es una nueva oportunidad de retomar esa carrera que dejé pendiente, postergué, o de darme la posibilidad de comenzar otra nueva carrera."
        ),
        OpcionForm(
            "4",
            "Estudiar es muy importante, sin embargo, no sé si tendré los ingresos suficientes para financiar mi carrera."
        )
    )
}