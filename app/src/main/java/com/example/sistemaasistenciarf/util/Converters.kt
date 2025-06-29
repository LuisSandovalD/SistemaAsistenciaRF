package com.example.sistemaasistenciarf.util

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromFloatArray(array: FloatArray): String {
        return array.joinToString(",")
    }

    @TypeConverter
    fun toFloatArray(data: String): FloatArray {
        return if (data.isEmpty()) {
            floatArrayOf()
        } else {
            data.split(",").map { it.toFloat() }.toFloatArray()
        }
    }
}