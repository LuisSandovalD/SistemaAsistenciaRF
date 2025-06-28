package com.example.sistemaasistenciarf.data.local.dao

import androidx.room.*
import com.example.sistemaasistenciarf.data.model.UsuarioAdmin

@Dao
interface AdminDao {

    @Query("SELECT * FROM usuarios_admin ORDER BY id DESC")
    suspend fun obtenerTodos(): List<UsuarioAdmin>

    @Query("SELECT * FROM usuarios_admin WHERE correo = :correo AND contraseña = :contraseña LIMIT 1")
    suspend fun login(correo: String, contraseña: String): UsuarioAdmin?

    @Insert
    suspend fun insertar(usuario: UsuarioAdmin)

    @Update
    suspend fun actualizar(usuario: UsuarioAdmin)

    @Delete
    suspend fun eliminar(usuario: UsuarioAdmin)
}
