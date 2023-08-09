package com.example.locationsample.di

import android.content.Context
import android.location.Geocoder
import com.example.locationsample.base.BaseAppConsts
import com.example.locationsample.data.network.ApiInterceptor
import com.example.locationsample.data.network.api.KakaoAddressApi
import com.example.locationsample.utils.location.LocationUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule{
    @Provides
    @Singleton
    fun provideApiInterceptor() = ApiInterceptor()

    @Provides
    @Singleton
    fun provideOkkHttpInterceptor(interceptor: ApiInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .writeTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .addNetworkInterceptor(interceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BaseAppConsts.REST_API_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideKakaoAddressApi(retrofit: Retrofit): KakaoAddressApi =
        retrofit.create(KakaoAddressApi::class.java)

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context, Locale.KOREA)
    }


}