package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ClientBookingsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProviderDashboardScreen
import com.example.ui.screens.ProviderDetailScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShopDesignerScreen
import com.example.ui.theme.ZuccoTheme
import com.example.ui.viewmodel.ZuccoViewModel
import com.example.util.NotificationHelper

sealed class AppScreen {
    data object Home : AppScreen()
    data object ProviderDetail : AppScreen()
    data object Auth : AppScreen()
    data object ProviderDashboard : AppScreen()
    data object ClientBookings : AppScreen()
    data object ShopDesigner : AppScreen()
    data object Settings : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannels(this)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            ZuccoTheme {
                ZuccoApp()
            }
        }
    }
}

@Composable
fun ZuccoApp(viewModel: ZuccoViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Anyone who installs the app for the first time should be shown the sign in / login page
    var currentScreen by remember {
        mutableStateOf<AppScreen>(
            if (uiState.isFirstInstall || uiState.currentUser == null) AppScreen.Auth else AppScreen.Home
        )
    }
    var hasAutoRouted by remember { mutableStateOf(false) }

    // Auto-Reroute on App Launch: When an already authenticated user (Client or Artisan) opens the app,
    // bypass the login screen completely and automatically route them straight to the main Explore screen.
    LaunchedEffect(uiState.currentUser) {
        if (!hasAutoRouted && uiState.currentUser != null) {
            hasAutoRouted = true
            currentScreen = AppScreen.Home
        }
    }

    when (currentScreen) {
        is AppScreen.Home -> {
            HomeScreen(
                providers = uiState.filteredProviders,
                savedProviders = uiState.savedProviders,
                currentUser = uiState.currentUser,
                currentLocation = uiState.userLocation,
                searchQuery = uiState.searchQuery,
                onSearchChange = { viewModel.onSearchQueryChanged(it) },
                selectedCity = uiState.selectedCity,
                onCitySelect = { viewModel.onCitySelected(it) },
                selectedCategory = uiState.selectedCategory,
                onCategorySelect = { viewModel.onCategorySelected(it) },
                sortOption = uiState.sortOption,
                onSortChange = { viewModel.onSortOptionChanged(it) },
                onlyAvailable = uiState.onlyAvailable,
                onToggleAvailable = { viewModel.onToggleAvailableOnly(it) },
                onLocationChange = { viewModel.onLocationSelected(it) },
                onProviderClick = { provider ->
                    viewModel.selectProvider(provider)
                    currentScreen = AppScreen.ProviderDetail
                },
                onToggleSave = { provider ->
                    viewModel.toggleSaveProvider(provider)
                },
                onOpenAuth = { currentScreen = AppScreen.Auth },
                onOpenProviderDashboard = { currentScreen = AppScreen.ProviderDashboard },
                onOpenClientBookings = { currentScreen = AppScreen.ClientBookings },
                userMessage = uiState.userMessage,
                onClearMessage = { viewModel.clearUserMessage() },
                recentSearches = uiState.recentSearches,
                onClearRecentSearches = { viewModel.clearRecentSearches() },
                onAddRecentSearch = { viewModel.addRecentSearch(it) },
                hasLocationPermission = uiState.hasLocationPermission,
                hasNotificationPermission = uiState.hasNotificationPermission,
                showPermissionDialog = uiState.showPermissionDialog,
                dismissedPermissionBanner = uiState.dismissedPermissionBanner,
                onOpenPermissionDialog = { viewModel.openPermissionDialog() },
                onDismissPermissionDialog = { viewModel.dismissPermissionDialog() },
                onDismissPermissionBanner = { viewModel.dismissPermissionBanner() },
                onDetectLocationClick = { viewModel.autoDetectLocation() },
                onLocationPermissionResult = { granted -> viewModel.onLocationPermissionResult(granted) },
                onNotificationPermissionResult = { granted -> viewModel.onNotificationPermissionResult(granted) },
                onSendTestNotification = { viewModel.sendTestNotification() }
            )
        }

        is AppScreen.ProviderDetail -> {
            BackHandler { currentScreen = AppScreen.Home }
            val provider = uiState.selectedProvider
            if (provider != null) {
                ProviderDetailScreen(
                    provider = provider,
                    reviews = uiState.selectedProviderReviews,
                    currentUser = uiState.currentUser,
                    userLocation = uiState.userLocation,
                    onBack = { currentScreen = AppScreen.Home },
                    onToggleSave = { viewModel.toggleSaveProvider(provider) },
                    onRequestBooking = { desc, date, urgency ->
                        viewModel.createBooking(provider, desc, date, urgency) { success ->
                            if (success) {
                                currentScreen = AppScreen.ClientBookings
                            }
                        }
                    },
                    onSubmitReview = { rating, comment ->
                        viewModel.addReview(provider.id, rating, comment) {}
                    }
                )
            } else {
                currentScreen = AppScreen.Home
            }
        }

        is AppScreen.Auth -> {
            BackHandler {
                if (uiState.currentUser != null) {
                    currentScreen = AppScreen.Home
                }
            }
            AuthScreen(
                currentUser = uiState.currentUser,
                onBack = { currentScreen = AppScreen.Home },
                onLogin = { id, pass, cb -> viewModel.login(id, pass, cb) },
                onRegisterClient = { name, email, phone, pass, city, dist, dp, cb ->
                    viewModel.registerClient(name, email, phone, pass, city, dist, dp, cb)
                },
                onRegisterProvider = { name, email, phone, whatsapp, pass, username, biz, cat, city, dist, addr, desc, pricing, yrs, bizReg, bizEmail, hours, services, areas, mapLink, dp, p1, p2, p3, cb ->
                    viewModel.registerProvider(
                        fullName = name,
                        email = email,
                        phone = phone,
                        whatsapp = whatsapp,
                        pass = pass,
                        username = username,
                        businessName = biz,
                        category = cat,
                        city = city,
                        district = dist,
                        address = addr,
                        description = desc,
                        pricing = pricing,
                        years = yrs,
                        businessRegNo = bizReg,
                        businessEmail = bizEmail,
                        openingHours = hours,
                        servicesOffered = services,
                        areasCovered = areas,
                        googleMapLink = mapLink,
                        profileDp = dp,
                        photo1 = p1,
                        photo2 = p2,
                        photo3 = p3,
                        onResult = cb
                    )
                },
                onQuickDemoLogin = { email ->
                    viewModel.login(email, "password") { _, _ -> }
                },
                onSendPasswordReset = { email, cb ->
                    viewModel.sendPasswordResetEmail(email, cb)
                },
                onResendVerification = { email, pass, cb ->
                    viewModel.resendVerificationEmail(email, pass, cb)
                },
                onLogout = { viewModel.logout() },
                onOpenSettings = { currentScreen = AppScreen.Settings }
            )
        }

        is AppScreen.ProviderDashboard -> {
            BackHandler { currentScreen = AppScreen.Home }
            ProviderDashboardScreen(
                provider = uiState.currentProviderProfile,
                bookings = uiState.providerBookings,
                currentUser = uiState.currentUser,
                onBack = { currentScreen = AppScreen.Home },
                onToggleAvailability = { isAvail ->
                    uiState.currentProviderProfile?.let {
                        viewModel.toggleAvailability(it.id, isAvail)
                    }
                },
                onUpdatePhotos = { p1, p2, p3 ->
                    viewModel.updateProviderPhotos(p1, p2, p3)
                },
                onUpdateBookingStatus = { id, status ->
                    viewModel.updateBookingStatus(id, status)
                },
                onOpenShopDesigner = { currentScreen = AppScreen.ShopDesigner },
                onUpdateShopBrandingAndOwnerBio = { tagline, color, style, badges, name, title, bio, motto ->
                    viewModel.updateShopBrandingAndOwnerBio(tagline, color, style, badges, name, title, bio, motto)
                },
                onUpdateProfilePhoto = { photoUri ->
                    viewModel.updateProfilePhoto(photoUri)
                }
            )
        }

        is AppScreen.ShopDesigner -> {
            BackHandler { currentScreen = AppScreen.ProviderDashboard }
            ShopDesignerScreen(
                provider = uiState.currentProviderProfile,
                onBack = { currentScreen = AppScreen.ProviderDashboard },
                onSaveShopBrandingAndOwnerBio = { tagline, color, style, badges, name, title, bio, motto ->
                    viewModel.updateShopBrandingAndOwnerBio(tagline, color, style, badges, name, title, bio, motto)
                }
            )
        }

        is AppScreen.ClientBookings -> {
            BackHandler { currentScreen = AppScreen.Home }
            ClientBookingsScreen(
                bookings = uiState.clientBookings,
                onBack = { currentScreen = AppScreen.Home },
                onFindArtisans = { currentScreen = AppScreen.Home }
            )
        }

        is AppScreen.Settings -> {
            BackHandler { currentScreen = AppScreen.Auth }
            SettingsScreen(
                currentUser = uiState.currentUser,
                currentProvider = uiState.currentProviderProfile,
                onBack = { currentScreen = AppScreen.Auth },
                onToggleProviderVisibility = { isAvail ->
                    uiState.currentProviderProfile?.let {
                        viewModel.toggleAvailability(it.id, isAvail)
                    }
                },
                onChangePassword = { oldPass, newPass, cb ->
                    viewModel.changePassword(oldPass, newPass, cb)
                },
                onDeleteAccount = {
                    viewModel.deleteCurrentAccount {
                        currentScreen = AppScreen.Auth
                    }
                },
                onLogout = {
                    viewModel.logout()
                    currentScreen = AppScreen.Auth
                },
                onUpdateProfilePhoto = { photoUri ->
                    viewModel.updateProfilePhoto(photoUri)
                }
            )
        }
    }
}
