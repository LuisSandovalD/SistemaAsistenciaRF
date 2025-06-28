package com.example.sistemaasistenciarf.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.DataType
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

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

    fun process(input: TensorBuffer): Output {
        // ⚠️ Asegura que la salida sea de tipo FLOAT32
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 128), DataType.FLOAT32)

        // 👇 Ejecutar el modelo
        interpreter.run(input.buffer, outputBuffer.buffer)

        return Output(outputBuffer)
    }

    fun close() {
        interpreter.close()
    }

    data class Output(val outputFeature0AsTensorBuffer: TensorBuffer)
}
