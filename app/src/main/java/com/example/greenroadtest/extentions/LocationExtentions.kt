package com.example.greenroadtest.extentions

import android.location.Location
import com.google.android.gms.maps.model.LatLng

fun Location.asLatLng() = LatLng(this.latitude, this.longitude)
