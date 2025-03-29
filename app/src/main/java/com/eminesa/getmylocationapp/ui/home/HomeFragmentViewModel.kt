package com.eminesa.getmylocationapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eminesa.getmylocationapp.di.MarkerManager
import com.eminesa.getmylocationapp.model.AddressEntity
import com.eminesa.getmylocationapp.repository.AddressRepository
import com.eminesa.getmylocationapp.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MarkerViewModel @Inject constructor(
    val locationRepository: LocationRepository,
    val markerManager: MarkerManager,
    private val repository: AddressRepository
) : ViewModel() {


    private val _addresses = MutableStateFlow<List<AddressEntity>>(emptyList())
    val addresses: StateFlow<List<AddressEntity>> get() = _addresses.asStateFlow()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    init {
        collectLocationUpdates()
        loadAddresses()
    }

    private fun collectLocationUpdates() {
        viewModelScope.launch {
            locationRepository.locationFlow.collect { newLocation ->
                _currentLocation.emit(newLocation)
            }
        }
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            repository.getAddresses().collect { addressList ->
                _addresses.value = addressList
            }
        }
    }

    fun addAddress(address: AddressEntity) {
        viewModelScope.launch {
            repository.addAddress(address)
        }
    }

    fun deleteAddresses() {
        viewModelScope.launch {
            repository.deleteAddresses()
        }
    }

}