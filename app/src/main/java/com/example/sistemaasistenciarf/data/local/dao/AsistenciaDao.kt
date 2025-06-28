package com.example.sistemaasistenciarf.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sistemaasistenciarf.data.model.Asistencia

@Dao
interface AsistenciaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(asistencia: Asistencia)

    @Query("SELECT * FROM asistencias ORDER BY id DESC")
    fun obtenerAsistencias(): LiveData<List<Asistencia>>
}
