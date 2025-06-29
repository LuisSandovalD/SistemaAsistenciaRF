package com.example.sistemaasistenciarf.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemaasistenciarf.data.local.database.AppDatabase
import com.example.sistemaasistenciarf.data.model.Asistencia
import kotlinx.coroutines.launch

class AsistenciaViewModel(application: Application) : AndroidViewModel(application) {

    private val asistenciaDao = AppDatabase.obtenerBaseDeDatos(application).asistenciaDao()

    val asistencias = asistenciaDao.obtenerAsistencias()

    fun insertar(asistencia: Asistencia) {
        viewModelScope.launch {
            asistenciaDao.insertar(asistencia)
        }
    }
}
