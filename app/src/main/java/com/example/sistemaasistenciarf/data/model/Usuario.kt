package com.example.sistemaasistenciarf.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

fun getFechaActual(): String {
    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formato.format(Date())
}

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val rutaRostro: String,
    val estado: Boolean = true,
    val fechaRegistro: String = getFechaActual(),
    val embedding: FloatArray
) : Serializable
