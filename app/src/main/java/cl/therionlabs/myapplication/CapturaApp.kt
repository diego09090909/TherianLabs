package cl.therionlabs.myapplication

import android.app.Application
import cl.therionlabs.myapplication.data.AppDatabase
import cl.therionlabs.myapplication.data.ProspectoRepository
import cl.therionlabs.myapplication.data.RetrofitClient
import cl.therionlabs.myapplication.sync.SyncScheduler

class CapturaApp : Application() {

    val repository: ProspectoRepository by lazy {
        val db = AppDatabase.obtenerInstancia(this)

        ProspectoRepository(
            this,
            db.prospectoDao(),
            RetrofitClient.api
        )
    }

    override fun onCreate() {
        super.onCreate()

        SyncScheduler.programarPeriodico(this)
        SyncScheduler.programarAhora(this)
    }
}
