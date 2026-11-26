package cl.therionlabs.myapplication.data

import android.content.Context
import android.content.SharedPreferences

// Datos del captador que inició sesión (vienen de ms-captadores)
data class SesionCaptador(
    val id: Long,
    val nombre: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val token: String
)

// Guarda la sesión en el teléfono para no pedir login cada vez que se abre la app
object SesionManager {

    private const val ARCHIVO = "sesion_captador"
    private var prefs: SharedPreferences? = null

    fun iniciar(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        }
    }

    fun guardar(sesion: SesionCaptador) {
        prefs?.edit()
            ?.putLong("id", sesion.id)
            ?.putString("nombre", sesion.nombre)
            ?.putString("rut", sesion.rut)
            ?.putString("correo", sesion.correo)
            ?.putString("telefono", sesion.telefono)
            ?.putString("token", sesion.token)
            ?.apply()
    }

    fun actual(): SesionCaptador? {
        val p = prefs ?: return null
        val token = p.getString("token", null) ?: return null
        return SesionCaptador(
            id = p.getLong("id", 0L),
            nombre = p.getString("nombre", "").orEmpty(),
            rut = p.getString("rut", "").orEmpty(),
            correo = p.getString("correo", "").orEmpty(),
            telefono = p.getString("telefono", "").orEmpty(),
            token = token
        )
    }

    fun token(): String? = prefs?.getString("token", null)

    fun cerrar() {
        prefs?.edit()?.clear()?.apply()
    }
}
