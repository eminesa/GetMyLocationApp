package com.eminesa.getmylocationapp.extention

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

fun LatLng.getNameOfLocation(context: Context): String {
    var locationInfo = "Bu konum için adres bulunamadı "
    val geocoder = Geocoder(context, Locale.getDefault())

    val addresses: List<Address>? =
        geocoder.getFromLocation(this.latitude, this.longitude, 1)

    if (!addresses.isNullOrEmpty()) {
        val address = addresses[0]
        val addressLine = address.getAddressLine(0) // Adresin tamamı
        val city = address.locality // Şehir
        val country = address.countryName // Ülke

        // Konum bilgilerini kullanıcıya gösterebiliriz
        locationInfo = "$addressLine, $city, $country"
    }
    return locationInfo
}
