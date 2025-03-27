package com.eminesa.getmylocationapp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eminesa.beinconnectclone.ui.base.BaseFragment
import com.eminesa.getmylocationapp.R
import com.eminesa.getmylocationapp.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.tasks.Task

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun FragmentHomeBinding.bindScreen() {

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync((this@HomeFragment))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // İzin kontrolü ve alma
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
        val locationTask: Task<Location> = fusedLocationClient.lastLocation
        locationTask.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Konum başarıyla alındı
                val latitude = location.latitude
                val longitude = location.longitude

                Toast.makeText(requireContext(), latitude.toString().plus(longitude.toString()), Toast.LENGTH_SHORT).show()

            } else {
                // Konum verisi yoksa kullanıcı GPS kapalıysa burada bir uyarı gösterebiliriz.
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        Toast.makeText(requireContext(), p0.toString(), Toast.LENGTH_SHORT).show()
    }
}
