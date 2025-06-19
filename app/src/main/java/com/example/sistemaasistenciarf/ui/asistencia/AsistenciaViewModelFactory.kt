package com.example.sistemaasistenciarf.ui.asistencia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sistemaasistenciarf.domain.usecase.RegistrarAsistenciaUseCase

class AsistenciaViewModelFactory(
    private val useCase: RegistrarAsistenciaUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AsistenciaViewModel(useCase) as T
    }
}
