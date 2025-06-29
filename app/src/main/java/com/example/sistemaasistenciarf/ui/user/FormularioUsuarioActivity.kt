package com.example.sistemaasistenciarf.ui.user

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.model.Usuario
import com.example.sistemaasistenciarf.hideSystemUI
import com.example.sistemaasistenciarf.ml.MyFacenet
import com.example.sistemaasistenciarf.util.BitmapUtils
import com.example.sistemaasistenciarf.viewmodel.UsuarioViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.sqrt

class FormularioUsuarioActivity : AppCompatActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etCorreo: EditText
    private lateinit var switchEstado: Switch
    private lateinit var ivRostro: ImageView
    private lateinit var btnGuardar: Button
    private lateinit var btnCapturarRostro: Button

    private var usuarioExistente: Usuario? = null
    private var rutaImagen: String? = null
    private var embedding: FloatArray? = null

    private var modeloFacial: MyFacenet? = null

    private val seleccionarImagenLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == RESULT_OK) {
            val uri: Uri? = resultado.data?.data
            uri?.let {
                val inputStream: InputStream? = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                ivRostro.setImageBitmap(bitmap)

                rutaImagen = guardarImagenEnArchivo(bitmap)
                procesarEmbedding(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_usuario)

        hideSystemUI()


        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etCorreo = findViewById(R.id.etCorreo)
        switchEstado = findViewById(R.id.switchEstado)
        ivRostro = findViewById(R.id.ivRostro)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCapturarRostro = findViewById(R.id.btnCapturarRostro)

        usuarioExistente = intent.getSerializableExtra("usuario") as? Usuario
        usuarioExistente?.let {
            etNombre.setText(it.nombre)
            etApellido.setText(it.apellido)
            etCorreo.setText(it.correo)
            switchEstado.isChecked = it.estado
            rutaImagen = it.rutaRostro
            embedding = it.embedding
            mostrarImagenDesdeRuta(it.rutaRostro)
        }

        CoroutineScope(Dispatchers.IO).launch {
            modeloFacial = MyFacenet.Companion.newInstance(this@FormularioUsuarioActivity)
        }

        btnGuardar.setOnClickListener { guardarUsuario() }
        btnCapturarRostro.setOnClickListener { abrirGaleria() }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        seleccionarImagenLauncher.launch(intent)
    }

    private fun guardarImagenEnArchivo(bitmap: Bitmap): String {
        val fileName = "rostro_${System.currentTimeMillis()}.png"
        val file = File(filesDir, fileName)
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        return file.absolutePath
    }


    private fun procesarEmbedding(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val resized = BitmapUtils.resizeBitmap(bitmap, 160, 160)
                val vector = modeloFacial?.getEmbedding(resized)

                if (vector != null) {
                    embedding = normalizeL2(vector) // ✅ Normalización aplicada aquí
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@FormularioUsuarioActivity,
                            "✅ Rostro procesado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    throw Exception("Embedding nulo")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@FormularioUsuarioActivity,
                        "❌ Error procesando rostro",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun guardarUsuario() {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val estado = switchEstado.isChecked
        val rutaRostro = rutaImagen
        val embeddingActual = embedding

        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || rutaRostro == null || embeddingActual == null) {
            Toast.makeText(this, "Completa todos los campos y selecciona un rostro", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = Usuario(
            id = usuarioExistente?.id ?: 0,
            nombre = nombre,
            apellido = apellido,
            correo = correo,
            rutaRostro = rutaRostro,
            estado = estado,
            embedding = embeddingActual
        )

        if (usuarioExistente == null) {
            usuarioViewModel.insertar(usuario)
            Toast.makeText(this, "✅ Usuario guardado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            usuarioViewModel.actualizar(usuario)
            Toast.makeText(this, "✅ Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun mostrarImagenDesdeRuta(ruta: String) {
        try {
            val bitmap = BitmapFactory.decodeFile(ruta)
            ivRostro.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        modeloFacial?.close()
    }

    /**
     * 🔁 Normaliza un vector con L2 (euclideana) para que tenga magnitud 1
     */
    private fun normalizeL2(vector: FloatArray): FloatArray {
        val norm = sqrt(vector.fold(0f) { acc, v -> acc + v * v })
        return if (norm == 0f) vector else vector.map { it / norm }.toFloatArray()
    }
}