package com.example.greenroadtest.model

import com.google.android.gms.maps.model.LatLng

data class MarkerData (
    val lat : Double,
    val lng: Double,
    val id :String,
    val type: MarkerType
) {

    fun asLatLng() =
        LatLng(lat, lng)
}

enum class MarkerType{
    Me,
    Geopoint,
    ActiveGeopoint
}
fun GeoFenceModel.mapToMarkerData(type : MarkerType) = MarkerData(lat = lat, lng = lng, id = "$id", type = type)