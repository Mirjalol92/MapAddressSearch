package com.example.locationsample.data.network.api

import com.example.locationsample.base.BaseAppConsts
import com.example.locationsample.data.model.AddressResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface KakaoAddressApi {
    @GET
    suspend fun fetchGetSearchAddress(
        @Url url: String = BaseAppConsts.REST_API_ADDRESS_URL,
        @Query("query") address: String,
        @Query("page") page: Int = 1,
        @Query("AddressSize") AddressSize: Int = 10) : AddressResponse                      // Address Search

    @GET
    suspend fun fetchGetSearchLocal(
        @Url url: String = BaseAppConsts.REST_API_LOCAL_URL,
        @Query("query") keyword: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10) : AddressResponse                                    // Local Search

    @GET
    suspend fun fetchCoordToAddress(
        @Url url: String = BaseAppConsts.REST_API_COORD_TO_ADDRESS_URL,
        @Query("x") x: Double,
        @Query("y") y: Double,
        @Query("input_coord") input_coord: String = "WGS84") : AddressResponse              // Address Search by Coordinates
    
}