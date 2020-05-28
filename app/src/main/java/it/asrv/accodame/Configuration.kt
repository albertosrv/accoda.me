package it.asrv.accodame

class Configuration {
    companion object {
        var debug: Boolean = BuildConfig.DEBUG

        var CITIES = arrayOf("Milano", "Torino")
    }
}