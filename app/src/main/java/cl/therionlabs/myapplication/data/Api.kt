package cl.therionlabs.myapplication.data

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Lo que se envia al microservicio Spring Boot (datos ya descifrados, viajan directo a casa central).
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

interface ApiService {
    // Carga masiva (RF-06). El equipo de backend debe exponer este endpoint.
    @POST("api/prospectos/lote")
    suspend fun enviarLote(@Body lote: List<ProspectoDto>): Response<Unit>
}

// Cambiar por la URL real del microservicio Spring Boot (10.0.2.2 = localhost del PC desde el emulador)
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
