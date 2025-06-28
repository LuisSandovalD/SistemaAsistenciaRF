package com.example.sistemaasistenciarf.data.model


import androidx.room.Embedded
import androidx.room.Relation

data class UsuarioConAsistencias(
    @Embedded val usuario: Usuario,

    @Relation(
        parentColumn = "id",
        entityColumn = "usuarioId"
    )
    val asistencias: List<Asistencia>
)
