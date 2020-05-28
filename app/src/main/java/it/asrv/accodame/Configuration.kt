package it.asrv.accodame

import com.google.android.gms.maps.model.LatLng

class Configuration {
    companion object {
        var debug: Boolean = BuildConfig.DEBUG

        var CITIES = arrayOf("Milano", "Roma", "Napoli")
        var CITIES_LATLNG = mapOf(
            "Milano" to LatLng(45.462, 9.1900),
            "Roma" to LatLng(41.9028, 12.4964),
            "Napoli" to LatLng(40.8518, 14.2681))

        var MAP_ZOOM_DEFAULT = 14.0f

    }
}