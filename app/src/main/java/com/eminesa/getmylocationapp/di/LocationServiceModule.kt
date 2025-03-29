package com.eminesa.getmylocationapp.di

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LocationChannel @Inject constructor() {
    private val _locationFlow = MutableStateFlow<LatLng?>(null)
    val locationFlow: StateFlow<LatLng?> = _locationFlow.asStateFlow()

    suspend fun sendLocation(latLng: LatLng) {
        _locationFlow.emit(latLng)
    }
}