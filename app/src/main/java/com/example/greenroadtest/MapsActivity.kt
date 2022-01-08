package com.example.greenroadtest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.greenroadtest.databinding.ActivityMapsBinding
import com.example.greenroadtest.extentions.asLatLng
import com.example.greenroadtest.model.GeoFenceModel
import com.example.greenroadtest.model.MarkerType
import com.example.greenroadtest.ui.OnItemClicked
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

const val GEOFENCE_RADIUS_IN_METERS = 300.0
private const val TAG = "MapsActivity"

@KoinApiExtension
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private var mMap: GoogleMap? = null
    private val viewModel : MapsViewModel by inject()

    private val geoFencePointsListAdapter = GeoFencePointsListAdapter(object : OnItemClicked<GeoFencePointViewModel> {
        override fun onItemClicked(item: GeoFencePointViewModel) {
            viewModel.onGeoPointSelected(item)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        checkLocationPermission()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        list.apply {
            layoutManager = LinearLayoutManager(this@MapsActivity, LinearLayoutManager.VERTICAL, false)
            adapter = geoFencePointsListAdapter
        }
        initObservers()
    }

    private fun initObservers()= with(viewModel) {
        geoFencesList.observe(this@MapsActivity){
            geoFencePointsListAdapter.submitList(it)//.map { GeoFencePointViewModel(it) })
        }

        markerList.observe(this@MapsActivity){
            mMap?.clear()
            it.forEach{ data->
                val markerColor = when(data.type){
                    MarkerType.Me -> BitmapDescriptorFactory.HUE_BLUE
                    MarkerType.Geopoint -> BitmapDescriptorFactory.HUE_RED
                    MarkerType.ActiveGeopoint -> BitmapDescriptorFactory.HUE_GREEN
                }
                mMap?.addMarker(MarkerOptions().position(data.asLatLng()).title("${data.id}")
                                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor)))
                if(data.type == MarkerType.ActiveGeopoint){
                    mMap?.addCircle(CircleOptions().center(data.asLatLng())
                                        .radius(GEOFENCE_RADIUS_IN_METERS).strokeColor(R.color.green))
                }
            }
        }

        showMapPosition.observe(this@MapsActivity){
            Log.d(TAG, "showMapPosition(), $it")
            mMap?.moveCamera(CameraUpdateFactory.newLatLng(it))
            mMap?.moveCamera(CameraUpdateFactory.zoomTo(15f))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startLocationUpdates()

    }

    override fun onPause() {
        super.onPause()
        viewModel.stopLocationUpdates()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) { // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this).setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ -> //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }.create().show()
            } else { // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ), MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ), MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> { // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
//                        fusedLocationProvider?.requestLocationUpdates(
//                            locationRequest, locationCallback, Looper.getMainLooper()
//                        )
                    viewModel.startLocationUpdates()
                        // Now check background location
                        checkBackgroundLocation()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> { // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        viewModel.startLocationUpdates()

                        Toast.makeText(
                            this, "Granted Background Location Permission", Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}