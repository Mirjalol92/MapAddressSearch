package com.example.locationsample.locations

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*


class LocationLiveData(private val context: Context, val onLocationSettingException: (ApiException) -> Unit, val onPermissionException: ()->Unit ): LiveData<Location>() {

    private val LOCATION_INTERVAL                                                                   = 1000 * 10.toLong()//10sec
    private val LOCATION_FASTEST_INTERVAL                                                           = 1000 * 5.toLong()//5sec
    private val LOCATION_DISPLACEMENT                                                               = 1.0f // 1 meters
    private val LOCATION_PRIORITY                                                                   = LocationRequest.PRIORITY_HIGH_ACCURACY

    val TAG = "LocationLiveData"

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create()
    }

    private val fusedLocation: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val googleApiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(connectionCallBacks)
            .addOnConnectionFailedListener(connectionCallFail)
            .build()
    }

    private val connectionCallFail = GoogleApiClient.OnConnectionFailedListener {
        Log.d(TAG,"OnConnectionFailedListener")
        stopLocationUpdates()
    }

    private val connectionCallBacks = object: GoogleApiClient.ConnectionCallbacks{
        override fun onConnected(p0: Bundle?) {
            Log.d(TAG,"onConnected")
            startLocationUpdates()
        }

        override fun onConnectionSuspended(p0: Int) {
            Log.d(TAG,"onConnectionSuspended")
        }

    }

    override fun onActive() {
        super.onActive()
        Log.d(TAG,"onActive")
        startLocationUpdates()
    }

    override fun onInactive() {
        super.onInactive()
        Log.d(TAG,"onInactive")
        stopLocationUpdates()
    }

    init {
        locationRequest.interval = LOCATION_INTERVAL
        locationRequest.fastestInterval = LOCATION_FASTEST_INTERVAL
        locationRequest.smallestDisplacement = LOCATION_DISPLACEMENT
        locationRequest.priority = LOCATION_PRIORITY
    }

    private val locationCallback = object: LocationCallback(){
        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
            Log.d(TAG, "onLocationAvailability")
        }

        override fun onLocationResult(locationResult: LocationResult) {
            Log.d(TAG, "onLocationResult")
            super.onLocationResult(locationResult)
            for (location in locationResult.locations){
                postValue(location)
            }
        }
    }

    fun startLocationUpdates(){
        Log.d(TAG,"startLocationUpdates")
        if(!googleApiClient.isConnected){
            googleApiClient.connect()
            return
        }

        val permissions = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!permissions){
            onPermissionException()
            return
        }

        fusedLocation.removeLocationUpdates(locationCallback)

        val locationSettingRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()

        LocationServices.getSettingsClient(context).checkLocationSettings(locationSettingRequest).addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)
                fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            }catch (e: ApiException){
                onLocationSettingException
            }
        }
        fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private fun stopLocationUpdates(){
        Log.d(TAG, "stopLocationUpdates")
        fusedLocation.removeLocationUpdates(locationCallback)
        googleApiClient.run {
            if(isConnected){
                disconnect()
            }
        }
    }

}