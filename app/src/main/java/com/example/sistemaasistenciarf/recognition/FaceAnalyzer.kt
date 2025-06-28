package com.example.sistemaasistenciarf.recognition

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.sistemaasistenciarf.ml.MyFacenet
import com.example.sistemaasistenciarf.util.BitmapUtils
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.sqrt

data class UsuarioEmbebido(
    val id: Int,
    val nombreCompleto: String,
    val embedding: FloatArray
)

class FaceAnalyzer(
    private val context: Context,
    private val usuariosEmbebidos: List<UsuarioEmbebido>,
    private val onUsuarioDetectado: (UsuarioEmbebido) -> Unit
) : ImageAnalysis.Analyzer {

    private val detectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(detectorOptions)

    private var modeloFacial: MyFacenet? = null
    private var modeloListo = false
    private var ultimaDeteccion = 0L

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                modeloFacial = MyFacenet.newInstance(context)
                modeloListo = true
            } catch (e: Exception) {
                Log.e("FaceAnalyzer", "❌ Error cargando el modelo Facenet", e)
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty() && modeloListo && modeloFacial != null) {
                    val face = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }!!
                    val bitmap = BitmapUtils.imageToBitmap(mediaImage, rotationDegrees)
                    val faceBitmap = BitmapUtils.cropFace(bitmap, face.boundingBox)

                    CoroutineScope(Dispatchers.Default).launch {
                        try {
                            val resizedBitmap = BitmapUtils.resizeBitmap(faceBitmap, 160, 160)

                            val tensorImage = TensorImage(DataType.FLOAT32)
                            tensorImage.load(resizedBitmap)

                            val input = TensorBuffer.createFixedSize(
                                intArrayOf(1, 160, 160, 3),
                                DataType.FLOAT32
                            )
                            input.loadBuffer(tensorImage.buffer) // ✅ Correcto tamaño: 307200 bytes

                            val currentEmbedding = modeloFacial!!
                                .process(input)
                                .outputFeature0AsTensorBuffer
                                .floatArray

                            for (usuario in usuariosEmbebidos) {
                                val distancia = calcularDistancia(currentEmbedding, usuario.embedding)
                                Log.d("FaceAnalyzer", "Distancia con ${usuario.nombreCompleto}: $distancia")

                                if (distancia < 1.0f && System.currentTimeMillis() - ultimaDeteccion > 5000) {
                                    ultimaDeteccion = System.currentTimeMillis()
                                    withContext(Dispatchers.Main) {
                                        onUsuarioDetectado(usuario)
                                    }
                                    break
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("FaceAnalyzer", "❌ Error procesando rostro", e)
                        } finally {
                            imageProxy.close()
                        }
                    }
                } else {
                    imageProxy.close()
                }
            }
            .addOnFailureListener {
                imageProxy.close()
            }
    }

    private fun calcularDistancia(e1: FloatArray, e2: FloatArray): Float {
        return sqrt(e1.zip(e2) { a, b -> (a - b) * (a - b) }.sum())
    }

    fun cerrarModelo() {
        modeloFacial?.close()
        modeloListo = false
    }
}
