package com.eminesa.getmylocationapp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.eminesa.beinconnectclone.ui.base.BaseFragment
import com.eminesa.getmylocationapp.R
import com.eminesa.getmylocationapp.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), OnMapReadyCallback {

    private val markerViewModel: MarkerViewModel by viewModels()
    private lateinit var googleMap: GoogleMap

    override fun FragmentHomeBinding.bindScreen() {

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync((this@HomeFragment))
        // Konum verisini dinlemek

       /* markerViewModel.locationData.observe(viewLifecycleOwner, Observer { latLng ->

            val address = getNameOfLocation(latLng)
            markerViewModel.markerManager.addMarker(latLng, address)
        }) */

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        // Konum izni kontrolü
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            // İzin verilmişse, konum al
            getLastKnownLocation()
        } else {
            // İzin verilmemişse, izin isteme
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // İzin isteme işlemi
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // İzin verilmişse, konum al
                getLastKnownLocation()
            } else {
                // İzin verilmemişse kullanıcıyı bilgilendir // alert
            }
        }

    private fun getLastKnownLocation() {
        // Son bilinen konumu al
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        lifecycleScope.launch {

            val locationTask: Task<Location> = markerViewModel.fusedLocationClient.lastLocation

            locationTask.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)

                    val address = getNameOfLocation(latLng)

                    markerViewModel.markerManager.addMarker(latLng, address)

                    // Haritayı o konumda zoom yapma
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            15f
                        )  // Burada 15f, zoom seviyesini belirtir
                    )


                } else {
                    Toast.makeText(requireContext(), "Bişeyler ters gitti", Toast.LENGTH_SHORT)
                        .show()

                    // Konum verisi yoksa kullanıcı GPS kapalıysa burada bir uyarı gösterebiliriz.
                }
            }

        }

    }

    private fun getNameOfLocation(location: LatLng): String {
        var locationInfo = "Bu konum için adres bulunamadı "
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val addresses: List<Address>? =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val addressLine = address.getAddressLine(0) // Adresin tamamı
            val city = address.locality // Şehir
            val country = address.countryName // Ülke

            // Konum bilgilerini kullanıcıya gösterebiliriz
            locationInfo = "Location: $addressLine, $city, $country"

        }
        return locationInfo
    }

    override fun onMapReady(map: GoogleMap) {

        markerViewModel.markerManager.setMap(map)
        googleMap = map
    }

    override fun onResume() {
        super.onResume()
        // Konum güncellemelerini başlat
        markerViewModel.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        // Konum güncellemelerini durdur
        markerViewModel.stopLocationUpdates()
    }
}
