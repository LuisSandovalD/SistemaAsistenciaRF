package com.example.sistemaasistenciarf.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sistemaasistenciarf.data.local.dao.*
import com.example.sistemaasistenciarf.data.model.*
import com.example.sistemaasistenciarf.util.Converters

@Database(
    entities = [
        UsuarioAdmin::class,
        Usuario::class,
        Asistencia::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun adminDao(): AdminDao
    abstract fun usuarioDao(): UsuarioDao
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
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instancia = it }
            }
        }
    }
}
