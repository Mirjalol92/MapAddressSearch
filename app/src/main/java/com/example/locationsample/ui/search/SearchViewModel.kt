package com.example.locationsample.ui.search

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationsample.data.repository.MapGeocoderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mapGeocoderRepository: MapGeocoderRepository
): ViewModel(){

    private var searchKeyword: String = ""

    private val _addressList = MutableStateFlow<List<Address>>(emptyList())
    val addressList: StateFlow<List<Address>> = _addressList

    fun setSearchKeyword(keyword: String){
        searchKeyword = keyword
    }
    fun performSearch(){
        if(searchKeyword.isNotEmpty()){
            viewModelScope.launch {
                mapGeocoderRepository.getSearchAddressList(searchKeyword).collectLatest {
                    _addressList.value = it
                }
            }
        }
    }
}