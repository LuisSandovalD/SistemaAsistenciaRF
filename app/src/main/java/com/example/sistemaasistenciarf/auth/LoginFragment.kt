package com.example.sistemaasistenciarf.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.local.AppDatabase
import com.example.sistemaasistenciarf.data.local.dao.AdminDao
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

        db = AppDatabase.obtenerBaseDeDatos(this)
        adminDao = db.adminDao()

        val emailField = findViewById<EditText>(R.id.usernameEditText)
        val passwordField = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerLink = findViewById<TextView>(R.id.registerLink)

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            // Room no permite en el hilo principal, usamos corrutina
            CoroutineScope(Dispatchers.IO).launch {
                val user = adminDao.login(email, password)
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        Toast.makeText(this@LoginFragment, "Bienvenido, ${user.nombre}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginFragment, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginFragment, "Datos incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterFragment::class.java))
        }
    }
}

