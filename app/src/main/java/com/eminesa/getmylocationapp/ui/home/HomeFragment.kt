package com.eminesa.getmylocationapp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.eminesa.beinconnectclone.ui.base.BaseFragment
import com.eminesa.getmylocationapp.R
import com.eminesa.getmylocationapp.databinding.FragmentHomeBinding
import com.eminesa.getmylocationapp.extention.getNameOfLocation
import com.eminesa.getmylocationapp.model.AddressEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    OnMapReadyCallback {

    private val markerViewModel: MarkerViewModel by viewModels()
    private lateinit var googleMap: GoogleMap

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun FragmentHomeBinding.bindScreen() {

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync((this@HomeFragment))

        markerViewModel.loadAddresses()
        listenAvailableLocation()
        listenLocation()

        clearTrackClick()
        startStopFollowClick()
        checkLocationPermission()
    }

    private fun listenAvailableLocation() {
        lifecycleScope.launch {
            markerViewModel.addresses.collect { addresses ->

                for (address in addresses) {
                    address.address?.let {

                        markerViewModel.markerManager.addMarker(
                            LatLng(
                                address.latitude,
                                address.longitude
                            ), it
                        )
                    }
                }
            }
        }
    }

    private fun FragmentHomeBinding.clearTrackClick() {
        clearTrackButton.setOnClickListener {
            markerViewModel.apply {
                markerManager.removeAllMarkers()
                markerViewModel.deleteAddresses()
                locationRepository.stopService(requireContext())
            }
        }
    }

    private fun FragmentHomeBinding.startStopFollowClick() {
        stopFollowButton.setOnClickListener {  // Bunu sormak mantıklı olabilir ama şuan zamanım yok
            if (stopFollowButton.text == getString(R.string.stop_follow)) {
                markerViewModel.apply {
                    markerManager.removeAllMarkers()
                    markerViewModel.deleteAddresses()
                    stopFollowButton.text = getString(R.string.start_follow)
                    locationRepository.stopService(requireContext())
                }
            } else {
                stopFollowButton.text = getString(R.string.stop_follow)
                markerViewModel.locationRepository.startService(requireContext())
            }
        }
    }

    private fun listenLocation() {

        lifecycleScope.launch {
            markerViewModel.currentLocation.collect { latLng ->
                latLng?.let {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    val nameOfLocation = it.getNameOfLocation(requireContext())

                    markerViewModel.markerManager.addMarker(it, nameOfLocation)
                    val newAddress = AddressEntity(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        address = nameOfLocation
                    )
                    markerViewModel.addAddress(newAddress)

                }
            }
        }

    }

    private fun checkLocationPermission() {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (API 34)
            permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        if (permissionsToRequest.all {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            markerViewModel.locationRepository.startService(requireContext())

        } else {
            requestPermissions.launch(permissionsToRequest.toTypedArray())
        }
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val foregroundServiceGranted =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    permissions[Manifest.permission.FOREGROUND_SERVICE_LOCATION] ?: false
                } else {
                    true // Daha düşük Android sürümlerinde bu izin zaten otomatik verilir.
                }

            if (!fineLocationGranted || !foregroundServiceGranted) {
                markerViewModel.locationRepository.startService(requireContext())
            } else {

                Toast.makeText(requireContext(), "Konum izni gerekli!", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        markerViewModel.markerManager.setMap(map)
    }
}
