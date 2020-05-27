package it.asrv.accodame

import android.R.attr.apiKey
import android.app.Application
import com.google.android.libraries.places.api.Places


class AccodameApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Places SDK
        Places.initialize(applicationContext, getString(R.string.google_api_key))
    }
}