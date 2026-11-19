package cl.therionlabs.myapplication.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cl.therionlabs.myapplication.CapturaApp

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repo = (applicationContext as CapturaApp).repository
        return if (repo.sincronizarPendientes()) Result.success() else Result.retry()
    }
}
