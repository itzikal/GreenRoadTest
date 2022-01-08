package com.example.greenroadtest.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.greenroadtest.extentions.format
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity(tableName = "Geo_Fence_Table")
data class GeoFenceModel(
    val lat : Double,
    val lng: Double,
    val time : Date
) {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0

    fun asLatLng() =
        LatLng(lat, lng)

    override fun toString(): String {
        return "Lat: $lat, Lng: $lng, time: ${time.format()}"
    }
}
