package com.watasolutions.week8_permission.services.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


class LocationPermissionUtils {
    private var listener: LocationComponentListener? = null

    interface LocationComponentListener {
        fun onLocationPermission(isGranted: Boolean)
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    fun registerActivityResultForPermission(fragment: Fragment) {
        requestPermissionLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                listener?.onLocationPermission(isGranted)
            }
    }


    fun setListener(listener: LocationComponentListener?) {
        this.listener = listener
    }

    fun isLocationFeatureIsEnabled(context: Context): Boolean {
        return isLocationPermissionIsAllow(context)
    }

    fun requestLocationPermission(
        fragment: Fragment,
    ) {
        val context = fragment.requireContext()
        if (isLocationPermissionIsAllow(context)) {
            listener?.onLocationPermission(true)
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    // Permission:
    // ACCESS_COARSE_LOCATION
    // ACCESS_FINE_LOCATION
    private fun isLocationPermissionIsAllow(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun openLocationInAppSetting(context: Context) {
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    fun openAppSetting(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }


    fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = lm!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            networkEnabled = lm!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return gpsEnabled && networkEnabled
    }

    fun handleLocationPermission(isLocationGranted: Boolean) {
        listener?.onLocationPermission(isLocationGranted)
    }

}