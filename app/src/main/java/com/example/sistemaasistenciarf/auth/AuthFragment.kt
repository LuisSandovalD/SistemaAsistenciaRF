package com.example.sistemaasistenciarf.auth

import com.example.sistemaasistenciarf.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.sistemaasistenciarf.hideSystemUI

@SuppressLint("CustomSplashScreen")
class AuthFragment : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.auth)

        val btnLogin = findViewById<Button>(R.id.buttonLogin)
        val btnRegister = findViewById<Button>(R.id.buttonRegister)

        btnLogin.setOnClickListener {
            val intentActivity = Intent(this, LoginFragment::class.java)
            startActivity(intentActivity)
            finish()
        }
        btnRegister.setOnClickListener {
            val intentRegister = Intent(this, RegisterFragment::class.java)
            startActivity(intentRegister)
            finish()
        }
        hideSystemUI()
    }

}
