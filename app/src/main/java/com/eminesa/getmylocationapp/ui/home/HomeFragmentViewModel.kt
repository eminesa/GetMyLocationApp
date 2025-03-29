package com.eminesa.getmylocationapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eminesa.getmylocationapp.common.LocationRepository
import com.eminesa.getmylocationapp.di.MarkerManager
import com.eminesa.getmylocationapp.model.AddressModel
import com.eminesa.getmylocationapp.service.LocationService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MarkerViewModel @Inject constructor(
    val locationRepository: LocationRepository,
    val markerManager: MarkerManager,
) : ViewModel() {

    var addressList: MutableSet<AddressModel> = mutableSetOf()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    init {
        collectLocationUpdates(locationRepository.locationFlow)
    }

    private fun collectLocationUpdates(flow: SharedFlow<LatLng?>) {
        viewModelScope.launch {
            flow.collect { newLocation ->
                _currentLocation.emit(newLocation)
            }
        }
    }

}
