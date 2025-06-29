package com.example.sistemaasistenciarf.repository

import androidx.lifecycle.LiveData
import com.example.sistemaasistenciarf.data.local.dao.UsuarioDao
import com.example.sistemaasistenciarf.data.model.Usuario

class UsuarioRepository(private val usuarioDao: UsuarioDao) {
    fun obtenerUsuarios(): LiveData<List<Usuario>> = usuarioDao.obtenerTodos()

    suspend fun insertar(usuario: Usuario) = usuarioDao.insertar(usuario)

    suspend fun actualizar(usuario: Usuario) = usuarioDao.actualizar(usuario)

    suspend fun eliminar(usuario: Usuario) = usuarioDao.eliminar(usuario)
}
