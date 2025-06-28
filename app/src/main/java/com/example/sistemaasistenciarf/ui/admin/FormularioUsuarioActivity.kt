package com.example.sistemaasistenciarf.ui.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.model.Usuario
import com.example.sistemaasistenciarf.viewmodel.UsuarioViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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

    // Abrir galería para seleccionar imagen
    private val seleccionarImagenLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = resultado.data?.data
            uri?.let {
                val inputStream: InputStream? = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                ivRostro.setImageBitmap(bitmap)

                // Guardar imagen en almacenamiento interno
                rutaImagen = guardarImagenEnArchivo(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_usuario)

        // Vincular vistas
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etCorreo = findViewById(R.id.etCorreo)
        switchEstado = findViewById(R.id.switchEstado)
        ivRostro = findViewById(R.id.ivRostro)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCapturarRostro = findViewById(R.id.btnCapturarRostro)

        // Comprobar si se va a editar un usuario existente
        usuarioExistente = intent.getSerializableExtra("usuario") as? Usuario
        usuarioExistente?.let {
            etNombre.setText(it.nombre)
            etApellido.setText(it.apellido)
            etCorreo.setText(it.correo)
            switchEstado.isChecked = it.estado
            rutaImagen = it.rutaRostro
            mostrarImagenDesdeRuta(it.rutaRostro)
        }

        // Acciones de botones
        btnGuardar.setOnClickListener { guardarUsuario() }
        btnCapturarRostro.setOnClickListener { abrirGaleria() }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        seleccionarImagenLauncher.launch(intent)
    }

    private fun guardarImagenEnArchivo(bitmap: Bitmap): String {
        val fileName = "rostro_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        }
        return file.absolutePath
    }

    private fun guardarUsuario() {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val estado = switchEstado.isChecked
        val rutaRostro = rutaImagen

        // Validación básica
        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || rutaRostro == null) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = Usuario(
            id = usuarioExistente?.id ?: 0,
            nombre = nombre,
            apellido = apellido,
            correo = correo,
            rutaRostro = rutaRostro,
            estado = estado
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
}
