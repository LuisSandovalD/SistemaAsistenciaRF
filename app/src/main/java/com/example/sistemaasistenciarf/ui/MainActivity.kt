package com.example.sistemaasistenciarf.ui

import android.os.Bundle
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.sistemaasistenciarf.data.local.database.AppDatabase
import com.example.sistemaasistenciarf.data.repository.AsistenciaRepository
import com.example.sistemaasistenciarf.domain.usecase.RegistrarAsistenciaUseCase
import com.example.sistemaasistenciarf.ui.asistencia.AsistenciaViewModel
import com.example.sistemaasistenciarf.ui.asistencia.AsistenciaViewModelFactory
import com.example.sistemaasistenciarf.ui.components.CustomToolbar
import com.example.sistemaasistenciarf.ui.theme.SistemaAsistenciaRFTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ Solicita el permiso de cámara si no ha sido concedido
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
        }

        // Inicializar la base de datos y dependencias
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "asistencia-db"
        ).build()

        val dao = database.asistenciaDao()
        val repository = AsistenciaRepository(dao)
        val useCase = RegistrarAsistenciaUseCase(repository)

        setContent {
            SistemaAsistenciaRFTheme {
                val viewModel: AsistenciaViewModel = viewModel(
                    factory = AsistenciaViewModelFactory(useCase)
                )

                Scaffold(
                    topBar = {
                        CustomToolbar(title = "Registro de Asistencia")
                    }
                ) { paddingValues ->
                    AsistenciaScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
