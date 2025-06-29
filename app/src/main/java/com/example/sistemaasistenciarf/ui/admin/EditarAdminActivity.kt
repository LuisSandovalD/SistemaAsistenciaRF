package com.example.sistemaasistenciarf.ui.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.model.UsuarioAdmin
import com.example.sistemaasistenciarf.hideSystemUI

class EditarAdminActivity : AppCompatActivity() {

    private val adminViewModel: AdminViewModel by viewModels()
    private var admin: UsuarioAdmin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_admin)

        hideSystemUI()


        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContraseña = findViewById<EditText>(R.id.etContraseña)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        // Recuperar el objeto UsuarioAdmin desde el intent
        admin = intent.getSerializableExtra("admin") as? UsuarioAdmin

        if (admin != null) {
            etNombre.setText(admin!!.nombre)
            etApellido.setText(admin!!.apellido)
            etCorreo.setText(admin!!.correo)
            etContraseña.setText(admin!!.contraseña)

            btnGuardar.setOnClickListener {
                val actualizado = admin!!.copy(
                    nombre = etNombre.text.toString().trim(),
                    apellido = etApellido.text.toString().trim(),
                    correo = etCorreo.text.toString().trim(),
                    contraseña = etContraseña.text.toString().trim()
                )

                adminViewModel.actualizar(actualizado)

                Toast.makeText(this, "Datos actualizados correctamente ✅", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "No se pudo cargar la información del administrador", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
