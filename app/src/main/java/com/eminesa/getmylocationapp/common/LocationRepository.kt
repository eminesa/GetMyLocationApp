package com.eminesa.getmylocationapp.common

import android.content.Context
import com.eminesa.getmylocationapp.service.LocationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationService: LocationService
) {
    val locationFlow: SharedFlow<LatLng?> = locationService.locationFlow

    fun startService(context: Context) = locationService.startService(context)

    fun stopService(context: Context) = locationService.stopService(context)

}