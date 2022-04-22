package com.watasolutions.week8_permission.services.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


class LocationPermissionUtils {
    private var listener: LocationComponentListener? = null

    interface LocationComponentListener {
        fun onLocationFeatureEnable(isEnabled: Boolean)
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    fun registerActivityResultForPermission(fragment: Fragment) {
        requestPermissionLauncher =
            fragment.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                listener?.onLocationFeatureEnable(isGranted)
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
            listener?.onLocationFeatureEnable(true)
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    private fun isLocationPermissionIsAllow(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun openLocationInAppSetting(context: Context) {
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    fun handleLocationPermission(isLocationGranted: Boolean) {
        listener?.onLocationFeatureEnable(isLocationGranted)
    }

}