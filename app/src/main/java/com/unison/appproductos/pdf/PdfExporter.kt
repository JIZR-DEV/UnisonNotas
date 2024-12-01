// PdfExporter.kt
package com.unison.appproductos.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.unison.appproductos.Models.Nota
import com.unison.appproductos.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfExporter {

    fun exportarNotaAPdf(context: Context, nota: Nota): File? {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        // Definir una página del PDF (Tamaño A4: 595x842 puntos)
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Dibujar el título
        paint.textSize = 24f
        paint.color = android.graphics.Color.BLACK
        canvas.drawText(nota.titulo, 40f, 50f, paint)

        // Dibujar el contenido
        paint.textSize = 16f
        canvas.drawText(nota.contenido, 40f, 100f, paint)

        // Puedes agregar más elementos como imágenes, colores de fondo, etc.

        // Finalizar la página
        pdfDocument.finishPage(page)

        // Crear el directorio donde se guardará el PDF
        val carpeta = File(context.getExternalFilesDir(null), "Documents/NotasPDF")
        if (!carpeta.exists()) {
            carpeta.mkdirs()
        }

        // Nombre del archivo PDF
        val nombreArchivo = "Nota_${nota.id}_${System.currentTimeMillis()}.pdf"
        val archivo = File(carpeta, nombreArchivo)

        return try {
            pdfDocument.writeTo(FileOutputStream(archivo))
            pdfDocument.close()
            archivo
        } catch (e: IOException) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun compartirPdf(context: Context, archivoPdf: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            archivoPdf
        )

        val compartirIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(compartirIntent, "Compartir nota como PDF"))
    }
}
