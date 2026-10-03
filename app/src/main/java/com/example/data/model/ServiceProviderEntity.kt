package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_providers")
data class ServiceProviderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long? = null,
    val businessName: String,
    val providerName: String,
    val serviceCategory: String, // e.g. "Electrician", "Plumber", "Carpenter", "Tailor & Fashion", "Auto Mechanic", "AC & Appliances", "Hair & Beauty", "Painter", "Welder"
    val city: String, // "Ota" or "Lagos"
    val district: String, // e.g. "Sango Ota", "Ikeja", "Lekki", "Yaba", "Victoria Island", "Surulere"
    val address: String,
    val phoneNumber: String,
    val whatsappNumber: String,
    val description: String,
    val pricingInfo: String,
    val yearsOfExperience: Int,
    val rating: Double = 4.8,
    val reviewCount: Int = 12,
    val isVerified: Boolean = true,
    val isAvailable: Boolean = true,
    // Display Picture (DP) profile photo
    val profileDp: String? = null,
    // Work portfolio: strictly maximum of 3 photos
    val photo1: String? = null,
    val photo2: String? = null,
    val photo3: String? = null,
    val latitude: Double,
    val longitude: Double,
    val isSaved: Boolean = false,
    // Business profile & registration details
    val username: String? = null,
    val businessRegNo: String? = null,
    val businessEmail: String? = null,
    val openingHours: String = "Mon - Sat: 8:00 AM - 6:00 PM",
    val servicesOffered: String? = null,
    val areasCovered: String? = null,
    val googleMapLink: String? = null,
    val syncedToCloudBackend: Boolean = true,
    // Custom Shop Design & Theme
    val shopTagline: String? = null,
    val shopThemeColor: String = "Gold", // "Gold", "Emerald", "SkyBlue", "SunsetOrange", "MidnightPurple", "ClassicNavy"
    val shopBannerStyle: String = "Modern Gradient", // "Modern Gradient", "Gold Luxury", "Minimalist Slate", "Workshop Wood", "Tech Glow"
    val specialtyBadges: String? = "⚡ Fast Response,🏆 Top Rated,🛡️ 100% Guaranteed",
    // About Business Owner / Artisan Bio
    val ownerName: String? = null,
    val ownerTitle: String? = null,
    val ownerBio: String? = null,
    val ownerMotto: String? = null,
    val isDemoAccount: Boolean = false
) {
    val photoList: List<String>
        get() = listOfNotNull(photo1, photo2, photo3).filter { it.isNotBlank() }

    val safeExperienceYears: Int
        get() = kotlin.math.max(1, kotlin.math.abs(yearsOfExperience))

    val formattedExperience: String
        get() = "$safeExperienceYears yrs exp"

    val formattedRating: String
        get() = String.format(java.util.Locale.US, "%.1f", kotlin.math.max(1.0, kotlin.math.min(5.0, rating)))
}
