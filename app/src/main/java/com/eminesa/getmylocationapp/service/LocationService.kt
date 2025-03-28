package com.eminesa.getmylocationapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.eminesa.getmylocationapp.R
import com.eminesa.getmylocationapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationService @Inject constructor() : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var lastLocation: Location? = null
    private val minDistanceThreshold = 10f // 100 metre

    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "location_channel"

    // Flow ile konum verisini paylaşmak için SharedFlow kullanıyoruz
    private val _locationFlow = MutableSharedFlow<LatLng>(replay = 1)
    val locationFlow: SharedFlow<LatLng> = _locationFlow.asSharedFlow()


    fun startService(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        context.startForegroundService(intent)
    }

    fun stopService(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        context.stopService(intent)
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    processNewLocation(location)
                }
            }
        }
    }

    private fun processNewLocation(location: Location) {
        val previousLocation = lastLocation

        if (previousLocation == null ||
            calculateDistance(previousLocation, location) >= minDistanceThreshold
        ) {

            lastLocation = location
            serviceScope.launch {
                val latLng = LatLng(location.latitude, location.longitude)
                _locationFlow.emit(latLng)
            }
        }
    }

    private fun calculateDistance(previous: Location, current: Location): Float {
        return previous.distanceTo(current)
    }

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000)
                .setMaxUpdateDelayMillis(10000)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.location_channel_name)
        val descriptionText = getString(R.string.location_channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(getString(R.string.location_notification_title))
        .setContentText(getString(R.string.location_notification_text))
        .setSmallIcon(R.drawable.ic_location)
        .setContentIntent(createPendingIntent())
        .build()

    private fun createPendingIntent(): PendingIntent { // burası ne yapacak??
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}