package com.example.sistemaasistenciarf.recognition

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

class FaceDetectorManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val onDetect: (String) -> Unit
) {
    fun iniciar() {
        val analyzer = FaceAnalyzer(onDetect)
        CameraXHelper.iniciarCamara(context, lifecycleOwner, previewView, analyzer)
    }
}