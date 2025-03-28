package com.eminesa.getmylocationapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eminesa.getmylocationapp.di.MarkerManager
import com.eminesa.getmylocationapp.model.AddressModel
import com.eminesa.getmylocationapp.service.LocationService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MarkerViewModel @Inject constructor(
    val locationService: LocationService,
    val markerManager: MarkerManager,
) : ViewModel() {

    var addressList: MutableSet<AddressModel> = mutableSetOf()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    fun collectLocationUpdates() {
        viewModelScope.launch {
            locationService.locationFlow.collectLatest { newLocation ->
                // Eğer yeni gelen konum eski konumla aynıysa güncelleme yapma
                if (_currentLocation.value != newLocation) {
                    _currentLocation.emit(newLocation)
                }
            }
        }
    }

}
