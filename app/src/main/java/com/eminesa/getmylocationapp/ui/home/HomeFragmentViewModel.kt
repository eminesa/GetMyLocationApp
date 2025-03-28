package com.eminesa.getmylocationapp.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eminesa.getmylocationapp.di.MarkerManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class MarkerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val markerManager: MarkerManager
) : ViewModel() {

    // LiveData, konum verisini tutuyor ve UI'yi güncelliyor.
    private val _locationData = MutableLiveData<LatLng>()
    val locationData: LiveData<LatLng> get() = _locationData

    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 2000  // 2 saniye aralık
    ).apply {
        setMinUpdateDistanceMeters(10f).setMaxUpdateDelayMillis(500) // 10 metre değişim
     }.build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                _locationData.postValue(latLng) // LiveData'yı güncelle
            }
        }
    }

        @SuppressLint("MissingPermission")
        fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
