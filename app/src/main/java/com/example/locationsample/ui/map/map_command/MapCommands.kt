package com.example.locationsample.ui.map.map_command

import android.location.Address
import android.location.Location
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapCommands @Inject constructor(){

    private val _sharedFlow: MutableSharedFlow<Commands> = MutableSharedFlow()
    val sharedFlow = _sharedFlow.asSharedFlow()
    suspend fun emitLocation(location: Location){
        _sharedFlow.emit(Commands.MapLocation(location))
    }
    suspend fun locateMe(){
        _sharedFlow.emit(Commands.LocateMe)
    }
    suspend fun setAddress(address: Address){
        _sharedFlow.emit(Commands.MapAddress(address))
    }
}

sealed class Commands{
    data class MapAddress(val address: Address): Commands()
    data class MapLocation(val location: Location): Commands()
    object LocateMe: Commands()
}