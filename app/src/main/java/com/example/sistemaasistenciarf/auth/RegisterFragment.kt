package com.example.sistemaasistenciarf.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.model.UsuarioAdmin
import com.example.sistemaasistenciarf.data.local.AppDatabase
import com.example.sistemaasistenciarf.data.local.dao.AdminDao
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

        db = AppDatabase.obtenerBaseDeDatos(this)
        adminDao = db.adminDao()

        val nameUser = findViewById<EditText>(R.id.nameUser)
        val lastNameUser = findViewById<EditText>(R.id.firtUser)
        val emailUser = findViewById<EditText>(R.id.emailUser)
        val passwordUser = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val accessLink = findViewById<TextView>(R.id.accessLink)

        loginButton.setOnClickListener {
            val admin = UsuarioAdmin(
                nombre = nameUser.text.toString(),
                apellido = lastNameUser.text.toString(),
                correo = emailUser.text.toString(),
                contraseña = passwordUser.text.toString()
            )

            CoroutineScope(Dispatchers.IO).launch {
                adminDao.insertar(admin)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterFragment, "Administrador registrado", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterFragment, LoginFragment::class.java))
                    finish()
                }
            }
        }

        accessLink.setOnClickListener {
            startActivity(Intent(this, LoginFragment::class.java))
        }
    }
}
