package cl.therionlabs.myapplication.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// ----- Cuerpos que espera ms-captadores -----
data class RegistroCaptadorDto(
    val nombre: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val contrasena: String
)

data class LoginCaptadorDto(
    val correo: String,
    val contrasena: String
)

data class ActualizarCaptadorDto(
    val nombre: String,
    val telefono: String
)

data class CambiarContrasenaDto(
    val contrasenaActual: String,
    val contrasenaNueva: String
)

// ----- Respuestas de ms-captadores -----
data class CaptadorDto(
    val id: Long,
    val nombre: String,
    val rut: String,
    val correo: String,
    val telefono: String
)

data class AuthResponseDto(
    val token: String,
    val tipo: String,
    val captador: CaptadorDto
)

// Formato de error que devuelven los microservicios (ManejadorErrores)
data class ErrorRespuestaDto(
    val status: Int?,
    val error: String?,
    val mensaje: String?,
    val detalles: List<String>?
)

interface CaptadoresApi {
    @POST("api/captadores/solicitudes")
    suspend fun registrar(@Body body: RegistroCaptadorDto): Response<AuthResponseDto>

    @POST("api/captadores/login")
    suspend fun login(@Body body: LoginCaptadorDto): Response<AuthResponseDto>

    @PUT("api/captadores/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body body: ActualizarCaptadorDto): Response<CaptadorDto>

    @PUT("api/captadores/{id}/contrasena")
    suspend fun cambiarContrasena(@Path("id") id: Long, @Body body: CambiarContrasenaDto): Response<Unit>
}
