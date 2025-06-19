package com.example.sistemaasistenciarf.domain.usecase

import com.example.sistemaasistenciarf.data.model.Asistencia
import com.example.sistemaasistenciarf.data.repository.AsistenciaRepository

class RegistrarAsistenciaUseCase(private val repository: AsistenciaRepository) {
    suspend operator fun invoke(asistencia: Asistencia) {
        repository.registrar(asistencia)
    }
    fun obtenerAsistencias() = repository.obtenerAsistencias()

}