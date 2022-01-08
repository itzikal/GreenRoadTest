package com.example.greenroadtest.storage

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.greenroadtest.location.GeoClientWrapper
import com.example.greenroadtest.model.GeoFenceModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import java.util.*

private const val TAG = "GeoFanceRepository"
@KoinApiExtension
class GeoFanceRepository(private val geoFenceDao: GeoFenceDao, private val geoClient : GeoClientWrapper) {

    fun getGeoFencesList(): LiveData<List<GeoFenceModel>> {
        return geoFenceDao.getAllDataLive()
    }
    fun getLastGeoFance(): LiveData<GeoFenceModel> {
        return geoFenceDao.getLastGeoFenceLive()
    }

    private suspend fun addGeoFancePoint(location: LatLng) = withContext(Dispatchers.IO){
      val model = GeoFenceModel(lat = location.latitude, lng = location.longitude, Date())
        Log.d(TAG, "new geo pint: $model")
        geoFenceDao.insert(model)

    }

    suspend fun activateGeoFance(location: LatLng) = withContext(Dispatchers.IO) {
        if(geoFenceDao.getLastGeoFence() == null){
            activatNewGeoFance(location)
        }
    }
    suspend fun activatNewGeoFance(location: LatLng) = withContext(Dispatchers.IO){
        addGeoFancePoint(location)
        geoClient.start(location)
    }
}