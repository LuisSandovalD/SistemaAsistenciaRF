package com.example.sistemaasistenciarf.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sistemaasistenciarf.data.model.Usuario

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios ORDER BY id DESC")
    fun obtenerTodos(): LiveData<List<Usuario>>

    @Insert
    suspend fun insertar(usuario: Usuario)

    @Update
    suspend fun actualizar(usuario: Usuario)

    @Delete
    suspend fun eliminar(usuario: Usuario)
}
