package com.example.sistemaasistenciarf.util

import android.graphics.*
import android.media.Image
import android.graphics.Rect
import java.io.ByteArrayOutputStream
import java.io.File

object BitmapUtils {

    /**
     * Carga un bitmap desde una ruta de archivo.
     */
    fun loadBitmapFromPath(path: String): Bitmap? {
        return try {
            val file = File(path)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Convierte una imagen YUV_420_888 (formato de CameraX) a Bitmap RGB compatible con TFLite.
     * Convierte usando NV21 → JPEG → Bitmap.
     */
    fun imageToBitmap(image: Image, rotationDegrees: Int): Bitmap {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        // Ensamblar arreglo NV21 (YVU)
        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        // Convertir NV21 a JPEG
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val jpegBytes = out.toByteArray()

        // Decodificar JPEG a Bitmap y rotarlo
        var bitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
        bitmap = rotateBitmap(bitmap, rotationDegrees)

        // 🔁 Forzar configuración ARGB_8888 (4 canales) compatible con TensorFlow
        return bitmap.copy(Bitmap.Config.ARGB_8888, false)
    }

    /**
     * Rota un bitmap según el ángulo especificado.
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees == 0) return bitmap
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Recorta una región facial del bitmap asegurando límites válidos.
     */
    fun cropFace(bitmap: Bitmap, rect: Rect): Bitmap {
        val safeRect = Rect(
            rect.left.coerceAtLeast(0),
            rect.top.coerceAtLeast(0),
            rect.right.coerceAtMost(bitmap.width),
            rect.bottom.coerceAtMost(bitmap.height)
        )

        if (safeRect.width() <= 0 || safeRect.height() <= 0) {
            throw IllegalArgumentException("❌ Rectángulo de rostro inválido: $safeRect")
        }

        return Bitmap.createBitmap(bitmap, safeRect.left, safeRect.top, safeRect.width(), safeRect.height())
    }


    /**
     * Redimensiona el bitmap a las dimensiones requeridas por el modelo (e.g., 160x160) y fuerza ARGB_8888.
     */
    fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val resized = Bitmap.createScaledBitmap(bitmap, width, height, true)
        return resized.copy(Bitmap.Config.ARGB_8888, false)
    }
}
