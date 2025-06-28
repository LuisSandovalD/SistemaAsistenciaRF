// DeteccionFacialActivity.kt
package com.example.sistemaasistenciarf.recognition

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.model.Asistencia
import com.example.sistemaasistenciarf.ml.MyFacenet
import com.example.sistemaasistenciarf.util.BitmapUtils
import com.example.sistemaasistenciarf.viewmodel.AsistenciaViewModel
import com.example.sistemaasistenciarf.viewmodel.UsuarioViewModel
import kotlinx.coroutines.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.text.SimpleDateFormat
import java.util.*

class DeteccionFacialActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var faceDetectorManager: FaceDetectorManager

    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private val asistenciaViewModel: AsistenciaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deteccion_facial)

        // Solicitud de permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            Toast.makeText(this, "⚠️ Se requiere permiso de cámara", Toast.LENGTH_LONG).show()
            return
        }

        previewView = findViewById(R.id.previewView)
        findViewById<Button>(R.id.btnCerrar).setOnClickListener { finish() }

        usuarioViewModel.usuarios.observe(this) { listaUsuarios ->
            CoroutineScope(Dispatchers.Default).launch {
                val modelo = try {
                    withContext(Dispatchers.IO) {
                        MyFacenet.newInstance(applicationContext)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@DeteccionFacialActivity,
                            "❌ Error al cargar el modelo",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    return@launch
                }

                val usuariosEmbebidos = listaUsuarios.mapNotNull { usuario ->
                    val bitmap = BitmapUtils.loadBitmapFromPath(usuario.rutaRostro)
                    if (bitmap != null) {
                        val resized = BitmapUtils.resizeBitmap(bitmap, 160, 160)

                        // Convertir a TensorImage con tipo FLOAT32
                        val tensorImage = TensorImage(DataType.FLOAT32)
                        tensorImage.load(resized)
                        val inputBuffer = tensorImage.buffer

                        val input = TensorBuffer.createFixedSize(
                            intArrayOf(1, 160, 160, 3),
                            DataType.FLOAT32
                        )
                        input.loadBuffer(inputBuffer)

                        val embedding = modelo.process(input).outputFeature0AsTensorBuffer.floatArray

                        UsuarioEmbebido(
                            usuario.id,
                            "${usuario.nombre} ${usuario.apellido}",
                            embedding
                        )
                    } else {
                        Log.w("DeteccionFacial", "⚠️ No se pudo cargar rostro para ${usuario.nombre}")
                        null
                    }
                }

                modelo.close()

                withContext(Dispatchers.Main) {
                    if (usuariosEmbebidos.isEmpty()) {
                        Toast.makeText(
                            this@DeteccionFacialActivity,
                            "⚠️ No hay rostros registrados",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                        return@withContext
                    }

                    faceDetectorManager = FaceDetectorManager(
                        context = this@DeteccionFacialActivity,
                        lifecycleOwner = this@DeteccionFacialActivity,
                        previewView = previewView,
                        usuariosEmbebidos = usuariosEmbebidos,
                        onUsuarioDetectado = { usuario ->
                            val asistencia = Asistencia(
                                usuarioId = usuario.id,
                                nombreUsuario = usuario.nombreCompleto,
                                fechaHora = obtenerFechaHoraActual()
                            )
                            asistenciaViewModel.insertar(asistencia)

                            Toast.makeText(
                                this@DeteccionFacialActivity,
                                "✅ Asistencia registrada para: ${usuario.nombreCompleto}",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    )
                    faceDetectorManager.iniciar()
                }
            }
        }
    }

    private fun obtenerFechaHoraActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }
}
