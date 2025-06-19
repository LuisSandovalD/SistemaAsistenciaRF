package com.example.sistemaasistenciarf.data.repository


import com.example.sistemaasistenciarf.data.local.dao.AsistenciaDao
import com.example.sistemaasistenciarf.data.model.Asistencia

class AsistenciaRepository(private val dao: AsistenciaDao) {
    fun obtenerAsistencias() = dao.obtenerAsistencias()
    suspend fun registrar(asistencia: Asistencia) = dao.insertar(asistencia)
}
