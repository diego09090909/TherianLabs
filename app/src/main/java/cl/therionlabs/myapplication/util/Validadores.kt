package cl.therionlabs.myapplication.util

import android.util.Patterns
import java.util.Calendar

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

    // Fecha de nacimiento del cupón: se escriben solo números y se agregan los guiones.
    // "01022008" -> "01-02-2008"
    fun formatearFecha(texto: String): String {
        val digitos = texto.filter { it.isDigit() }.take(8)
        return buildString {
            digitos.forEachIndexed { i, c ->
                if (i == 2 || i == 4) append('-')
                append(c)
            }
        }
    }

    // dd-MM-aaaa: día y mes reales (con años bisiestos), no futura y no anterior a hace 60 años
    fun fechaNacimientoValida(fecha: String): Boolean {
        val m = Regex("^(\\d{2})-(\\d{2})-(\\d{4})$").matchEntire(fecha) ?: return false
        val dia = m.groupValues[1].toInt()
        val mes = m.groupValues[2].toInt()
        val anio = m.groupValues[3].toInt()
        if (mes !in 1..12 || dia < 1) return false

        val bisiesto = (anio % 4 == 0 && anio % 100 != 0) || anio % 400 == 0
        val diasDelMes = when (mes) {
            2 -> if (bisiesto) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
        if (dia > diasDelMes) return false

        val hoy = Calendar.getInstance()
        val anioActual = hoy.get(Calendar.YEAR)
        if (anio > anioActual || anio < anioActual - 60) return false
        if (anio == anioActual) {
            val mesActual = hoy.get(Calendar.MONTH) + 1
            val diaActual = hoy.get(Calendar.DAY_OF_MONTH)
            if (mes > mesActual || (mes == mesActual && dia > diaActual)) return false
        }
        return true
    }
}
