package com.example.sistemaasistenciarf.ui.asistencia

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemaasistenciarf.data.model.Asistencia
import com.example.sistemaasistenciarf.domain.usecase.RegistrarAsistenciaUseCase
import kotlinx.coroutines.launch

class AsistenciaViewModel(
    private val useCase: RegistrarAsistenciaUseCase
) : ViewModel() {

    val asistencias: LiveData<List<Asistencia>> = useCase.obtenerAsistencias()

    fun registrarAsistencia(asistencia: Asistencia) {
        viewModelScope.launch {
            useCase(asistencia)
        }
    }
}
