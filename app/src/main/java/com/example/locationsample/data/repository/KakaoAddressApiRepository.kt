package com.example.locationsample.data.repository

import android.location.Location
import android.util.Log
import com.example.locationsample.data.model.Document
import com.example.locationsample.data.network.api.KakaoAddressApi
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KakaoAddressApiRepository @Inject constructor(
    val kakaoAddressApi: KakaoAddressApi
){
    val TAG = "KakaoAddressApi"

    suspend fun getAddressFromKakaoApi(location: Location) = flow{
        try {
            val response = kakaoAddressApi.fetchCoordToAddress(x = location.longitude, y = location.latitude)
            val document = response.documents[0]
            document.apply {
                document.address.let {
                    address_name = it.address_name
                }

                document.road_address.let {
                    road_address_name = it.address_name
                    place_name = it.building_name
                }
                document.x = location.longitude
                document.y = location.latitude
            }
            emit(document)
        }catch (ioException: IOException){
            Log.d(TAG, ioException.localizedMessage ?: "exception")
            emit(null)
        }catch (exception: Exception){
            Log.d(TAG, exception.localizedMessage ?: "exception")
            emit(null)
        }
    }
}