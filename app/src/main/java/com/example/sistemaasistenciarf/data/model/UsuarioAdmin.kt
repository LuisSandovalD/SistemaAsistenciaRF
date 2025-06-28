// Ruta: com.example.sistemaasistenciarf.data.model.UsuarioAdmin.kt
package com.example.sistemaasistenciarf.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios_admin")
data class UsuarioAdmin(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contraseña: String
)
