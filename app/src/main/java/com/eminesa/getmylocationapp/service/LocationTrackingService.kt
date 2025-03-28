package com.eminesa.getmylocationapp.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.eminesa.getmylocationapp.R
import com.eminesa.getmylocationapp.di.MarkerManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

