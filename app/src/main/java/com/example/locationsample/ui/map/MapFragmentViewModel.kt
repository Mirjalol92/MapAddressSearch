package com.example.locationsample.ui.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationsample.data.repository.KakaoAddressApiRepository
import com.example.locationsample.data.repository.MapGeocoderRepository
import com.example.locationsample.ui.map.map_command.Commands
import com.example.locationsample.ui.map.map_command.MapCommands
import com.example.locationsample.utils.location.LocationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.w3c.dom.Document
import javax.inject.Inject

@HiltViewModel
class MapFragmentViewModel @Inject constructor(
    private val mapGeocoder: MapGeocoderRepository,
    private val mapCommand: MapCommands,
): ViewModel() {
    var locationUtil: LocationUtil? = null

    private val _mapState: MutableStateFlow<MapUiState> = MutableStateFlow(MapUiState.Loading(false))
    val mapState: StateFlow<MapUiState> = _mapState
    init {
        viewModelScope.launch{
            mapCommand.sharedFlow.collect {
                when(it){
                    is Commands.LocateMe->{
                        locationUtil?.getLocationOnce(true)
                    }
                    is Commands.MapLocation->{
                        fetchCoordToAddress(it.location)
                    }
                }
            }
        }
    }
    fun startCollectingLocation(){
        viewModelScope.launch {
            locationUtil?.locationState?.collect {
                when(it){
                    is LocationUtil.LocationState.Result->{
                        fetchCoordToAddress(it.location)
                    }
                    is LocationUtil.LocationState.Denied->{
                        _mapState.value = MapUiState.LocationAccessDenied
                    }
                    is LocationUtil.LocationState.Loading->{
                        _mapState.value = MapUiState.Loading(it.isLoading)
                    }
                    is LocationUtil.LocationState.GpsNotEnabled->{
                        _mapState.value = MapUiState.GpsNotEnabled
                    }
                }
            }
        }
    }
    fun fetchCoordToAddress(location: Location) {
        viewModelScope.launch {
            mapGeocoder.getAddressFromLocation(location).collect {
                it?.let { address ->
                    _mapState.value = MapUiState.Address(address)
                    mapCommand.setAddress(address)
                }
            }
        }
    }
}
sealed class MapUiState{
    data class Address(val address: android.location.Address): MapUiState()
    data class Loading(val isLoading: Boolean): MapUiState()
    object GpsNotEnabled: MapUiState()
    object LocationAccessDenied: MapUiState()
}