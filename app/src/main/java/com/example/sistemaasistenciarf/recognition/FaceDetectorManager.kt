package com.example.sistemaasistenciarf.recognition

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

class FaceDetectorManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val usuariosEmbebidos: List<UsuarioEmbebido>,
    private val onUsuarioDetectado: (UsuarioEmbebido) -> Unit
) {

    private var cameraStarted = false
    private var faceAnalyzer: FaceAnalyzer? = null

    fun iniciar() {
        if (cameraStarted) return  // ✅ Evita reinicialización múltiple
        cameraStarted = true

        faceAnalyzer = FaceAnalyzer(context, usuariosEmbebidos, onUsuarioDetectado)
        CameraXHelper.iniciarCamara(context, lifecycleOwner, previewView, faceAnalyzer!!)
    }

    fun close() {
        cameraStarted = false

        // Opcional: cerrar modelo facial y limpiar recursos si necesitas
        faceAnalyzer?.cerrarModelo()
        faceAnalyzer = null
    }
}
