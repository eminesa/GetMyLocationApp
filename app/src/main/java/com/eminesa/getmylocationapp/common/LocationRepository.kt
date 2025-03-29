package com.eminesa.getmylocationapp.common

import android.content.Context
import android.content.Intent
import com.eminesa.getmylocationapp.di.LocationChannel
import com.eminesa.getmylocationapp.service.LocationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationChannel: LocationChannel
) {
    val locationFlow: StateFlow<LatLng?> = locationChannel.locationFlow

    fun startService(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        context.startForegroundService(intent)
    }

    fun stopService(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        context.stopService(intent)
    }
}

