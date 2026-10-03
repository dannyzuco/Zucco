package com.example.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import com.example.data.model.LocationArea
import com.example.data.model.LocationCatalog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun detectCurrentLocation(): LocationArea? {
        return try {
            val location = getLastKnownLocation() ?: requestSingleUpdate()
            if (location != null) {
                mapCoordinatesToNearestLocation(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("LocationHelper", "Error detecting current location", e)
            null
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { cont ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { loc: Location? ->
                cont.resume(loc)
            }
            .addOnFailureListener {
                cont.resume(null)
            }
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestSingleUpdate(): Location? = suspendCancellableCoroutine { cont ->
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000)
            .setMaxUpdates(1)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                cont.resume(result.lastLocation)
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            cont.resume(null)
        }
    }

    fun mapCoordinatesToNearestLocation(lat: Double, lng: Double): LocationArea {
        // Find closest predefined zone in Ota or Lagos
        val nearest = LocationCatalog.predefinedLocations.minByOrNull { loc ->
            LocationCatalog.calculateDistanceKm(lat, lng, loc.latitude, loc.longitude)
        }

        return if (nearest != null) {
            val distToNearest = LocationCatalog.calculateDistanceKm(lat, lng, nearest.latitude, nearest.longitude)
            if (distToNearest <= 15.0) {
                nearest.copy(latitude = lat, longitude = lng)
            } else {
                // If within Nigeria / general bounds of Lagos or Ogun State
                val state = if (lat >= 6.65) "Ogun" else "Lagos"
                val city = if (lat >= 6.65) "Ota" else "Lagos"
                LocationArea(
                    name = "Current GPS Location",
                    city = city,
                    state = state,
                    latitude = lat,
                    longitude = lng,
                    isPopular = false
                )
            }
        } else {
            LocationCatalog.defaultUserLocation
        }
    }
}
