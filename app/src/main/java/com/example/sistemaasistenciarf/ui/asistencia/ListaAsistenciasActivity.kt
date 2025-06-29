package com.example.sistemaasistenciarf.ui.asistencia

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.hideSystemUI
import com.example.sistemaasistenciarf.viewmodel.AsistenciaViewModel

class ListaAsistenciasActivity : AppCompatActivity() {

    private val asistenciaViewModel: AsistenciaViewModel by viewModels()
    private lateinit var adaptador: AsistenciaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_asistencias)

        hideSystemUI()

        val usuarioId = intent.getIntExtra("usuarioId", -1)
        val nombreUsuario = intent.getStringExtra("nombreUsuario")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAsistencias)
        adaptador = AsistenciaAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListaAsistenciasActivity)
            adapter = adaptador
        }

        // Observa todas las asistencias y filtra por usuario
        asistenciaViewModel.asistencias.observe(this) { lista ->
            val filtradas = lista.filter { it.usuarioId == usuarioId }
            adaptador.actualizarLista(filtradas)
        }

        title = "Asistencias de $nombreUsuario"
    }
}