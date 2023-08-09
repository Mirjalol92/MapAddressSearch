package com.example.locationsample.di

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.locationsample.utils.location.LocationUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    fun provideLocationUtil(@ActivityContext activityContext: Context) = LocationUtil(activityContext)
}