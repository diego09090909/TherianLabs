package cl.therionlabs.myapplication.data

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class ProspectoDto(
    val eventoId: String,
    val captadorId: String,
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val sexo: String,
    val fechaNacimiento: String,
    val comunaResidencia: String,
    val celular: String,
    val email: String,
    val actividad: String,
    val actividadOtra: String,
    val establecimiento: String,
    val comunaEstablecimiento: String,
    val jornada: String,
    val sedes: String,
    val nivelInteres: String,
    val afirmacion: String,
    val carrera1: String,
    val carrera2: String,
    val carrera3: String,
    val autorizaContacto: Boolean,
    val fechaCaptura: Long
)

data class ResultadoProspectoDto(
    val eventoId: String?,
    val rut: String?,
    val estado: String,
    val motivo: String?,
    val id: Long?
)

data class RespuestaLoteDto(
    val recibidos: Int,
    val creados: Int,
    val duplicados: Int,
    val invalidos: Int,
    val resultados: List<ResultadoProspectoDto>
)

data class EventoDto(
    val id: String,
    val nombre: String,
    val liceo: String,
    val fecha: String,
    val estado: String
)

interface ApiService {
    @POST("api/prospectos/lote")
    suspend fun enviarLote(@Body lote: List<ProspectoDto>): Response<RespuestaLoteDto>
}

interface EventosApi {
    @GET("api/eventos/{id}")
    suspend fun obtenerEvento(@Path("id") id: String): Response<EventoDto>
}

const val PROSPECTOS_URL = "http://10.0.2.2:8082/"
const val EVENTOS_URL = "http://10.0.2.2:8081/"

object RetrofitClient {

    private val cliente: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun construir(url: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val api: ApiService by lazy { construir(PROSPECTOS_URL).create(ApiService::class.java) }

    val eventos: EventosApi by lazy { construir(EVENTOS_URL).create(EventosApi::class.java) }
}
