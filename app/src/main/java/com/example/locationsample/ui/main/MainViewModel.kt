package com.example.locationsample.ui.main

import android.location.Address
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.example.locationsample.data.model.Document
import com.example.locationsample.data.repository.KakaoAddressApiRepository
import com.example.locationsample.data.repository.MapGeocoderRepository
import com.example.locationsample.ui.map.map_command.Commands
import com.example.locationsample.ui.map.map_command.MapCommands
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mapCommand: MapCommands
) : ViewModel() {

    val mapPointingAddress = MutableStateFlow<Address?>(null)
    init {
        viewModelScope.launch {
            mapCommand.sharedFlow.collect {
                when(it){
                    is Commands.MapAddress->{
                        mapPointingAddress.value = it.address
                    }
                }
            }
        }
    }

    fun locateMe(){
        viewModelScope.launch{
            mapCommand.locateMe()
        }
    }
}