package com.example.sistemaasistenciarf.recognition

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
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

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .enableTracking()
            .build()
    )

    private var modeloFacial: MyFacenet? = null
    private var modeloListo = false
    private var ultimaDeteccion = 0L
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Puedes bajarlo más adelante a 0.6 o 0.5
    private val UMBRAL_RECONOCIMIENTO = 0.9f
    private val TIEMPO_ENTRE_DETECCIONES_MS = 20000L

    init {
        coroutineScope.launch {
            try {
                modeloFacial = MyFacenet.newInstance(context)
                modeloListo = true
            } catch (e: Exception) {
                Log.e("FaceAnalyzer", "❌ Error cargando modelo", e)
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return imageProxy.close()
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty() && modeloListo && modeloFacial != null) {
                    val rostroMayor = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }
                        ?: return@addOnSuccessListener

                    if (rostroMayor.boundingBox.width() < 100 || rostroMayor.boundingBox.height() < 100) {
                        imageProxy.close()
                        return@addOnSuccessListener
                    }

                    val bitmap = BitmapUtils.imageToBitmap(mediaImage, rotationDegrees)
                    val rostroBitmap = BitmapUtils.cropFace(bitmap, rostroMayor.boundingBox)

                    procesarRostro(imageProxy, rostroBitmap)
                } else {
                    imageProxy.close()
                }
            }
            .addOnFailureListener {
                Log.e("FaceAnalyzer", "❌ Error en detección facial", it)
                imageProxy.close()
            }
    }

    private fun procesarRostro(imageProxy: ImageProxy, rostroBitmap: Bitmap) {
        coroutineScope.launch {
            try {
                val resized = BitmapUtils.resizeBitmap(rostroBitmap, 160, 160)
                val embedding = modeloFacial!!.getEmbedding(resized)
                val embeddingNormalizado = normalizeL2(embedding)

                verificarCoincidencia(embeddingNormalizado)
            } catch (e: Exception) {
                Log.e("FaceAnalyzer", "❌ Error procesando rostro", e)
            } finally {
                imageProxy.close()
            }
        }
    }

    private suspend fun verificarCoincidencia(embeddingActual: FloatArray) {
        var mejorUsuario: UsuarioEmbebido? = null
        var menorDistancia = Float.MAX_VALUE

        for (usuario in usuariosEmbebidos) {
            val distancia = calcularDistancia(embeddingActual, normalizeL2(usuario.embedding))

            Log.d("FaceAnalyzer", "🔍 Distancia con ${usuario.nombreCompleto}: $distancia")

            if (distancia < menorDistancia) {
                menorDistancia = distancia
                mejorUsuario = usuario
            }
        }

        if (mejorUsuario != null &&
            menorDistancia < UMBRAL_RECONOCIMIENTO &&
            System.currentTimeMillis() - ultimaDeteccion > TIEMPO_ENTRE_DETECCIONES_MS
        ) {
            ultimaDeteccion = System.currentTimeMillis()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "✅ ${mejorUsuario.nombreCompleto} (dist: %.4f)".format(menorDistancia),
                    Toast.LENGTH_SHORT
                ).show()
                onUsuarioDetectado(mejorUsuario)
            }
        }
    }

    private fun calcularDistancia(e1: FloatArray, e2: FloatArray): Float {
        return sqrt(e1.zip(e2) { a, b -> (a - b) * (a - b) }.sum())
    }

    private fun normalizeL2(vector: FloatArray): FloatArray {
        val norm = sqrt(vector.map { it * it }.sum())
        return if (norm == 0f) vector else vector.map { it / norm }.toFloatArray()
    }

    fun cerrarModelo() {
        modeloFacial?.close()
        modeloListo = false
    }
}
