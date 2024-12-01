// WorkManagerUtils.kt
package com.unison.appproductos.recordatorio

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.unison.appproductos.recordatorio.RecordatorioWorker
import java.util.concurrent.TimeUnit
import android.content.Context

object WorkManagerUtils {

    fun programarRecordatorio(
        context: Context,
        titulo: String,
        contenido: String,
        delayInMinutes: Long
    ) {
        val data = Data.Builder()
            .putString("titulo", titulo)
            .putString("contenido", contenido)
            .build()

        val recordatorioRequest = OneTimeWorkRequestBuilder<RecordatorioWorker>()
            .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(recordatorioRequest)
    }
}
