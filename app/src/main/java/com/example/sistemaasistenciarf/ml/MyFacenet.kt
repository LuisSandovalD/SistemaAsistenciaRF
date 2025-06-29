package com.example.sistemaasistenciarf.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.DataType
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import kotlin.collections.toFloatArray
import kotlin.math.sqrt

class MyFacenet(private val interpreter: Interpreter) {

    companion object {
        fun newInstance(context: Context): MyFacenet {
            val modelBuffer = loadModelFile(context, "facenet.tflite")
            val interpreter = Interpreter(modelBuffer)
            return MyFacenet(interpreter)
        }

        private fun loadModelFile(context: Context, fileName: String): ByteBuffer {
            val fileDescriptor = context.assets.openFd(fileName)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }

    /**
     * Devuelve el embedding normalizado (vector de 128 float con L2 norm = 1)
     */
    fun getEmbedding(bitmap: Bitmap): FloatArray {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(160, 160, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(127.5f, 127.5f)) // ⚠️ Normaliza a [-1, 1]
            .build()

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        val processedImage = imageProcessor.process(tensorImage)

        val input = TensorBuffer.createFixedSize(intArrayOf(1, 160, 160, 3), DataType.FLOAT32)
        input.loadBuffer(processedImage.buffer)

        val output = TensorBuffer.createFixedSize(intArrayOf(1, 128), DataType.FLOAT32)
        interpreter.run(input.buffer, output.buffer)

        return l2Normalize(output.floatArray)
    }

    /**
     * Normalización L2: hace que la magnitud del vector sea 1
     */
    private fun l2Normalize(embedding: FloatArray): FloatArray {
        var sum = 0f
        for (value in embedding) {
            sum += value * value
        }
        val norm = sqrt(sum)

        val normalized = FloatArray(embedding.size)
        for (i in embedding.indices) {
            normalized[i] = embedding[i] / norm
        }
        return normalized
    }

    fun close() {
        interpreter.close()
    }
}
