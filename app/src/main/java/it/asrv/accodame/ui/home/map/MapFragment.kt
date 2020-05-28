package it.asrv.accodame.ui.home.map

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import it.asrv.accodame.MainActivity.Companion.TAG
import it.asrv.accodame.R
import it.asrv.accodame.ui.BaseFragment
import it.asrv.accodame.utils.DLog


class MapFragment : BaseFragment(), OnMapReadyCallback {

    private val REQ_CODE_LOCATION: Int = 123
    private val REQ_CODE_PERMISSION: Int = 456

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

    private fun doShowPermissionDeniedDialog(first: Boolean) {
        var positiveListener = DialogInterface.OnClickListener{ dialog, id ->
            if(first) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQ_CODE_LOCATION
                )
            } else {
                goToAppSettings()
            }
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

    private fun goToAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, REQ_CODE_PERMISSION)
    }

}