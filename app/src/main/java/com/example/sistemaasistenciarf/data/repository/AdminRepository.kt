package com.example.sistemaasistenciarf.data.repository


import com.example.sistemaasistenciarf.data.local.dao.AdminDao
import com.example.sistemaasistenciarf.data.model.UsuarioAdmin

class AdminRepository(private val dao: AdminDao) {

    suspend fun obtenerTodos(): List<UsuarioAdmin> = dao.obtenerTodos()

    suspend fun login(correo: String, contraseña: String): UsuarioAdmin? =
        dao.login(correo, contraseña)

    suspend fun insertar(admin: UsuarioAdmin) = dao.insertar(admin)

    suspend fun actualizar(admin: UsuarioAdmin) = dao.actualizar(admin)

    suspend fun eliminar(admin: UsuarioAdmin) = dao.eliminar(admin)
}
