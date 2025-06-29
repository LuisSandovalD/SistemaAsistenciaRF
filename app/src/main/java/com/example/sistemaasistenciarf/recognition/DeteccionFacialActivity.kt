// File: DeteccionFacialActivity.kt
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

class DeteccionFacialActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var faceDetectorManager: FaceDetectorManager

    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private val asistenciaViewModel: AsistenciaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deteccion_facial)

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
                        try {
                            val resized = BitmapUtils.resizeBitmap(bitmap, 160, 160)
                            val embedding = modelo.getEmbedding(resized)
                            val normalizedEmbedding = normalizeL2(embedding)

                            UsuarioEmbebido(
                                usuario.id,
                                "${usuario.nombre} ${usuario.apellido}",
                                normalizedEmbedding
                            )
                        } catch (e: Exception) {
                            Log.w("DeteccionFacial", "⚠️ Error procesando rostro de ${usuario.nombre}", e)
                            null
                        }
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
                            "⚠️ No hay usuarios con rostros válidos",
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

    private fun normalizeL2(vector: FloatArray): FloatArray {
        val norm = sqrt(vector.fold(0f) { acc, v -> acc + v * v })
        return if (norm == 0f) vector else vector.map { it / norm }.toFloatArray()
    }
}
