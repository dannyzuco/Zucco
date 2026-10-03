package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BookingRequestEntity
import com.example.data.model.BookingStatus
import com.example.data.model.LocationArea
import com.example.data.model.LocationCatalog
import com.example.data.model.ServiceProviderEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.ZuccoRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ZuccoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ZuccoRepository.getInstance(application)
    private val locationHelper = com.example.data.location.LocationHelper(application)
    private val sharedPrefs = application.getSharedPreferences("zucco_prefs", android.content.Context.MODE_PRIVATE)

    private fun loadRecentSearchesFromPrefs(): List<String> {
        val raw = sharedPrefs.getString("recent_searches", "") ?: ""
        return if (raw.isBlank()) emptyList() else raw.split("|").filter { it.isNotBlank() }
    }

    private val _uiState = MutableStateFlow(ZuccoUiState(recentSearches = loadRecentSearchesFromPrefs()))
    val uiState: StateFlow<ZuccoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
            repository.restoreSavedUserSession()
            refreshBackendRecords()
            autoDetectLocation()
        }

        // Collect repository flows
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
                if (user != null) {
                    launch {
                        repository.getProviderByUserId(user.id).collect { prov ->
                            if (prov != null) {
                                _uiState.update { it.copy(currentProviderProfile = prov) }
                            } else if (user.role == UserRole.PROVIDER) {
                                val bName = if (!user.username.isNullOrBlank()) user.username else "${user.fullName}'s Business"
                                val defaultShop = ServiceProviderEntity(
                                    userId = user.id,
                                    businessName = bName,
                                    providerName = user.fullName,
                                    serviceCategory = "Electrician",
                                    city = if (user.city.isBlank()) "Ota" else user.city,
                                    district = if (user.district.isBlank()) "Sango Ota" else user.district,
                                    address = if (user.district.isBlank()) "Ota Road" else "${user.district}, ${user.city}",
                                    phoneNumber = user.phoneNumber,
                                    whatsappNumber = "234" + user.phoneNumber.trim().removePrefix("+").removePrefix("234").removePrefix("0"),
                                    description = "Professional service provider $bName specializing in high-quality local domestic jobs.",
                                    pricingInfo = "From ₦5,000 / inspection",
                                    yearsOfExperience = 5,
                                    latitude = 6.7090,
                                    longitude = 3.2384,
                                    isVerified = true,
                                    isAvailable = true,
                                    username = user.username ?: user.fullName.lowercase().replace(" ", "_"),
                                    shopTagline = "Premium custom services and expert care.",
                                    ownerName = user.fullName,
                                    ownerTitle = "Master Artisan",
                                    ownerBio = "Local professional with certified skills."
                                )
                                repository.saveServiceProviderProfileDirect(defaultShop)
                            } else {
                                _uiState.update { it.copy(currentProviderProfile = null) }
                            }
                        }
                    }
                    launch {
                        repository.getAllBookings().collect { all ->
                            val provId = _uiState.value.currentProviderProfile?.id
                            val filtered = if (provId != null) {
                                all.filter { it.providerId == provId }
                            } else all
                            _uiState.update { it.copy(providerBookings = filtered) }
                        }
                    }
                    launch {
                        repository.getBookingsForClient(user.id).collect { bookings ->
                            _uiState.update { it.copy(clientBookings = bookings) }
                        }
                    }
                } else {
                    _uiState.update { it.copy(currentProviderProfile = null, clientBookings = emptyList(), providerBookings = emptyList()) }
                }
            }
        }

        viewModelScope.launch {
            repository.userLocation.collect { loc ->
                _uiState.update { it.copy(userLocation = loc) }
                applyFilterAndSorting()
            }
        }

        viewModelScope.launch {
            repository.getAllProviders().collect { providers ->
                _uiState.update { it.copy(allProviders = providers) }
                applyFilterAndSorting()
            }
        }

        viewModelScope.launch {
            repository.getSavedProviders().collect { saved ->
                _uiState.update { it.copy(savedProviders = saved) }
            }
        }

        viewModelScope.launch {
            repository.getAllUsers().collect { users ->
                _uiState.update { it.copy(allUsers = users) }
            }
        }

        // Check if first install
        _uiState.update {
            it.copy(
                isFirstInstall = repository.isFirstInstall(),
                hasLocationPermission = NotificationHelper.hasLocationPermission(application),
                hasNotificationPermission = NotificationHelper.hasNotificationPermission(application)
            )
        }
        NotificationHelper.createNotificationChannels(application)
        refreshBackendRecords()
    }

    fun refreshBackendRecords() {
        viewModelScope.launch {
            val providerRecords = repository.getBackendProviderRecords()
            val userRecords = repository.getBackendUserRecords()
            val syncStatus = repository.getBackendSyncStatus()
            _uiState.update {
                it.copy(
                    backendRecords = providerRecords,
                    backendUserRecords = userRecords,
                    backendSyncStatus = syncStatus
                )
            }
        }
    }

    fun forceCloudSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val status = repository.forceCloudSync()
            val providerRecords = repository.getBackendProviderRecords()
            val userRecords = repository.getBackendUserRecords()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    backendRecords = providerRecords,
                    backendUserRecords = userRecords,
                    backendSyncStatus = status,
                    userMessage = "🔒 Secure Cloud Backend Synced: ${userRecords.size} users & ${providerRecords.size} artisans saved."
                )
            }
        }
    }

    fun markFirstInstallCompleted() {
        repository.markFirstInstallCompleted()
        _uiState.update { it.copy(isFirstInstall = false) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilterAndSorting()
    }

    fun addRecentSearch(query: String) {
        if (query.trim().isBlank()) return
        val normalized = query.trim()
        val current = loadRecentSearchesFromPrefs().toMutableList()
        current.remove(normalized)
        current.add(0, normalized)
        val limited = current.take(6)
        sharedPrefs.edit().putString("recent_searches", limited.joinToString("|")).apply()
        _uiState.update { it.copy(recentSearches = limited) }
    }

    fun clearRecentSearches() {
        sharedPrefs.edit().remove("recent_searches").apply()
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    fun onCitySelected(city: String) {
        _uiState.update { it.copy(selectedCity = city) }
        applyFilterAndSorting()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilterAndSorting()
    }

    fun onLocationSelected(location: LocationArea) {
        repository.setUserLocation(location)
    }

    fun autoDetectLocation() {
        viewModelScope.launch {
            try {
                val detected = locationHelper.detectCurrentLocation()
                if (detected != null) {
                    repository.setUserLocation(detected)
                    _uiState.update {
                        it.copy(userMessage = "📍 Location detected: ${detected.name} (${detected.city})")
                    }
                } else {
                    _uiState.update {
                        it.copy(userMessage = "Could not fetch GPS fix. Using ${it.userLocation.name}.")
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun updatePermissions(hasLocation: Boolean, hasNotification: Boolean) {
        _uiState.update {
            it.copy(
                hasLocationPermission = hasLocation,
                hasNotificationPermission = hasNotification
            )
        }
    }

    fun openPermissionDialog() {
        _uiState.update { it.copy(showPermissionDialog = true) }
    }

    fun dismissPermissionDialog() {
        _uiState.update { it.copy(showPermissionDialog = false) }
    }

    fun dismissPermissionBanner() {
        _uiState.update { it.copy(dismissedPermissionBanner = true) }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = granted) }
        if (granted) {
            autoDetectLocation()
        } else {
            _uiState.update {
                it.copy(userMessage = "Location permission denied. You can manually choose your area above.")
            }
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(hasNotificationPermission = granted) }
        if (granted) {
            NotificationHelper.createNotificationChannels(getApplication())
            NotificationHelper.sendPermissionGrantedWelcome(getApplication())
            _uiState.update {
                it.copy(userMessage = "🔔 Notifications enabled! You'll receive real-time booking updates.")
            }
        } else {
            _uiState.update {
                it.copy(userMessage = "Notification permission denied. You can enable alerts in App Settings.")
            }
        }
    }

    fun sendTestNotification() {
        NotificationHelper.sendNotification(
            context = getApplication(),
            notificationId = 8888,
            title = "🔔 Zucco Alert Test",
            message = "Location & Notification access is working smoothly! You will get instant alerts for artisan bookings in Ota & Lagos.",
            subText = "Zucco Alerts"
        )
        _uiState.update { it.copy(userMessage = "Test alert sent! Check your notification drawer.") }
    }

    fun onSortOptionChanged(option: SortOption) {
        _uiState.update { it.copy(sortOption = option) }
        applyFilterAndSorting()
    }

    fun onToggleAvailableOnly(enabled: Boolean) {
        _uiState.update { it.copy(onlyAvailable = enabled) }
        applyFilterAndSorting()
    }

    private fun applyFilterAndSorting() {
        val state = _uiState.value
        val userLoc = state.userLocation

        val rawQuery = state.searchQuery.trim()
        val stopwords = setOf("in", "at", "near", "for", "and", "the", "a", "to", "of", "service", "services", "artisan", "artisans")
        val queryTerms = if (rawQuery.isBlank()) {
            emptyList()
        } else {
            rawQuery.split(Regex("[,\\s]+"))
                .map { it.trim() }
                .filter { it.isNotBlank() && !stopwords.contains(it.lowercase()) }
        }

        val filtered = state.allProviders.filter { provider ->
            // Require admin approval/verification for public marketplace visibility
            val isApprovedMarketplaceArtisan = provider.isVerified || provider.isDemoAccount || provider.userId == state.currentUser?.id
            if (!isApprovedMarketplaceArtisan) return@filter false

            // Query match across terms (e.g. "Electrician Ota" or "Plumber Ikeja")
            val matchesQuery = queryTerms.isEmpty() || queryTerms.all { term ->
                provider.businessName.contains(term, ignoreCase = true) ||
                        provider.providerName.contains(term, ignoreCase = true) ||
                        provider.serviceCategory.contains(term, ignoreCase = true) ||
                        provider.description.contains(term, ignoreCase = true) ||
                        provider.district.contains(term, ignoreCase = true) ||
                        provider.city.contains(term, ignoreCase = true) ||
                        provider.address.contains(term, ignoreCase = true) ||
                        (provider.servicesOffered?.contains(term, ignoreCase = true) == true)
            }

            // City filter ("All", "Ota", "Lagos")
            val matchesCity = when (state.selectedCity) {
                "All" -> true
                "Ota" -> provider.city.equals("Ota", ignoreCase = true) ||
                        provider.district.contains("Ota", ignoreCase = true) ||
                        provider.address.contains("Ota", ignoreCase = true) ||
                        provider.address.contains("Ogun", ignoreCase = true) ||
                        provider.district.contains("Canaanland", ignoreCase = true)
                "Lagos" -> provider.city.equals("Lagos", ignoreCase = true) ||
                        provider.district.contains("Lagos", ignoreCase = true) ||
                        provider.address.contains("Lagos", ignoreCase = true) ||
                        provider.district.contains("Ikeja", ignoreCase = true) ||
                        provider.district.contains("Lekki", ignoreCase = true) ||
                        provider.district.contains("Yaba", ignoreCase = true) ||
                        provider.district.contains("Surulere", ignoreCase = true) ||
                        provider.district.contains("Victoria", ignoreCase = true) ||
                        provider.district.contains("Alimosho", ignoreCase = true) ||
                        provider.district.contains("Festac", ignoreCase = true)
                else -> provider.city.equals(state.selectedCity, ignoreCase = true)
            }

            // Category filter
            val matchesCat = state.selectedCategory == "All Services" ||
                    provider.serviceCategory.equals(state.selectedCategory, ignoreCase = true)

            // Availability filter
            val matchesAvail = !state.onlyAvailable || provider.isAvailable

            matchesQuery && matchesCity && matchesCat && matchesAvail
        }.map { provider ->
            val dist = LocationCatalog.calculateDistanceKm(
                userLoc.latitude,
                userLoc.longitude,
                provider.latitude,
                provider.longitude
            )
            ProviderWithDistance(
                provider = provider,
                distanceKm = dist,
                formattedDistance = LocationCatalog.formatDistance(dist)
            )
        }

        val sorted = when (state.sortOption) {
            SortOption.CLOSEST -> filtered.sortedBy { it.distanceKm }
            SortOption.TOP_RATED -> filtered.sortedByDescending { it.provider.rating }
            SortOption.EXPERIENCE -> filtered.sortedByDescending { it.provider.safeExperienceYears }
        }

        _uiState.update { it.copy(filteredProviders = sorted) }
    }

    fun selectProvider(provider: ServiceProviderEntity?) {
        _uiState.update { it.copy(selectedProvider = provider) }
        if (provider != null) {
            val q = _uiState.value.searchQuery.trim()
            if (q.isNotBlank()) {
                addRecentSearch(q)
            }
            viewModelScope.launch {
                repository.getReviewsForProvider(provider.id).collect { reviews ->
                    _uiState.update { it.copy(selectedProviderReviews = reviews) }
                }
            }
        }
    }

    fun toggleSaveProvider(provider: ServiceProviderEntity) {
        viewModelScope.launch {
            val willBeSaved = !provider.isSaved
            repository.toggleSavedStatus(provider.id, provider.isSaved)
            val feedback = if (willBeSaved) {
                "Saved \"${provider.businessName}\" to your favorites ⭐"
            } else {
                "Removed \"${provider.businessName}\" from your favorites"
            }
            if (_uiState.value.selectedProvider?.id == provider.id) {
                _uiState.update {
                    it.copy(
                        selectedProvider = it.selectedProvider?.copy(isSaved = willBeSaved),
                        userMessage = feedback
                    )
                }
            } else {
                _uiState.update {
                    it.copy(userMessage = feedback)
                }
            }
        }
    }

    fun toggleAvailability(providerId: Long, isAvailable: Boolean) {
        viewModelScope.launch {
            repository.updateAvailability(providerId, isAvailable)
            _uiState.update {
                it.copy(
                    currentProviderProfile = it.currentProviderProfile?.copy(isAvailable = isAvailable),
                    userMessage = if (isAvailable) "You are now marked Available for jobs" else "You are now marked Offline / Busy"
                )
            }
        }
    }

    fun updateProviderPhotos(p1: String?, p2: String?, p3: String?) {
        val currentProv = _uiState.value.currentProviderProfile ?: return
        viewModelScope.launch {
            repository.updatePortfolioPhotos(currentProv.id, p1, p2, p3)
            _uiState.update {
                it.copy(
                    currentProviderProfile = currentProv.copy(photo1 = p1, photo2 = p2, photo3 = p3),
                    userMessage = "Portfolio photos updated successfully"
                )
            }
        }
    }

    fun updateProviderProfile(
        businessName: String,
        category: String,
        city: String,
        district: String,
        address: String,
        phone: String,
        pricing: String,
        description: String,
        years: Int
    ) {
        val current = _uiState.value.currentProviderProfile ?: return
        viewModelScope.launch {
            val updated = current.copy(
                businessName = businessName,
                serviceCategory = category,
                city = city,
                district = district,
                address = address,
                phoneNumber = phone,
                whatsappNumber = phone.replace("+", "").replace(" ", ""),
                pricingInfo = pricing,
                description = description,
                yearsOfExperience = kotlin.math.max(1, kotlin.math.abs(years))
            )
            repository.updateProviderProfile(updated)
            _uiState.update {
                it.copy(
                    currentProviderProfile = updated,
                    userMessage = "Profile updated successfully"
                )
            }
        }
    }

    fun updateShopBrandingAndOwnerBio(
        shopTagline: String?,
        shopThemeColor: String,
        shopBannerStyle: String,
        specialtyBadges: String?,
        ownerName: String?,
        ownerTitle: String?,
        ownerBio: String?,
        ownerMotto: String?
    ) {
        val current = _uiState.value.currentProviderProfile ?: return
        viewModelScope.launch {
            val updated = current.copy(
                shopTagline = shopTagline,
                shopThemeColor = shopThemeColor,
                shopBannerStyle = shopBannerStyle,
                specialtyBadges = specialtyBadges,
                ownerName = ownerName,
                ownerTitle = ownerTitle,
                ownerBio = ownerBio,
                ownerMotto = ownerMotto
            )
            repository.updateProviderProfile(updated)
            _uiState.update {
                it.copy(
                    currentProviderProfile = updated,
                    userMessage = "🎨 Shop design & Owner bio updated! Clients can now see your custom shop!"
                )
            }
        }
    }

    fun login(identifier: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val res = repository.login(identifier, pass)
                if (res.isSuccess) {
                    val user = res.getOrNull()!!
                    _uiState.update { it.copy(userMessage = "Welcome back, ${user.fullName}!") }
                    onResult(true, "Login successful")
                } else {
                    onResult(false, res.exceptionOrNull()?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Login failed unexpectedly. Please try again.")
            }
        }
    }

    fun registerClient(
        fullName: String,
        email: String,
        phone: String,
        pass: String,
        city: String,
        district: String,
        profileDp: String? = null,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val res = repository.registerClient(fullName, email, phone, pass, city, district, profileDp)
                if (res.isSuccess) {
                    _uiState.update { it.copy(userMessage = "Account created! Welcome to Zucco.") }
                    onResult(true, "Client account created")
                } else {
                    onResult(false, res.exceptionOrNull()?.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Registration failed. Please try again.")
            }
        }
    }

    fun registerProvider(
        fullName: String,
        email: String,
        phone: String,
        whatsapp: String,
        pass: String,
        username: String,
        businessName: String,
        category: String,
        city: String,
        district: String,
        address: String,
        description: String,
        pricing: String,
        years: Int,
        businessRegNo: String?,
        businessEmail: String?,
        openingHours: String,
        servicesOffered: String?,
        areasCovered: String?,
        googleMapLink: String? = null,
        profileDp: String?,
        photo1: String?,
        photo2: String?,
        photo3: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val res = repository.registerServiceProvider(
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    whatsapp = whatsapp,
                    password = pass,
                    username = username,
                    businessName = businessName,
                    category = category,
                    city = city,
                    district = district,
                    address = address,
                    description = description,
                    pricing = pricing,
                    experienceYears = years,
                    businessRegNo = businessRegNo,
                    businessEmail = businessEmail,
                    openingHours = openingHours,
                    servicesOffered = servicesOffered,
                    areasCovered = areasCovered,
                    googleMapLink = googleMapLink,
                    profileDp = profileDp,
                    photo1 = photo1,
                    photo2 = photo2,
                    photo3 = photo3
                )
                if (res.isSuccess) {
                    val pair = res.getOrNull()!!
                    val isVerified = pair.second.isVerified
                    val msg = if (isVerified)
                        "🎉 Account created! As one of the first 100 service providers, you've received an Automatic Verified Badge!"
                    else
                        "Registration submitted! Welcome to Zucco."
                    _uiState.update {
                        it.copy(
                            currentProviderProfile = pair.second,
                            userMessage = msg
                        )
                    }
                    refreshBackendRecords()
                    onResult(true, msg)
                } else {
                    onResult(false, res.exceptionOrNull()?.message ?: "Provider registration failed")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Registration failed. Please check your connection.")
            }
        }
    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val res = repository.sendPasswordResetEmail(email)
                if (res.isSuccess) {
                    onResult(true, "Password reset link sent! Check your email inbox.")
                } else {
                    onResult(false, res.exceptionOrNull()?.message ?: "Failed to send password reset email.")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Failed to send password reset email.")
            }
        }
    }

    fun updateProfilePhoto(photoUri: String) {
        viewModelScope.launch {
            val user = _uiState.value.currentUser ?: return@launch
            repository.updateUserProfilePhoto(user.id, photoUri)
            _uiState.update { it.copy(userMessage = "Profile photo updated successfully!") }
        }
    }

    fun updatePortfolioPhotos(providerId: Long, photo1: String?, photo2: String?, photo3: String?) {
        viewModelScope.launch {
            repository.updatePortfolioPhotos(providerId, photo1, photo2, photo3)
            _uiState.update { it.copy(userMessage = "Work portfolio photos updated successfully!") }
        }
    }

    fun resendVerificationEmail(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val res = repository.resendVerificationEmail(email, pass)
                if (res.isSuccess) {
                    onResult(true, "Verification email resent! Please check your inbox.")
                } else {
                    onResult(false, res.exceptionOrNull()?.message ?: "Failed to resend verification email.")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Failed to resend verification email.")
            }
        }
    }

    fun createBooking(
        provider: ServiceProviderEntity,
        description: String,
        scheduledDate: String,
        urgency: String,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.createBooking(provider, description, scheduledDate, urgency)
            if (res.isSuccess) {
                val bookingId = res.getOrDefault(System.currentTimeMillis())
                NotificationHelper.sendBookingSubmittedNotification(
                    context = getApplication(),
                    providerName = provider.businessName,
                    serviceCategory = provider.serviceCategory,
                    bookingId = bookingId
                )
                _uiState.update { it.copy(userMessage = "Service request sent to ${provider.businessName}!") }
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    fun updateBookingStatus(bookingId: Long, status: BookingStatus) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
            val currentProvider = _uiState.value.currentProviderProfile
            val artisanName = currentProvider?.businessName ?: "Your Artisan"
            NotificationHelper.sendBookingStatusUpdateNotification(
                context = getApplication(),
                providerName = artisanName,
                newStatus = status.name.replace("_", " "),
                bookingId = bookingId
            )
            _uiState.update { it.copy(userMessage = "Booking status updated to ${status.name}") }
        }
    }

    fun addReview(providerId: Long, rating: Float, comment: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.addReview(providerId, rating, comment)
            _uiState.update { it.copy(userMessage = "Thank you! Your review was posted.") }
            onDone()
        }
    }

    fun changePassword(oldPass: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        val user = _uiState.value.currentUser
        if (user == null) {
            onResult(false, "No active user session")
            return
        }
        if (user.password != oldPass) {
            onResult(false, "Current password is incorrect")
            return
        }
        if (newPass.length < 6) {
            onResult(false, "New password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            repository.updatePassword(user.id, newPass)
            _uiState.update { it.copy(userMessage = "Password updated successfully") }
            onResult(true, "Password changed successfully")
        }
    }

    fun deleteCurrentAccount(onDeleted: () -> Unit) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val provider = _uiState.value.currentProviderProfile
            if (provider != null) {
                repository.deleteProvider(provider.id)
            }
            repository.deleteUser(user.id)
            repository.logout()
            _uiState.update {
                it.copy(
                    currentUser = null,
                    currentProviderProfile = null,
                    userMessage = "Account and data deleted permanently"
                )
            }
            onDeleted()
        }
    }

    fun logout() {
        repository.logout()
        _uiState.update {
            it.copy(
                currentUser = null,
                currentProviderProfile = null,
                userMessage = "Signed out"
            )
        }
    }

    fun quickSwitchUser(user: UserEntity) {
        repository.switchUser(user)
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
