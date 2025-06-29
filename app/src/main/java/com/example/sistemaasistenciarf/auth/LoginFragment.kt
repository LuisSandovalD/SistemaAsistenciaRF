package com.example.sistemaasistenciarf.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.local.database.AppDatabase
import com.example.sistemaasistenciarf.data.local.dao.AdminDao
import com.example.sistemaasistenciarf.data.model.UsuarioAdmin
import com.example.sistemaasistenciarf.hideSystemUI
import com.example.sistemaasistenciarf.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : ComponentActivity() {
    private lateinit var db: AppDatabase
    private lateinit var adminDao: AdminDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

        hideSystemUI()

        db = AppDatabase.obtenerBaseDeDatos(this)
        adminDao = db.adminDao()

        val emailField = findViewById<EditText>(R.id.usernameEditText)
        val passwordField = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerLink = findViewById<TextView>(R.id.registerLink)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val user = adminDao.login(email, password)
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        Toast.makeText(
                            this@LoginFragment,
                            "Bienvenido, ${user.nombre}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // 👉 Enviar el admin logueado correctamente a MainActivity
                        val intent = Intent(this@LoginFragment, MainActivity::class.java)
                        intent.putExtra("admin", user)
                        startActivity(intent)

                        // ✅ Cierra la pantalla de login para que no regrese ni se relance
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginFragment,
                            "Datos incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterFragment::class.java))
        }
    }
}
