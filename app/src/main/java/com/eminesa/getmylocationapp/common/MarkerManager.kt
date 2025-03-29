package com.eminesa.getmylocationapp.common

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject

class MarkerManager @Inject constructor() {

    private var googleMap: GoogleMap? = null
    private val markers = mutableListOf<Marker>()

    fun setMap(map: GoogleMap) {
        this.googleMap = map
    }

    fun addMarker(location: LatLng, title: String): Marker? {
        googleMap?.let { map ->
            val marker = map.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(title)
            )
            marker?.let { markers.add(it) }
            return marker
        }
        return null
    }

    fun removeAllMarkers() {
        markers.forEach { it.remove() }
        markers.clear()
    }
}
