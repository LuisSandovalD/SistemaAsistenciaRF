/*
*package com.example.sistemaasistenciarf.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AsistenciaScreen(
    viewModel: AsistenciaViewModel,
    usuarioId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val asistencias by viewModel.asistencias.observeAsState(initial = emptyList())
    val listaUsuarios by viewModel.usuarios.observeAsState(initial = emptyList())

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                600
            )
        }
    }

    // Iniciar detección facial solo una vez cuando hay usuarios disponibles
    LaunchedEffect(listaUsuarios) {
        if (listaUsuarios.isNotEmpty()) {
            val detector = FaceDetectorManager(
                context = context,
                lifecycleOwner = lifecycleOwner,
                previewView = previewView,
                listaUsuarios = listaUsuarios,
                onUsuarioDetectado = { usuario ->
                    coroutineScope.launch {
                        viewModel.registrarAsistencia(
                            Asistencia(
                                usuarioId = usuario.id,
                                fecha = obtenerFechaActual(),
                                hora = obtenerHoraActual()
                            )
                        )
                    }
                }
            )
            detector.iniciar()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AndroidView(factory = { previewView })

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Asistencias registradas:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(asistencias) { asistencia ->
                Text(
                    text = "📅 ${asistencia.fecha} ⏰ ${asistencia.hora}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun obtenerFechaActual(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

fun obtenerHoraActual(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}

* */