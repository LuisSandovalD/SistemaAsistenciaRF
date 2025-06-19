package com.example.sistemaasistenciarf.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sistemaasistenciarf.data.model.Asistencia
import com.example.sistemaasistenciarf.data.local.dao.AsistenciaDao

@Database(entities = [Asistencia::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun asistenciaDao(): AsistenciaDao
}