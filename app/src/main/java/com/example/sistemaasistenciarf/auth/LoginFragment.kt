package com.example.sistemaasistenciarf.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.sistemaasistenciarf.R

import android.widget.Toast
import android.content.Intent
import com.google.android.material.textfield.TextInputEditText

@SuppressLint("CustomSplashScreen")
class LoginFragment : ComponentActivity() {

    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_login)

        dbHelper = DBHelper(this)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Campos vacíos", Toast.LENGTH_SHORT).show()
            } else {
                val isValid = dbHelper.checkUser(email, password)
                if (isValid) {
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AuthFragment::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
