package it.asrv.accodame.ui.home.map

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import it.asrv.accodame.Configuration
import it.asrv.accodame.Constants
import it.asrv.accodame.R
import it.asrv.accodame.ui.BaseFragment
import it.asrv.accodame.ui.home.HomeFragment
import it.asrv.accodame.utils.DLog


class MapFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        val TAG = MapFragment::class.java.name
    }

    private val REQ_CODE_LOCATION: Int = 123
    private val REQ_CODE_PERMISSION: Int = 456
    private val REQ_CODE_LOCATION_UPDATE: Int = 789
    private val REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES"
    private val FIRST_LOCATION_UPDATE_KEY = "FIRST_LOCATION_UPDATE"

    private var mMap : GoogleMap? = null

    private lateinit var locationManager : LocationManager

    private lateinit var locationCallback: LocationCallback

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var requestingLocationUpdates = false
    private var firstLocationUpdate = true

    private var receiverSearch: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val query = intent.extras?.getString(Constants.EXTRA_SEARCH_QUERY)
            val latLng = Configuration.CITIES_LATLNG.get(query)
            if(latLng != null)
                goToLocation(latLng, Configuration.MAP_ZOOM_DEFAULT)
            else
                showMessage(
                    getString(R.string.alert_error_title),
                    getString(R.string.alert_location_not_found),
                    getString(R.string.alert_btn_ok),
                null,
                    null,
                    null
                )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if(firstLocationUpdate) {
                    firstLocationUpdate = false
                    var location = locationResult.lastLocation
                    goToLocation(
                        LatLng(location.latitude, location.longitude),
                        Configuration.MAP_ZOOM_DEFAULT
                    )
                    doEnableMyLocationButton()
                }
            }
        }

        updateValuesFromBundle(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState?.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        outState?.putBoolean(FIRST_LOCATION_UPDATE_KEY, firstLocationUpdate)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DLog.i(TAG, "onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.fMap)
        (supportMapFragment as SupportMapFragment).getMapAsync(this)
    }

    override fun onStart() {
        DLog.i(TAG, "onStart()")
        super.onStart()
        val filter = IntentFilter(Constants.ACTION_SEARCH)
        activity?.registerReceiver(receiverSearch, filter)
    }

    override fun onStop() {
        DLog.i(TAG, "onPause()")
        super.onStop()
        activity?.unregisterReceiver(receiverSearch)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onMapReady(p0: GoogleMap?) {
        val context = activity
        if(context != null) {
            mMap = p0

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    doShowPermissionDeniedDialog(true)
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQ_CODE_LOCATION
                    )
                }
            } else {
                onLocationPermissionGranted()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        DLog.i(TAG, "onRequestPermissionsResult: $requestCode")
        super.onActivityResult(requestCode, resultCode, data)
        val context = activity
        if(context != null) {
            when (requestCode) {
                REQ_CODE_PERMISSION -> {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        onLocationPermissionGranted()
                    } else {
                        doShowPermissionDeniedDialog(false)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        DLog.i(TAG, "onRequestPermissionsResult: $requestCode")
        when (requestCode) {
            REQ_CODE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onLocationPermissionGranted()
                } else {
                    doShowPermissionDeniedDialog(false)
                }
            }
        }
    }

    private fun createLocationRequest() : LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY)
        }

        if (savedInstanceState.keySet().contains(FIRST_LOCATION_UPDATE_KEY)) {
            firstLocationUpdate = savedInstanceState.getBoolean(
                FIRST_LOCATION_UPDATE_KEY)
        }
    }

    private fun startLocationUpdates() {
        DLog.i(TAG, "startLocationUpdates()")
        fusedLocationClient.requestLocationUpdates(createLocationRequest(),
        locationCallback,
        Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        DLog.i(TAG, "stopLocationUpdates()")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun doShowPermissionDeniedDialog(first: Boolean) {
        val positiveListener = DialogInterface.OnClickListener{ dialog, id ->
            if(first) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQ_CODE_LOCATION
                )
            } else {
                goToAppSettings()
            }
        }
        val negativeListener = DialogInterface.OnClickListener{ dialog, id ->
            (parentFragment as? HomeFragment)?.doFocusOnPlaceSearch()
        }
        showMessage(
            getString(R.string.alert_location_title),
            getString(R.string.alert_location_message),
            getString(R.string.alert_location_btn_activate_geolocal),
            positiveListener,
            getString(R.string.alert_location_btn_select_city),
            negativeListener)
    }

    private fun goToLocation(latLng: LatLng, zoom: Float) {
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    private fun onLocationPermissionGranted() {

        doCheckLocationAvailability()
    }

    private fun doCheckLocationAvailability() {
        if(activity!=null) {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest())

            val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {
                doEnableMyLocationButton()
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException){
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(activity,
                            REQ_CODE_LOCATION_UPDATE)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        if(sendEx.localizedMessage != null)
                            DLog.e(TAG, sendEx.localizedMessage!!);
                    }
                }
            }
        }

    }

    private fun doEnableMyLocationButton() {
        mMap?.isMyLocationEnabled = true
        mMap?.uiSettings?.isMyLocationButtonEnabled = true
    }

    private fun goToAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, REQ_CODE_PERMISSION)
    }

}