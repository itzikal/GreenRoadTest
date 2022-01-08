package com.example.greenroadtest.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.greenroadtest.model.GeoFenceModel
import java.util.*

@Database(entities = [GeoFenceModel::class], version = 2, exportSchema = false)
@TypeConverters(value = [DatabaseDateTypeConverter::class])
abstract class GeoFenceDB : RoomDatabase() {
    abstract fun geoFanceDao() : GeoFenceDao
}

class DatabaseDateTypeConverter {
    @TypeConverter
    fun DateToLong(data: Date?): Long? {
        return data?.time
    }

    @TypeConverter
    fun LongToDate(data: Long?): Date? {
        return if (data == null) {
            null
        } else {
            Date(data)
        }
    }
}