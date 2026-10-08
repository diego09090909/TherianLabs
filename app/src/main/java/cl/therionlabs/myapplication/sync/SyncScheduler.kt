package cl.therionlabs.myapplication.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncScheduler {

    private val soloConRed = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    // Se llama al guardar un prospecto: WorkManager lo ejecuta en cuanto haya internet.
    fun programarAhora(context: Context) {
        val trabajo = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(soloConRed)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork("sync_ahora", ExistingWorkPolicy.REPLACE, trabajo)
    }

    // Red de seguridad: revisa pendientes cada 15 min (minimo permitido por Android).
    fun programarPeriodico(context: Context) {
        val trabajo = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(soloConRed)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork("sync_periodico", ExistingPeriodicWorkPolicy.KEEP, trabajo)
    }
}
