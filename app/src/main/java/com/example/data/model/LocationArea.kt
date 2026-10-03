package com.example.data.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class LocationArea(
    val name: String,
    val city: String, // "Ota" or "Lagos"
    val state: String, // "Ogun" or "Lagos"
    val latitude: Double,
    val longitude: Double,
    val isPopular: Boolean = true
)

object LocationCatalog {
    val predefinedLocations = listOf(
        // Ota & surroundings
        LocationArea("Sango Ota", "Ota", "Ogun", 6.7090, 3.2384),
        LocationArea("Toll Gate Ota", "Ota", "Ogun", 6.6780, 3.2420),
        LocationArea("KM 10 / Canaanland", "Ota", "Ogun", 6.6710, 3.1610),
        LocationArea("Bells / Idiroko Rd", "Ota", "Ogun", 6.7150, 3.2100),
        LocationArea("Iyana Iyesi Ota", "Ota", "Ogun", 6.6850, 3.1950),
        LocationArea("Atan Ota", "Ota", "Ogun", 6.7500, 3.1500),

        // Lagos Mainland
        LocationArea("Ikeja", "Lagos", "Lagos", 6.6018, 3.3515),
        LocationArea("Alimosho / Egbeda", "Lagos", "Lagos", 6.6111, 3.2667),
        LocationArea("Yaba", "Lagos", "Lagos", 6.5095, 3.3711),
        LocationArea("Surulere", "Lagos", "Lagos", 6.4975, 3.3575),
        LocationArea("Maryland", "Lagos", "Lagos", 6.5744, 3.3725),
        LocationArea("Festac Town", "Lagos", "Lagos", 6.4678, 3.2842),
        LocationArea("Ikorodu", "Lagos", "Lagos", 6.6194, 3.5105),
        LocationArea("Oshodi", "Lagos", "Lagos", 6.5569, 3.3421),

        // Lagos Island
        LocationArea("Lekki Phase 1", "Lagos", "Lagos", 6.4698, 3.5852),
        LocationArea("Victoria Island", "Lagos", "Lagos", 6.4281, 3.4219),
        LocationArea("Ikoyi", "Lagos", "Lagos", 6.4549, 3.4246),
        LocationArea("Ajah", "Lagos", "Lagos", 6.4682, 3.5684)
    )

    val defaultUserLocation = predefinedLocations[0] // Sango Ota

    val categories = listOf(
        "All Services",
        "Electrician",
        "Plumber",
        "Carpenter",
        "Catering Services",
        "Land Surveyor",
        "Tailor & Fashion",
        "Auto Mechanic",
        "AC & Appliances",
        "Hair & Beauty",
        "Painter",
        "Welder",
        "Cleaner & Fumigator",
        "Tech & Gadget Repair"
    )

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius of the Earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun formatDistance(km: Double): String {
        return if (km < 1.0) {
            "${(km * 1000).toInt()} m"
        } else {
            String.format("%.1f km", km)
        }
    }
}
