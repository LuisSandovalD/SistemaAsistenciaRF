package com.example.sistemaasistenciarf.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.livedata.observeAsState
import com.example.sistemaasistenciarf.data.model.Asistencia
import com.example.sistemaasistenciarf.recognition.FaceDetectorManager
import com.example.sistemaasistenciarf.ui.asistencia.AsistenciaViewModel

@Composable
fun AsistenciaScreen(
    viewModel: AsistenciaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val asistencias by viewModel.asistencias.observeAsState(initial = emptyList())

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                600
            )
        }
    }

    LaunchedEffect(Unit) {
        val detector = FaceDetectorManager(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = previewView
        ) { resultado ->
            val (nombre, fecha, hora) = resultado.split(",")
            viewModel.registrarAsistencia(
                Asistencia(nombre = nombre, fecha = fecha, hora = hora)
            )
        }
        detector.iniciar()
    }

    Column(modifier = modifier.padding(16.dp)) {
        AndroidView(factory = { previewView })

        Spacer(modifier = Modifier.height(16.dp))

        Text("Asistencias registradas:", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(asistencias) {
                Text("\uD83D\uDC64 ${it.nombre} | \uD83D\uDCC5 ${it.fecha} ⏰ ${it.hora}")
            }
        }
    }
}
