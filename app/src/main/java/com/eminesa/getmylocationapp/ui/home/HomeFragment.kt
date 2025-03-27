package com.eminesa.getmylocationapp.ui.home

import com.eminesa.beinconnectclone.ui.base.BaseFragment
import com.eminesa.getmylocationapp.R
import com.eminesa.getmylocationapp.databinding.FragmentHomeBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun FragmentHomeBinding.bindScreen() {
        // Eğer mapFragment zaten XML'de varsa, onu alıyoruz.
        // val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
       // mapFragment?.getMapAsync((this@HomeFragment))
    }

   // override fun onMapReady(p0: GoogleMap) {

  //  }
}
