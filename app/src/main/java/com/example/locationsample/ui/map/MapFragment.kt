package com.example.locationsample.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.VersionInfo
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.example.locationsample.MainActivity
import com.example.locationsample.R
import com.example.locationsample.base.BaseAppConsts
import com.example.locationsample.base.BaseFragment
import com.example.locationsample.databinding.FragmentMapBinding
import com.example.locationsample.locations.LocationLiveData
import com.example.locationsample.ui.map.map_command.Commands
import com.example.locationsample.ui.map.map_command.MapCommands
import com.example.locationsample.utils.format
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.MapFragment

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.innfinity.permissionflow.lib.Permission
import com.innfinity.permissionflow.lib.requestEachPermissions

import com.innfinity.permissionflow.lib.requestPermissions
import com.example.locationsample.utils.*
import com.example.locationsample.utils.location.LocationUtil
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment: BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate){

    private val TAG = "MapFragment"

    private lateinit var map: GoogleMap

    private val viewModel: MapFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.locationUtil = (requireActivity() as MainActivity).locationUtil
        viewModel.startCollectingLocation()
        setUpObservers()
        setUpMap()
    }
    override fun onStop() {
        viewModel.locationUtil = null
        super.onStop()
    }
    private fun setUpObservers(){
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.mapState.collectLatest {
                when(it){
                    is MapUiState.Loading ->{
                        it.isLoading
                    }
                    is MapUiState.GpsNotEnabled->{
                        showGpsNotEnabled()
                    }
                    is MapUiState.LocationAccessDenied->{
                        showLocationDenied()
                    }
                    is MapUiState.Address->{
                        if (!moveByGesture){
                            moveMapToCoor(LatLng(it.address.latitude, it.address.longitude))
                        }
                    }
                }
            }
        }
    }

    var moveByGesture = false
    private fun setUpMap(){
        val mapFragment = SupportMapFragment.newInstance()
        mapFragment.getMapAsync {
            map = it
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(BaseAppConsts.DEFUALT_START_LOCATION.y, BaseAppConsts.DEFUALT_START_LOCATION.x), 17f))
            map.setOnCameraMoveStartedListener { reason->
                moveByGesture = reason == OnCameraMoveStartedListener.REASON_GESTURE
            }
            map.setOnCameraIdleListener{
                if(moveByGesture){
                    lifecycleScope.launch {
                        map.cameraPosition.target.run{
                            val location = Location(LocationManager.GPS_PROVIDER)
                            location.latitude = latitude
                            location.longitude = longitude
                            viewModel.fetchCoordToAddress(location)
                            Log.d(TAG, "x=${location.longitude}, y=${location.latitude}")
                        }
                    }
                    moveByGesture = false
                }
            }
        }
        parentFragmentManager
            .beginTransaction()
            .add(binding.googleMapContainer.id, mapFragment)
            .commit()
    }
    private fun moveMapToCoor(latLng: LatLng){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }
    private fun showGpsNotEnabled(){
        AlertDialog.Builder(requireContext())
            .setTitle("Alert")
            .setMessage("Please allow ")
            .setPositiveButton("OK") { dialog, p1 ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.dismiss() }
            .show()
    }
    private fun showLocationDenied(){
        AlertDialog.Builder(requireContext())
            .setTitle("Alert")
            .setMessage("To use your current location information,\n Please check 'Allow only while using the app' in [Settings > Apps > ${getString(R.string.app_name)} > Permissions > Location].")
            .setPositiveButton("OK") { dialog, p1 ->
                try {
                    startActivity(Intent().apply {
                        action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", requireContext().packageName, null)
                    })
                }catch (ex: java.lang.Exception){
                    ex.printStackTrace()
                }
                dialog.dismiss() }
            .show()
    }
}