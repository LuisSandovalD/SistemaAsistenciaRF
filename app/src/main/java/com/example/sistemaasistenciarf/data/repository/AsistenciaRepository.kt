package com.example.sistemaasistenciarf.data.repository


import androidx.lifecycle.LiveData
import com.example.sistemaasistenciarf.data.local.dao.AsistenciaDao
import com.example.sistemaasistenciarf.data.model.Asistencia

class AsistenciaRepository(private val dao: AsistenciaDao) {

    val todasLasAsistencias: LiveData<List<Asistencia>> = dao.obtenerAsistencias()

    suspend fun insertar(asistencia: Asistencia) {
        dao.insertar(asistencia)
    }
}
