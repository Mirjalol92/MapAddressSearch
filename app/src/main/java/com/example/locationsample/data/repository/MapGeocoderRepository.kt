package com.example.locationsample.data.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.locationsample.data.model.Document
import com.example.locationsample.data.network.api.KakaoAddressApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class MapGeocoderRepository @Inject constructor(
    val geocoder: Geocoder
){
    val TAG = "MapGeocoder"

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAddressFromLocation(location: Location): Flow<Address?> = callbackFlow{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1){
                trySend(it.firstOrNull())
            }
        }else{
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            trySend(addressList?.firstOrNull())
        }

        awaitClose {
            this.cancel()
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getSearchAddressList(keyword: String) = callbackFlow<List<Address>>{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(keyword, 10){
                trySend(it)
            }
        }else{
            trySend(geocoder.getFromLocationName(keyword, 10) ?: emptyList())
        }
        awaitClose {
            this.cancel()
        }
    }
    suspend fun getAddressFromLocation(location: Location, result:(Address?)->Unit){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1){
                result(it.firstOrNull())
            }
        }else{
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            result(addressList?.firstOrNull())
        }
    }

    suspend fun getLocationFromAddress(addressName: String) = flow<Location?> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(addressName, 1) {
            }
        }else{
            val locationList = geocoder.getFromLocationName(addressName, 1) ?: return@flow
            val result = Location(LocationManager.GPS_PROVIDER)
            result.latitude = locationList.first().latitude
            result.longitude = locationList.first().longitude
            emit(result)
        }
    }
}