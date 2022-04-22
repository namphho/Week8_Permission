package com.watasolutions.week8_permission.services.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationClient(context: Context) {
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    suspend fun getLastLocation(): Location? {
        return suspendCancellableCoroutine { cont ->
            val context = fusedLocationClient.applicationContext
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("LocationClient", "request permission")
                cont.resume(null)
            } else {
                val callback =
                    OnSuccessListener<Location> { location ->
                        cont.resume(location)
                    }
                fusedLocationClient.lastLocation.addOnSuccessListener(callback)
            }

        }
    }


    fun getDistanceInMeters(locationA: Location?, latB: Double?, lngB: Double?): Float? {
        if (latB == null || lngB == null) {
            return null
        }
        return locationA?.let {
            val results = FloatArray(1)
            Location.distanceBetween(locationA.latitude, locationA.longitude, latB, lngB, results)
            results[0]
        }
    }

    fun getDistanceInMiles(locationA: Location?, latB: Double?, lngB: Double?): Float? {
        val distance = getDistanceInMeters(locationA, latB, lngB)
        return distance?.let {
            it / 1609.344f
        }
    }
}