package cl.therionlabs.myapplication

import android.app.Application
import cl.therionlabs.myapplication.data.AppDatabase
import cl.therionlabs.myapplication.data.ProspectoRepository
import cl.therionlabs.myapplication.data.RetrofitClient
import cl.therionlabs.myapplication.sync.SyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CapturaApp : Application() {

    // Scope global de corrutinas respaldado por la aplicación
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val repository: ProspectoRepository by lazy {
        // 👈 SE LE PASAN AMBOS PARÁMETROS A AppDatabase: context y scope
        val db = AppDatabase.obtenerInstancia(this, applicationScope)

        ProspectoRepository(
            context = this,
            dao = db.prospectoDao(),
            api = RetrofitClient.api,
            scope = applicationScope
        )
    }

    override fun onCreate() {
        super.onCreate()

        SyncScheduler.programarPeriodico(this)
        SyncScheduler.programarAhora(this)
    }
}