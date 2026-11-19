package cl.therionlabs.myapplication.data

class CaptadorRepository(
    private val dao: CaptadorDao
) {

    suspend fun guardarLocalmente(
        nombreCompleto: String,
        rut: String,
        correo: String,
        telefono: String
    ) {
        val captador = CaptadorEntity(
            nombreCompleto = nombreCompleto.trim(),
            rut = rut.trim(),
            correo = correo.trim(),
            telefono = telefono.trim()
        )

        dao.insertar(captador)
    }

    suspend fun obtenerPendientes(): List<CaptadorEntity> {
        return dao.obtenerPendientes()
    }

    suspend fun obtenerTodos(): List<CaptadorEntity> {
        return dao.obtenerTodos()
    }

    suspend fun marcarSincronizado(captadorId: String) {
        dao.marcarSincronizado(captadorId)
    }
}
