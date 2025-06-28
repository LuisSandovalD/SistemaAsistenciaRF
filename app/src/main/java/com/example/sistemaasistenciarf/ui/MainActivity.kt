package com.example.sistemaasistenciarf.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.recognition.DeteccionFacialActivity
import com.example.sistemaasistenciarf.ui.admin.FormularioUsuarioActivity
import com.example.sistemaasistenciarf.ui.admin.ListaUsuariosActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_registerUser).setOnClickListener {
            startActivity(Intent(this, FormularioUsuarioActivity::class.java))
        }

        findViewById<Button>(R.id.btnVerUsuarios).setOnClickListener {
            startActivity(Intent(this, ListaUsuariosActivity::class.java))
        }

        findViewById<Button>(R.id.btnTomarAsistencia).setOnClickListener {
            startActivity(Intent(this, DeteccionFacialActivity::class.java))
        }
    }
}
