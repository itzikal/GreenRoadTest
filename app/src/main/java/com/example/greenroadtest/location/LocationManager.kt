package com.example.greenroadtest.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

private const val TAG = "LocationManager"
class LocationManager(private val context: Context) {
    private var fusedLocationProvider: FusedLocationProviderClient? = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 20

    }

    private var locationCallback: LocationCallback? = null
    private var lastLocation : Location? = null
    private var innerLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
                Log.d(TAG, "onLocationResult(), last location $location")
                if(lastLocation != location) {
                    lastLocation = location
                    locationCallback?.onLocationResult(locationResult)
                }
            }
        }
    }

    fun start(locationCallback: LocationCallback) {
        this.locationCallback = locationCallback
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest, innerLocationCallback, Looper.getMainLooper()
            )
        }
    }
    fun stop(){
        this.locationCallback = null
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.removeLocationUpdates(innerLocationCallback)
        }
    }

}