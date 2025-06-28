package com.example.sistemaasistenciarf.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.sistemaasistenciarf.data.local.AppDatabase
import com.example.sistemaasistenciarf.data.model.Asistencia
import com.example.sistemaasistenciarf.data.model.Usuario
import com.example.sistemaasistenciarf.repository.UsuarioRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UsuarioRepository
    val usuarios: LiveData<List<Usuario>>

    // 👉 DAO para registrar asistencia correctamente
    private val asistenciaDao = AppDatabase.obtenerBaseDeDatos(application).asistenciaDao()

    init {
        val usuarioDao = AppDatabase.obtenerBaseDeDatos(application).usuarioDao()
        repository = UsuarioRepository(usuarioDao)
        usuarios = repository.obtenerUsuarios()
    }

    fun insertar(usuario: Usuario) = viewModelScope.launch {
        repository.insertar(usuario)
    }

    fun actualizar(usuario: Usuario) = viewModelScope.launch {
        repository.actualizar(usuario)
    }

    fun eliminar(usuario: Usuario) = viewModelScope.launch {
        repository.eliminar(usuario)
    }

    fun registrarAsistencia(usuario: Usuario) = viewModelScope.launch {
        val fechaHora = obtenerFechaActual()
        val asistencia = Asistencia(
            usuarioId = usuario.id,
            nombreUsuario = "${usuario.nombre} ${usuario.apellido}",
            fechaHora = fechaHora
        )
        asistenciaDao.insertar(asistencia)
    }

    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
