package com.example.sistemaasistenciarf.recognition

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

object CameraXHelper {

    fun iniciarCamara(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        analyzer: ImageAnalysis.Analyzer
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = try {
                cameraProviderFuture.get()
            } catch (e: Exception) {
                Log.e("CameraXHelper", "❌ No se pudo obtener cameraProvider", e)
                return@addListener
            }

            val preview = Preview.Builder()
                .build()
                .apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

            // ✅ Usar resolución más estable (ej. 640x480) para evitar imágenes demasiado grandes
            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(android.util.Size(640, 480)) // resolución común compatible
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(ContextCompat.getMainExecutor(context), analyzer)
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
                Log.d("CameraXHelper", "✅ Cámara iniciada correctamente")
            } catch (e: Exception) {
                Log.e("CameraXHelper", "❌ Error al vincular la cámara", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
