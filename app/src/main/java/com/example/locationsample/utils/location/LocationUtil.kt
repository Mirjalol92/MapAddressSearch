package com.example.locationsample.utils.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

open class LocationUtil @Inject constructor(private val context: Context) {

    private val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

//    val locationState = MutableLiveData<LocationState>()
    val locationState = MutableStateFlow<LocationState>(LocationState.Loading(false))

    private val TAG = "LocationUtil"

    private val fusedLocation get() = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest
        .Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5_000L)
//        .setMaxUpdates(10)
        .build()
//        .create().apply {
//        interval = 10_000L
//        fastestInterval = 5_000L
//        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        numUpdates = 2
//    }

    private val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

    private val client: SettingsClient by lazy {
        LocationServices.getSettingsClient(context)
    }

    private val task: Task<LocationSettingsResponse> by lazy {
        client.checkLocationSettings(builder.build())
    }

    private val locationCallBack = object: LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.lastLocation?.let {loc->
//                val point = MapPoint.mapPointWithGeoCoord(loc.latitude, loc.longitude)
                locationState.value = LocationState.Result(loc)
                locationState.value = LocationState.Loading(false)
                fusedLocation.removeLocationUpdates(this)
            }
        }
    }

    private val registerForActivityResult = (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions->
        var granted = true
        permissions.map {
            granted = granted && it.value
        }
        if(granted){
            delegate.granted()
        }else{
            if(shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)){
                delegate.denied()
            }else{
                delegate.deniedAtAll()
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun getLocationOnce(isRequest: Boolean = false){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationState.value = LocationState.GpsNotEnabled
            return
        }
        delegate.requested()
        task.addOnSuccessListener {
            checkAndRequestPermissions(isRequest)
        }.addOnFailureListener {
            delegate.fail()
        }.addOnCanceledListener {
            delegate.fail()
        }
    }

    private fun checkAndRequestPermissions(isRequest: Boolean = false) {
        var allGranted = true

        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).forEach { permission ->
            allGranted = allGranted && ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PermissionChecker.PERMISSION_GRANTED
        }

        if (allGranted) {
            delegate.granted()
            return
        }else if (!isRequest){
            delegate.denied()
            return
        }

        registerForActivityResult.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private val delegate = object: LocationRequestDelegate {
        override fun requested() {
            locationState.value = LocationState.Loading(true)
//            Log.d(TAG, "Location fetching is REQUESTED")
        }

        @SuppressLint("MissingPermission")
        override fun granted() {
//            Log.d(TAG, "Location fetching is GRANTED")
//            fusedLocation.lastLocation.addOnSuccessListener {
//                val point = MapPoint.mapPointWithGeoCoord(it.latitude, it.longitude)
//                locationState.value = LocationState.Result(point)
//                locationState.value = LocationState.Loading(false)
//            }
            fusedLocation.requestLocationUpdates(
                locationRequest,
                locationCallBack,
                Looper.getMainLooper()
            )
        }

        override fun denied() {
            locationState.value = LocationState.Loading(false)
            fusedLocation.removeLocationUpdates(locationCallBack)
//            Log.d(TAG, "Location fetching is DENIED")
        }

        override fun deniedAtAll() {
            locationState.value = LocationState.Loading(false)
            fusedLocation.removeLocationUpdates(locationCallBack)
//            Log.d(TAG, "Location fetching is DENIED AT ALL")
            locationState.value = LocationState.Denied
        }

        override fun fail() {
            locationState.value = LocationState.Loading(false)
            fusedLocation.removeLocationUpdates(locationCallBack)
//            Log.d(TAG, "Location fetching is CANCELED")
        }
    }

    private interface LocationRequestDelegate{
        fun requested()
        fun granted()
        fun denied()
        fun deniedAtAll()
        fun fail()
    }

    sealed class LocationState{
        object GpsNotEnabled: LocationState()
        data class Result(val location: Location): LocationState()
        object Denied: LocationState()
        data class Loading(val isLoading: Boolean): LocationState()
    }

}