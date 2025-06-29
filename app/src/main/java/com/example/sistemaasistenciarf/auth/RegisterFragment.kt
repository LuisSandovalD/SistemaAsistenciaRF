package com.example.sistemaasistenciarf.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.model.UsuarioAdmin
import com.example.sistemaasistenciarf.data.local.database.AppDatabase
import com.example.sistemaasistenciarf.data.local.dao.AdminDao
import com.example.sistemaasistenciarf.hideSystemUI
import com.example.sistemaasistenciarf.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : ComponentActivity() {
    private lateinit var db: AppDatabase
    private lateinit var adminDao: AdminDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_register)

        hideSystemUI()

        db = AppDatabase.obtenerBaseDeDatos(this)
        adminDao = db.adminDao()

        val nameUser = findViewById<EditText>(R.id.nameUser)
        val lastNameUser = findViewById<EditText>(R.id.lastNameUser)
        val emailUser = findViewById<EditText>(R.id.emailUser)
        val passwordUser = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val accessLink = findViewById<TextView>(R.id.accessLink)

        loginButton.setOnClickListener {
            val nombre = nameUser.text.toString().trim()
            val apellido = lastNameUser.text.toString().trim()
            val correo = emailUser.text.toString().trim()
            val contraseña = passwordUser.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoAdmin = UsuarioAdmin(
                nombre = nombre,
                apellido = apellido,
                correo = correo,
                contraseña = contraseña
            )

            CoroutineScope(Dispatchers.IO).launch {
                // Insertar el nuevo admin
                adminDao.insertar(nuevoAdmin)

                // Obtener el último admin insertado
                val adminRegistrado = adminDao.login(correo, contraseña)

                withContext(Dispatchers.Main) {
                    if (adminRegistrado != null) {
                        Toast.makeText(this@RegisterFragment, "Administrador registrado", Toast.LENGTH_SHORT).show()

                        // Ir a MainActivity con el admin registrado
                        val intent = Intent(this@RegisterFragment, MainActivity::class.java)
                        intent.putExtra("admin", adminRegistrado)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterFragment, "Ocurrió un error al registrar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        accessLink.setOnClickListener {
            startActivity(Intent(this, LoginFragment::class.java))
        }
    }
}
