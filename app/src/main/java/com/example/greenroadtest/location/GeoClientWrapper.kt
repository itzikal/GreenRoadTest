package com.example.greenroadtest.location

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.greenroadtest.GEOFENCE_RADIUS_IN_METERS
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.koin.core.component.KoinApiExtension

private const val GEOFENCE_EXPIRATION_IN_HOURS: Long = 1
private const val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long =
    GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000
private const val TAG = "GeoClientWrapper"
@KoinApiExtension
class GeoClientWrapper(private val context: Context) {
    private val geofencingClient = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    @SuppressWarnings("MissingPermission")
    fun start(location : LatLng){
       if(!checkPermissions()){
           return
       }
        geofencingClient.removeGeofences(geofencePendingIntent).addOnCompleteListener{
            addTheFences(location)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun addTheFences(location: LatLng) {
        geofencingClient.addGeofences(getGeofencingRequest(location), geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d(TAG, "addGeofences(), success ")
            }
            addOnFailureListener {
                Log.d(TAG, "addGeofences(), Failed ")

            }
        }
    }


    fun stop(){
        removeGeofences()
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private fun removeGeofences() {
        if (!checkPermissions()) {
            return
        }

    }

    private fun getGeofencingRequest(location : LatLng): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
            addGeofences(createGeoFancingList(location))
        }.build()
    }

    private fun createGeoFancingList(location : LatLng): List<Geofence> {
        Log.d(TAG, "createGeoFancingList(), on location: $location")
        return listOf<Geofence>(

            Geofence.Builder() // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("geoFanceRequestId")

                // Set the circular region of this geofence.
                .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS_IN_METERS.toFloat())


                //                    // Set the expiration duration of the geofence. This geofence gets automatically
                //                    // removed after this period of time.
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build())


    }

}