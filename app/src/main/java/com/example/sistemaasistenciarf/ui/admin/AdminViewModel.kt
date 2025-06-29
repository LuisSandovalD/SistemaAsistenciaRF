package com.example.sistemaasistenciarf.ui.admin


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemaasistenciarf.data.local.database.AppDatabase
import com.example.sistemaasistenciarf.data.model.UsuarioAdmin
import com.example.sistemaasistenciarf.data.repository.AdminRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AdminRepository

    init {
        val dao = AppDatabase.obtenerBaseDeDatos(application).adminDao()
        repository = AdminRepository(dao)
    }

    fun actualizar(admin: UsuarioAdmin) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.actualizar(admin)
        }
    }
}
