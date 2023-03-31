package com.watasolutions.week8_permission.screens.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watasolutions.week8_permission.services.location.LocationClient
import kotlinx.coroutines.launch

class HomeViewModel(private val location: LocationClient) : ViewModel() {
    private var _locationEvent : MutableLiveData<Location> = MutableLiveData<Location>()
    val locationEvent: LiveData<Location>
    get() = _locationEvent

    fun getLocation() {
        viewModelScope.launch {
            val location = location.getLastLocation()
            location?.let {
                _locationEvent.postValue(it)
            }
        }
    }
}