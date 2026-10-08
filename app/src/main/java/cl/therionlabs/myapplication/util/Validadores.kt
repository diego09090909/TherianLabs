package cl.therionlabs.myapplication.util

import android.util.Patterns

object Validadores {

    // Quita puntos y guion: "12.345.678-5" -> "123456785"
    private fun limpiarRut(rut: String): String =
        rut.replace(".", "").replace("-", "").trim().uppercase()

    // Formato normalizado para guardar: "12345678-5"
    fun normalizarRut(rut: String): String {
        val l = limpiarRut(rut)
        return if (l.length < 2) l else l.dropLast(1) + "-" + l.last()
    }

    // Formatea mientras se escribe: "123456785" -> "12345678-5"
    fun formatearRutEnVivo(texto: String): String {
        val t = texto.uppercase().filter { it.isDigit() || it == 'K' || it == '-'}

        if(t.contains('-')){
            val cuerpo = t.substringBefore('-').filter { it.isDigit() }.take(8)
            val dv = t.substringAfter('-').filter { it.isDigit() || it == 'K' }.take(1)
            return if (cuerpo.isEmpty())"" else "$cuerpo-$dv"
        }

        val limpio = t.take(9)
        return if (limpio.length == 9) limpio.dropLast(1) + "-" + limpio.last() else limpio
    }

    // Valida formato y digito verificador (modulo 11)
    fun rutValido(rut: String): Boolean {
        val l = limpiarRut(rut)
        if (l.length < 7 || l.length > 9) return false
        val cuerpo = l.dropLast(1)
        val dv = l.last()
        if (!cuerpo.all { it.isDigit() }) return false

        var suma = 0
        var multiplicador = 2
        for (c in cuerpo.reversed()) {
            suma += c.digitToInt() * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }
        val resto = 11 - (suma % 11)
        val esperado = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> resto.digitToChar()
        }
        return dv == esperado
    }

    fun correoValido(correo: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()

    // Celular chileno: 9XXXXXXXX, con o sin +56
    fun telefonoValido(tel: String): Boolean =
        Regex("^(\\+?56)?9\\d{8}$").matches(tel.replace(" ", ""))
}
