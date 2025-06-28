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

    fun iniciar() {
        if (cameraStarted) return  // ✅ Evita reinicialización múltiple
        cameraStarted = true

        val analyzer = FaceAnalyzer(context, usuariosEmbebidos, onUsuarioDetectado)
        CameraXHelper.iniciarCamara(context, lifecycleOwner, previewView, analyzer)
    }

    fun detener() {
        cameraStarted = false
        // Aquí puedes liberar recursos o detener CameraX si implementas esa lógica.
    }
}
