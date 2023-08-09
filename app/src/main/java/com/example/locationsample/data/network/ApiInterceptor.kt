package com.example.locationsample.data.network

import android.content.Context
import android.os.Build
import com.example.locationsample.BuildConfig
import com.example.locationsample.R
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Properties
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiInterceptor: Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()
        val requestUrl = originalRequest.url.toString()
        val kak = BuildConfig.KAKAO_REST_API_KEY

        when {
            requestUrl.contains("https://dapi.kakao.com") ->{
                builder.addHeader(
                    "Authorization",
                    "KakaoAK $kak"
                )
                    .addHeader("Accept-Charset", "UTF-8")
                    .addHeader("charset", "UTF-8r")
            }
        }

        val newRequest = builder.build()
        return chain.proceed(newRequest)

    }
}