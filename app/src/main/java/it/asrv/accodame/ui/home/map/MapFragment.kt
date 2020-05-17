package it.asrv.accodame.ui.home.map

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import it.asrv.accodame.R
import it.asrv.accodame.ui.BaseFragment


class MapFragment : BaseFragment(), OnMapReadyCallback {

    private val REQ_CODE_LOCATION: Int = 123

    var mMap : GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.fMap)
        (supportMapFragment as SupportMapFragment).getMapAsync(this)
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
                    doShowPermissionDeniedDialog()
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQ_CODE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onLocationPermissionGranted()
                }
            }
        }
    }

    private fun doShowPermissionDeniedDialog() {
        var positiveListener = DialogInterface.OnClickListener{ dialog, id ->
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQ_CODE_LOCATION
            )
        }
        var negativeListener = DialogInterface.OnClickListener{ dialog, id ->
            //TODO
        }
        showMessage(
            getString(R.string.alert_location_title),
            getString(R.string.alert_location_message),
            getString(R.string.alert_location_btn_activate_geolocal),
            positiveListener,
            getString(R.string.alert_location_btn_select_city),
            negativeListener)
    }

    private fun onLocationPermissionGranted() {
        mMap?.isMyLocationEnabled = true
        mMap?.uiSettings?.isMyLocationButtonEnabled = true
    }

}