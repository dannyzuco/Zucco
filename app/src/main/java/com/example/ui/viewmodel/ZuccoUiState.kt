package com.example.ui.viewmodel

import com.example.data.model.BookingRequestEntity
import com.example.data.model.LocationArea
import com.example.data.model.LocationCatalog
import com.example.data.model.ReviewEntity
import com.example.data.model.ServiceProviderEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole

enum class SortOption(val title: String) {
    CLOSEST("Closest to Me"),
    TOP_RATED("Top Rated"),
    EXPERIENCE("Most Experienced")
}

data class ProviderWithDistance(
    val provider: ServiceProviderEntity,
    val distanceKm: Double,
    val formattedDistance: String
)

data class ZuccoUiState(
    val allProviders: List<ServiceProviderEntity> = emptyList(),
    val filteredProviders: List<ProviderWithDistance> = emptyList(),
    val savedProviders: List<ServiceProviderEntity> = emptyList(),
    val currentUser: UserEntity? = null,
    val currentProviderProfile: ServiceProviderEntity? = null,
    val userLocation: LocationArea = LocationCatalog.defaultUserLocation,
    val searchQuery: String = "",
    val selectedCity: String = "All", // "All", "Ota", "Lagos"
    val selectedCategory: String = "All Services",
    val sortOption: SortOption = SortOption.CLOSEST,
    val onlyAvailable: Boolean = false,
    val selectedProvider: ServiceProviderEntity? = null,
    val selectedProviderReviews: List<ReviewEntity> = emptyList(),
    val clientBookings: List<BookingRequestEntity> = emptyList(),
    val providerBookings: List<BookingRequestEntity> = emptyList(),
    val allUsers: List<UserEntity> = emptyList(),
    val backendRecords: List<com.example.data.backend.ProviderBackendDto> = emptyList(),
    val backendUserRecords: List<com.example.data.backend.UserBackendDto> = emptyList(),
    val backendSyncStatus: com.example.data.backend.BackendSyncStatus? = null,
    val userMessage: String? = null,
    val isFirstInstall: Boolean = false,
    val isLoading: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val showPermissionDialog: Boolean = false,
    val dismissedPermissionBanner: Boolean = false,
    val simulatedRole: UserRole? = null,
    val recentSearches: List<String> = emptyList()
)
