package com.eminesa.getmylocationapp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute.Location
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.eminesa.beinconnectclone.ui.base.BaseFragment
import com.eminesa.getmylocationapp.R
import com.eminesa.getmylocationapp.databinding.FragmentHomeBinding
import com.eminesa.getmylocationapp.extention.getNameOfLocation
import com.eminesa.getmylocationapp.model.AddressModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), OnMapReadyCallback {

    private val markerViewModel: MarkerViewModel by viewModels()
    private lateinit var googleMap: GoogleMap

    override fun FragmentHomeBinding.bindScreen() {

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync((this@HomeFragment))

        markerViewModel.collectLocationUpdates()
        // Konum verisini dinlemek
        listenLocation()

        clearTrackClick()
        startStopFollowClick()
        //kullanıcı izni almak
        checkLocationPermission()
    }

    private fun FragmentHomeBinding.clearTrackClick() {
        clearTrackButton.setOnClickListener {
            markerViewModel.apply {
                markerManager.removeAllMarkers()
                addressList.clear()
            }
        }
    }

    private fun FragmentHomeBinding.startStopFollowClick() {
        stopFollowButton.setOnClickListener {  // Bunu sormak mantıklı olabilir ama şuan zamanım yok
            if (stopFollowButton.text == getString(R.string.stop_follow)) {
                markerViewModel.apply {
                    markerManager.removeAllMarkers()
                    addressList.clear()
                    stopFollowButton.text = getString(R.string.start_follow)
                    markerViewModel.locationService.stopService(requireContext())
                }
            } else {
                stopFollowButton.text = getString(R.string.stop_follow)
                markerViewModel.locationService.startService(requireContext())
            }
        }
    }

    private fun listenLocation() {

        lifecycleScope.launch {
            markerViewModel.currentLocation.collect { latLng ->
                latLng?.let {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 15f))
                    val nameOfLocation = it.getNameOfLocation(requireContext())

                    //  if (!markerViewModel.addressList.contains(AddressModel(it, nameOfLocation))) {
                    markerViewModel.markerManager.addMarker(it, nameOfLocation)
                    markerViewModel.addressList.add(AddressModel(it, nameOfLocation))
                    //  }
                }
            }
        }


      /*  lifecycleScope.launch {
            markerViewModel.locationService.locationFlow.collect { latLng ->

            }
        } */
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

    }


    override fun onMapReady(map: GoogleMap) {
        markerViewModel.markerManager.setMap(map)
        googleMap = map
    }
}
