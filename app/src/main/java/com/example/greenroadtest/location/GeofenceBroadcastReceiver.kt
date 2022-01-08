package com.example.greenroadtest.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.greenroadtest.services.GeofenceTransitionsJobIntentService
import com.google.android.gms.location.GeofencingEvent
import org.koin.core.component.KoinApiExtension


private const val TAG = "GeofenceBrodcastReceive"
@KoinApiExtension
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        // Enqueues a JobIntentService passing the context and intent as parameters
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        Log.d(TAG, "onReceive(), geofencingEvent.")
        val geofenceTransition = geofencingEvent.geofenceTransition
        Log.d(TAG, "geofenceTransition: $geofenceTransition")
        GeofenceTransitionsJobIntentService.enqueueWork(context, intent)

    }
}