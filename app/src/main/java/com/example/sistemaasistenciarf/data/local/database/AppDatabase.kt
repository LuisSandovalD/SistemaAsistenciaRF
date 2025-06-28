// Ruta: com.example.sistemaasistenciarf.data.local.AppDatabase.kt
package com.example.sistemaasistenciarf.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sistemaasistenciarf.data.local.dao.*
import com.example.sistemaasistenciarf.data.model.*

@Database(
    entities = [
        UsuarioAdmin::class,
        Usuario::class,
        Asistencia::class
    ],
    version = 4, // Asegúrate de incrementar si haces cambios
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAO para administrador (UsuarioAdmin)
    abstract fun adminDao(): AdminDao

    // DAO para profesores (Usuario)
    abstract fun usuarioDao(): UsuarioDao

    // DAO para asistencias
    abstract fun asistenciaDao(): AsistenciaDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtenerBaseDeDatos(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "asistencia_db"
                )
                    .fallbackToDestructiveMigration() // ⚠️ Borra datos si hay cambios estructurales
                    .build()
                    .also { instancia = it }
            }
        }
    }
}
