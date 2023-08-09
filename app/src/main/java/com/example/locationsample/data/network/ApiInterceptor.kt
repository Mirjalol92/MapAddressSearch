package com.example.locationsample.data.network

import android.content.Context
import com.example.locationsample.R
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiInterceptor @Inject constructor(val context: Context): Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()
        val requestUrl = originalRequest.url.toString()

        when {
            requestUrl.contains("https://dapi.kakao.com") ->{
                builder.addHeader(
                    "Authorization",
                    "KakaoAK ${context.getString(R.string.kakao_rest_api_key)}"
                )
                    .addHeader("Accept-Charset", "UTF-8")
                    .addHeader("charset", "UTF-8r")
            }
        }

        val newRequest = builder.build()
        return chain.proceed(newRequest)

    }
}