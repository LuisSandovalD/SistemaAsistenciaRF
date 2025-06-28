package com.example.sistemaasistenciarf.utils

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

object FaceNetHelper {

    // Carga el modelo desde assets
    private fun loadModelFile(context: Context): ByteBuffer {
        val fileDescriptor = context.assets.openFd("facenet.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Procesa la imagen y devuelve el embedding de 128 floats
    fun getFaceEmbedding(context: Context, bitmap: Bitmap): FloatArray {
        val resized = Bitmap.createScaledBitmap(bitmap, 160, 160, true)

        val byteBuffer = ByteBuffer.allocateDirect(1 * 160 * 160 * 3 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(160 * 160)
        resized.getPixels(intValues, 0, 160, 0, 0, 160, 160)

        for (pixel in intValues) {
            val r = ((pixel shr 16) and 0xFF).toFloat() / 255.0f
            val g = ((pixel shr 8) and 0xFF).toFloat() / 255.0f
            val b = (pixel and 0xFF).toFloat() / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        val interpreter = Interpreter(loadModelFile(context))
        val output = Array(1) { FloatArray(128) }
        interpreter.run(byteBuffer, output)
        interpreter.close()

        return output[0]
    }
}
