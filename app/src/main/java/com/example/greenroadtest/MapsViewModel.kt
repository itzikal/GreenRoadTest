package com.example.greenroadtest

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.example.greenroadtest.extentions.asLatLng
import com.example.greenroadtest.location.LocationManager
import com.example.greenroadtest.model.GeoFenceModel
import com.example.greenroadtest.model.MarkerData
import com.example.greenroadtest.model.MarkerType
import com.example.greenroadtest.model.mapToMarkerData
import com.example.greenroadtest.storage.GeoFanceRepository
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension

private const val TAG = "MapsViewModel"

@KoinApiExtension
class MapsViewModel(private val geoFenceRepository: GeoFanceRepository, private val locationManager: LocationManager) :
    ViewModel() {

    private val visibleGeoFacncePoints = mutableListOf<GeoFenceModel>()
    private val visibleGeoFacncePointsLive = MutableLiveData<List<GeoFenceModel>>(null)
    private var lastKnonwnLocation = MutableLiveData<Location>()
    private val lastGeoFance = geoFenceRepository.getLastGeoFance()
    val showMapPosition = MutableLiveData<LatLng>()

    val geoFencesList = geoFenceRepository.getGeoFencesList()
        .map {
        it.map { model ->
            GeoFencePointViewModel(model, visibleGeoFacncePoints.contains(model))
        }
    }

    val markerList = Transformations.map(TripleTrigger(visibleGeoFacncePointsLive, lastKnonwnLocation, lastGeoFance)) {
        val list = mutableListOf<MarkerData>()
        it.first?.forEach { item -> list.add(item.mapToMarkerData(MarkerType.Geopoint)) }
        it.second?.let { lastKnonwnLocation ->
            list.add(MarkerData(lastKnonwnLocation.latitude, lastKnonwnLocation.longitude, id = "Me", MarkerType.Me))
        }
        it.third?.let { lastGeoFance -> list.add(lastGeoFance.mapToMarkerData(MarkerType.ActiveGeopoint)) }
        list
    }


    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
                viewModelScope.launch {
                    Log.d(TAG, "onLocationResult(), last location $location")
                    geoFenceRepository.activateGeoFance(location.asLatLng())
                    lastKnonwnLocation.value = location
                  //  showMapPosition.value = location.asLatLng()
                }
            }
        }
    }

    fun startLocationUpdates() {
        locationManager.start(locationCallback)
    }

    fun stopLocationUpdates() {
        locationManager.stop()
    }

    fun onGeoPointSelected(pointViewModel: GeoFencePointViewModel) {
        val item = pointViewModel.point
        pointViewModel.isSelected = !pointViewModel.isSelected
        if (pointViewModel.isSelected) {
            visibleGeoFacncePoints.add(item)
            showMapPosition.postValue(item.asLatLng())
        } else {
            visibleGeoFacncePoints.remove(item)
        }
        visibleGeoFacncePointsLive.postValue(visibleGeoFacncePoints)
    }
}

class TripleTrigger<A, B, C>(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>) : MediatorLiveData<Triple<A?, B?, C?>>() {
    init {
        addSource(a) { value = Triple(it, value?.second, value?.third) }
        addSource(b) { value = Triple(value?.first, it, value?.third) }
        addSource(c) { value = Triple(value?.first, value?.second, it) }
    }
}