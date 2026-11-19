package cl.therionlabs.myapplication.data

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// 1. DTO de Transferencia de Datos
data class ProspectoDto(
    val eventoId: String,
    val nombreCompleto: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val colegio: String,
    val nivelEducativo: String,
    val carrera: String,
    val modalidad: String,
    val observaciones: String,
    val fechaCaptura: Long,
    // Campos del cupón de admisión (el backend debe agregarlos para guardarlos)
    val carrera2: String,
    val carrera3: String,
    val sexo: String,
    val fechaNacimiento: String,   // dd-MM-aaaa
    val comunaResidencia: String,
    val comunaColegio: String,
    val jornada: String,
    val sedes: List<String>,
    val nivelInteres: String,
    val afirmacion: String,
    val autorizaDatos: Boolean
)

data class EventoDto(
    val id: String,
    val nombre: String,
    val liceo: String,
    val fecha: String,
    val estado: String
)

// 2. Interfaz con los endpoints de Retrofit
interface ApiService {
    @POST("api/prospectos/lote")
    suspend fun enviarLote(@Body lote: List<ProspectoDto>): Response<Unit>

    @GET("api/eventos/{id}")
    suspend fun obtenerEvento(@Path("id") id: String): Response<EventoDto>
}

// 3. Cliente Retrofit Singleton
const val BASE_URL = "http://10.0.2.2:8080/"

object RetrofitClient {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}