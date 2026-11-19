package cl.therionlabs.myapplication.model

sealed class ResultadoEvento {
    data class Encontrado(val evento: EventoQr) : ResultadoEvento()

    data class SinConexion(val evento: EventoQr) : ResultadoEvento()

    object Cerrado : ResultadoEvento()

    object NoExiste : ResultadoEvento()
}