package com.example.sistemaasistenciarf.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asistencias")
data class Asistencia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val nombreUsuario: String,
    val fechaHora: String
)
