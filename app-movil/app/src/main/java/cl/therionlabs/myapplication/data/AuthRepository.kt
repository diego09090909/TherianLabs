package cl.therionlabs.myapplication.data

import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

// Error que viene del servidor: código HTTP + mensaje del microservicio
class ApiException(val codigo: Int, mensaje: String, val detalles: List<String> = emptyList()) :
    Exception(mensaje)

// Sin conexión o el servidor no responde
class SinConexionException : Exception("Sin conexión con el servidor")

class AuthRepository(
    private val api: CaptadoresApi = RetrofitClient.captadores
) {

    val usuarioActual: SesionCaptador?
        get() = SesionManager.actual()

    suspend fun registrarCaptador(
        nombre: String,
        rut: String,
        correo: String,
        telefono: String,
        clave: String
    ): Result<CaptadorDto> = llamar {
        api.registrar(RegistroCaptadorDto(nombre, rut, correo, telefono, clave))
    }.map { it.captador }

    suspend fun iniciarSesion(correo: String, clave: String): Result<SesionCaptador> =
        llamar { api.login(LoginCaptadorDto(correo, clave)) }.map { auth ->
            val c = auth.captador
            val sesion = SesionCaptador(c.id, c.nombre, c.rut, c.correo, c.telefono, auth.token)
            SesionManager.guardar(sesion)
            sesion
        }

    suspend fun actualizarNombre(nombre: String): Result<Unit> {
        val sesion = usuarioActual ?: return Result.failure(IllegalStateException("Sin sesión"))
        return llamar { api.actualizar(sesion.id, ActualizarCaptadorDto(nombre, sesion.telefono)) }
            .map { c -> SesionManager.guardar(sesion.copy(nombre = c.nombre, telefono = c.telefono)) }
    }

    suspend fun cambiarClave(claveActual: String, claveNueva: String): Result<Unit> {
        val sesion = usuarioActual ?: return Result.failure(IllegalStateException("Sin sesión"))
        return llamarSinCuerpo { api.cambiarContrasena(sesion.id, CambiarContrasenaDto(claveActual, claveNueva)) }
    }

    fun cerrarSesion() {
        SesionManager.cerrar()
    }

    // ----- Manejo común de respuestas -----
    private suspend fun <T> llamar(peticion: suspend () -> Response<T>): Result<T> =
        try {
            val r = peticion()
            val cuerpo = r.body()
            if (r.isSuccessful && cuerpo != null) Result.success(cuerpo) else Result.failure(aError(r))
        } catch (e: IOException) {
            Result.failure(SinConexionException())
        } catch (e: Exception) {
            Result.failure(e)
        }

    private suspend fun llamarSinCuerpo(peticion: suspend () -> Response<Unit>): Result<Unit> =
        try {
            val r = peticion()
            if (r.isSuccessful) Result.success(Unit) else Result.failure(aError(r))
        } catch (e: IOException) {
            Result.failure(SinConexionException())
        } catch (e: Exception) {
            Result.failure(e)
        }

    private fun aError(r: Response<*>): ApiException {
        val error = try {
            Gson().fromJson(r.errorBody()?.string(), ErrorRespuestaDto::class.java)
        } catch (e: Exception) {
            null
        }
        return ApiException(r.code(), error?.mensaje ?: "Error ${r.code()}", error?.detalles.orEmpty())
    }
}
