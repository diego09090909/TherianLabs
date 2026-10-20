package cl.therionlabs.myapplication.data

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
    val fechaCaptura: Long
)

data class EventoDto(
    val id: String,
    val nombre: String,
    val liceo: String,
    val fecha: String,   // llega como AAAA-MM-DD
    val estado: String   // ACTIVO o CERRADO
)

interface ApiService {
    @POST("api/prospectos/lote")
    suspend fun enviarLote(@Body lote: List<ProspectoDto>): Response<Unit>

    // Busca un evento por su código (el mismo que trae el QR).
    @GET("api/eventos/{id}")
    suspend fun obtenerEvento(@Path("id") id: String): Response<EventoDto>
}

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