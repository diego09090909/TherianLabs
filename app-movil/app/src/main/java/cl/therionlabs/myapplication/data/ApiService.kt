package cl.therionlabs.myapplication.data

import cl.therionlabs.myapplication.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
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

data class ProspectoRemotoDto(
    val id: Long,
    val rut: String?
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

    @GET("api/prospectos")
    suspend fun listarPorEvento(@Query("eventoId") eventoId: String): Response<List<ProspectoRemotoDto>>

    @PUT("api/prospectos/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body prospecto: ProspectoDto): Response<ProspectoRemotoDto>

    @DELETE("api/prospectos/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}

interface EventosApi {
    @GET("api/eventos/{id}")
    suspend fun obtenerEvento(@Path("id") id: String): Response<EventoDto>
}

// El servidor se define en app/build.gradle.kts (API_HOST):
// emulador = 10.0.2.2, AWS = IP pública o dominio del servidor
const val PROSPECTOS_URL = "http://${BuildConfig.API_HOST}:8082/"
const val EVENTOS_URL = "http://${BuildConfig.API_HOST}:8081/"
const val CAPTADORES_URL = "http://${BuildConfig.API_HOST}:8083/"
const val INDICADORES_URL = "https://mindicador.cl/"

// API externa pública (mindicador.cl): indicadores económicos de Chile, sin API key.
data class IndicadorDto(val valor: Double?, val fecha: String?)
data class IndicadoresDto(val uf: IndicadorDto?, val dolar: IndicadorDto?)

interface IndicadoresApi {
    @GET("api")
    suspend fun obtenerIndicadores(): Response<IndicadoresDto>
}

object RetrofitClient {

    private val cliente: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Cliente para nuestros microservicios: agrega el token JWT del captador si hay sesión
    private val clienteConToken: OkHttpClient by lazy {
        cliente.newBuilder()
            .addInterceptor { chain ->
                val token = SesionManager.token()
                val peticion = if (token != null) {
                    chain.request().newBuilder().header("Authorization", "Bearer $token").build()
                } else {
                    chain.request()
                }
                chain.proceed(peticion)
            }
            .build()
    }

    private fun construir(url: String, http: OkHttpClient = clienteConToken): Retrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val api: ApiService by lazy { construir(PROSPECTOS_URL).create(ApiService::class.java) }

    val eventos: EventosApi by lazy { construir(EVENTOS_URL).create(EventosApi::class.java) }

    val captadores: CaptadoresApi by lazy { construir(CAPTADORES_URL).create(CaptadoresApi::class.java) }

    // API externa: sin token
    val indicadores: IndicadoresApi by lazy { construir(INDICADORES_URL, cliente).create(IndicadoresApi::class.java) }
}
