package com.example.sistemaasistenciarf.recognition

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.common.InputImage
import java.text.SimpleDateFormat
import java.util.*

class FaceAnalyzer(private val onFaceDetected: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val detector = FaceDetection.getClient()

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    onFaceDetected("UsuarioDetectado,$date,$time")
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}