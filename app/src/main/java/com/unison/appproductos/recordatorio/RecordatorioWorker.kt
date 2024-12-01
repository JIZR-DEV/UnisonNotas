// RecordatorioWorker.kt
package com.unison.appproductos.recordatorio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.unison.appproductos.R

class RecordatorioWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val titulo = inputData.getString("titulo") ?: "Recordatorio de Nota"
        val contenido = inputData.getString("contenido") ?: "Tienes un recordatorio pendiente."

        enviarNotificacion(titulo, contenido)

        return Result.success()
    }

    private fun enviarNotificacion(titulo: String, contenido: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "recordatorio_channel"

        // Crear el canal de notificación si es necesario (solo para Android O y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombreCanal = "Recordatorios de Notas"
            val descripcionCanal = "Canal para recordatorios de notas"
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(channelId, nombreCanal, importancia).apply {
                description = descripcionCanal
            }
            notificationManager.createNotificationChannel(canal)
        }

        // Construir la notificación
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener este icono en res/drawable
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Enviar la notificación
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
