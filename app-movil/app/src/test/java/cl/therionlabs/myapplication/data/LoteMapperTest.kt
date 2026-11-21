package cl.therionlabs.myapplication.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoteMapperTest {

    private fun prospecto(id: Int = 1, nombreCompleto: String = "Camila Andrea Rojas Soto") = Prospecto(
        id = id,
        eventoId = "EVT-0042",
        nombreCompleto = nombreCompleto,
        rut = "12345678-5",
        correoCifrado = "x",
        telefonoCifrado = "y",
        colegio = "Liceo A-90",
        nivelEducativo = "4° medio",
        carrera = "Analista Programador",
        modalidad = "",
        observaciones = "",
        fechaCaptura = 1763600000000L,
        carrera2 = "Ingeniería en Informática",
        carrera3 = "",
        sexo = "Femenino",
        fechaNacimiento = "30-05-2008",
        comunaResidencia = "Maipú",
        comunaColegio = "Maipú",
        jornada = "Diurna",
        sedes = "Maipú|Plaza Oeste",
        nivelInteres = "Alto",
        afirmacion = "2",
        autorizaDatos = true,
        nombres = "Camila Andrea",
        apellidos = "Rojas Soto",
        captadorId = "uid-1"
    )

    private fun resultado(estado: String, motivo: String? = null) =
        ResultadoProspectoDto("EVT-0042", "12345678-5", estado, motivo, null)

    private fun respuesta(vararg resultados: ResultadoProspectoDto) =
        RespuestaLoteDto(resultados.size, 0, 0, 0, resultados.toList())

    @Test
    fun aDto_usaLosNombresDelContratoDelBackend() {
        val dto = LoteMapper.aDto(prospecto(), correo = "camila@correo.cl", telefono = "912345678")

        assertEquals("EVT-0042", dto.eventoId)
        assertEquals("uid-1", dto.captadorId)
        assertEquals("Camila Andrea", dto.nombres)
        assertEquals("Rojas Soto", dto.apellidos)
        assertEquals("camila@correo.cl", dto.email)
        assertEquals("912345678", dto.celular)
        assertEquals("Liceo A-90", dto.establecimiento)
        assertEquals("Maipú", dto.comunaEstablecimiento)
        assertEquals("Analista Programador", dto.carrera1)
        assertEquals("Maipú|Plaza Oeste", dto.sedes)
        assertTrue(dto.autorizaContacto)
    }

    @Test
    fun nombresYApellidos_sinColumnasSeSeparaElNombreCompleto() {
        val antiguo = prospecto().copy(nombres = "", apellidos = "")

        assertEquals("Camila Andrea" to "Rojas Soto", LoteMapper.nombresYApellidos(antiguo))
        assertEquals("Camila" to "Rojas", LoteMapper.nombresYApellidos(antiguo.copy(nombreCompleto = "Camila Rojas")))
        assertEquals("Camila" to "Rojas Soto", LoteMapper.nombresYApellidos(antiguo.copy(nombreCompleto = "Camila Rojas Soto")))
        assertEquals("Camila" to "Camila", LoteMapper.nombresYApellidos(antiguo.copy(nombreCompleto = "Camila")))
        assertEquals("" to "", LoteMapper.nombresYApellidos(antiguo.copy(nombreCompleto = "  ")))
    }

    @Test
    fun separarActividad_detectaElCampoOtra() {
        assertEquals("4° medio" to "", LoteMapper.separarActividad("4° medio"))
        assertEquals("Otra" to "Trabajando", LoteMapper.separarActividad("Otra: Trabajando"))
    }

    @Test
    fun interpretar_creadoYDuplicadoQuedanSincronizados() {
        val lote = listOf(prospecto(id = 1), prospecto(id = 2))

        val resultado = LoteMapper.interpretar(lote, respuesta(resultado("CREADO"), resultado("DUPLICADO")))

        assertNotNull(resultado)
        assertEquals(listOf(1, 2), resultado!!.sincronizados)
        assertTrue(resultado.rechazados.isEmpty())
    }

    @Test
    fun interpretar_invalidoQuedaRechazadoConSuMotivo() {
        val lote = listOf(prospecto(id = 1), prospecto(id = 2))

        val resultado = LoteMapper.interpretar(
            lote,
            respuesta(resultado("CREADO"), resultado("INVALIDO", "El evento EVT-0042 esta cerrado"))
        )

        assertEquals(listOf(1), resultado!!.sincronizados)
        assertEquals(mapOf(2 to "El evento EVT-0042 esta cerrado"), resultado.rechazados)
    }

    @Test
    fun interpretar_invalidoSinMotivoUsaUnMensajePorDefecto() {
        val resultado = LoteMapper.interpretar(listOf(prospecto(id = 1)), respuesta(resultado("INVALIDO")))

        assertTrue(resultado!!.rechazados.getValue(1).isNotBlank())
    }

    @Test
    fun interpretar_estadoDesconocidoSeDejaPendiente() {
        val resultado = LoteMapper.interpretar(listOf(prospecto(id = 1)), respuesta(resultado("OTRO")))

        assertTrue(resultado!!.sincronizados.isEmpty())
        assertTrue(resultado.rechazados.isEmpty())
    }

    @Test
    fun interpretar_siElTamanoNoCoincideDevuelveNulo() {
        val lote = listOf(prospecto(id = 1), prospecto(id = 2))

        assertNull(LoteMapper.interpretar(lote, respuesta(resultado("CREADO"))))
    }
}
