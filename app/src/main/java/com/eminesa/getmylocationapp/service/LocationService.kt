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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.eminesa.getmylocationapp.R
import com.eminesa.getmylocationapp.di.LocationChannel
import com.eminesa.getmylocationapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject lateinit var locationChannel: LocationChannel

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var lastLocation: Location? = null
    private val minDistanceThreshold = 30f // 100 metre

    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "location_channel"

    private val _locationFlow = MutableStateFlow<LatLng?>(null)
    val locationFlow: StateFlow<LatLng?> = _locationFlow.asStateFlow()

    fun startService(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        context.startForegroundService(intent)
    }

    fun stopService(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        context.stopService(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationCallback()
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

                //  processNewLocation(locationResult.locations.first())
                locationResult.locations.forEach { location ->
                    processNewLocation(location)
                }
            }
        }
    }

    private fun processNewLocation(newLocation: Location) {

        if (lastLocation == null) {
            serviceScope.launch {
                //val latLng = LatLng(41.1101, 29.0194) //Martı Ofisi.
                val latLng = LatLng(newLocation.latitude, newLocation.longitude)
                locationChannel.sendLocation(latLng)
                Log.d("LocationService", "Konum güncelleniyor: $latLng")
            }
            lastLocation = newLocation
            return
        }

        val distance = calculateDistance(lastLocation!!, newLocation)

        if (distance > minDistanceThreshold) { // 2 metreden küçük değişimleri ihmal et
            Log.d("LocationService", "Mesafe: $distance metre")

            serviceScope.launch {
                val latLng = LatLng(newLocation.latitude, newLocation.longitude)
                _locationFlow.emit(latLng)
                Log.d("LocationService", "Konum güncelleniyor: $latLng")
            }
            lastLocation = newLocation //Yalnızca anlamlı bir değişiklik varsa güncelle
        } else {
            Log.d("LocationService", "Önemsiz konum değişikliği, güncelleme yapılmadı.")
        }
    }

    private fun calculateDistance(previous: Location?, current: Location?): Float {
        if (previous == null || current == null) return 0f

        val distance = previous.distanceTo(current) // Metre cinsinden mesafe

        // Eğer cihaz sabit duruyorsa ve mesafe çok küçükse, sıfır kabul edelim
        return if (distance < minDistanceThreshold) 0f else distance
    }

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 2000  // 2 saniye aralık
            ).apply {
                setMinUpdateDistanceMeters(10f).setMaxUpdateDelayMillis(2000) // 10 metre değişim
            }.build()

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