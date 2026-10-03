package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.PermissionBanner
import com.example.ui.components.PermissionRequestDialog
import com.example.ui.components.ApexDigitalHubBadge
import com.example.ui.components.ApexFooter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.LocationArea
import com.example.data.model.LocationCatalog
import com.example.data.model.ServiceProviderEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.FilterSection
import com.example.ui.components.ProviderCard
import com.example.ui.components.ZuccoTopBar
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenContainer
import com.example.ui.theme.LightSlate
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightElevated
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.SoftGray
import com.example.ui.theme.ZuccoGold
import com.example.ui.viewmodel.ProviderWithDistance
import com.example.ui.viewmodel.SortOption

@Composable
fun HomeScreen(
    providers: List<ProviderWithDistance>,
    savedProviders: List<ServiceProviderEntity>,
    currentUser: UserEntity?,
    currentLocation: LocationArea,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCity: String,
    onCitySelect: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit,
    onlyAvailable: Boolean,
    onToggleAvailable: (Boolean) -> Unit,
    onLocationChange: (LocationArea) -> Unit,
    onProviderClick: (ServiceProviderEntity) -> Unit,
    onToggleSave: (ServiceProviderEntity) -> Unit,
    onOpenAuth: () -> Unit,
    onOpenProviderDashboard: () -> Unit,
    onOpenClientBookings: () -> Unit,
    userMessage: String?,
    onClearMessage: () -> Unit,
    recentSearches: List<String> = emptyList(),
    onClearRecentSearches: () -> Unit = {},
    onAddRecentSearch: (String) -> Unit = {},
    hasLocationPermission: Boolean = false,
    hasNotificationPermission: Boolean = false,
    showPermissionDialog: Boolean = false,
    dismissedPermissionBanner: Boolean = false,
    onOpenPermissionDialog: () -> Unit = {},
    onDismissPermissionDialog: () -> Unit = {},
    onDismissPermissionBanner: () -> Unit = {},
    onDetectLocationClick: () -> Unit = {},
    onLocationPermissionResult: (Boolean) -> Unit = {},
    onNotificationPermissionResult: (Boolean) -> Unit = {},
    onSendTestNotification: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val locFine = permissionsMap[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val locCoarse = permissionsMap[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val locGranted = locFine || locCoarse
        onLocationPermissionResult(locGranted)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notifGranted = permissionsMap[Manifest.permission.POST_NOTIFICATIONS] == true
            onNotificationPermissionResult(notifGranted)
        } else {
            onNotificationPermissionResult(true)
        }
    }

    val requestAllPermissions = {
        val list = mutableListOf<String>()
        if (!hasLocationPermission) {
            list.add(Manifest.permission.ACCESS_FINE_LOCATION)
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (list.isNotEmpty()) {
            permissionLauncher.launch(list.toTypedArray())
        } else {
            onDetectLocationClick()
        }
    }
    var currentNavTab by remember { mutableIntStateOf(0) } // 0: Explore, 1: Saved, 2: Requests, 3: Provider Hub
    var savedSearchQuery by remember { mutableStateOf("") }
    var selectedSavedCategory by remember { mutableStateOf("All") }

    val savedCategories = remember(savedProviders) {
        listOf("All") + savedProviders.map { it.serviceCategory }.distinct().sorted()
    }

    val filteredSavedProviders = remember(savedProviders, savedSearchQuery, selectedSavedCategory) {
        savedProviders.filter { provider ->
            val matchesQuery = savedSearchQuery.isBlank() ||
                    provider.businessName.contains(savedSearchQuery, ignoreCase = true) ||
                    provider.providerName.contains(savedSearchQuery, ignoreCase = true) ||
                    provider.serviceCategory.contains(savedSearchQuery, ignoreCase = true) ||
                    provider.district.contains(savedSearchQuery, ignoreCase = true) ||
                    provider.city.contains(savedSearchQuery, ignoreCase = true)

            val matchesCategory = selectedSavedCategory == "All" ||
                    provider.serviceCategory.equals(selectedSavedCategory, ignoreCase = true)

            matchesQuery && matchesCategory
        }
    }

    LaunchedEffect(userMessage) {
        if (userMessage != null) {
            snackbarHostState.showSnackbar(userMessage)
            onClearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ZuccoTopBar(
                currentLocation = currentLocation,
                currentUser = currentUser,
                hasLocationPermission = hasLocationPermission,
                hasNotificationPermission = hasNotificationPermission,
                onLocationChange = onLocationChange,
                onDetectLocationClick = {
                    if (hasLocationPermission) {
                        onDetectLocationClick()
                    } else {
                        requestAllPermissions()
                    }
                },
                onOpenPermissionDialog = onOpenPermissionDialog,
                onAuthClick = onOpenAuth
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MidnightElevated,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentNavTab == 0,
                    onClick = { currentNavTab = 0 },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                    label = { Text("Explore", fontSize = 11.sp, fontWeight = if (currentNavTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightDark,
                        selectedTextColor = ZuccoGold,
                        indicatorColor = ZuccoGold,
                        unselectedIconColor = LightSlate,
                        unselectedTextColor = LightSlate
                    ),
                    modifier = Modifier.testTag("nav_explore")
                )

                NavigationBarItem(
                    selected = currentNavTab == 1,
                    onClick = { currentNavTab = 1 },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved") },
                    label = { Text("Saved (${savedProviders.size})", fontSize = 11.sp, fontWeight = if (currentNavTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightDark,
                        selectedTextColor = ZuccoGold,
                        indicatorColor = ZuccoGold,
                        unselectedIconColor = LightSlate,
                        unselectedTextColor = LightSlate
                    ),
                    modifier = Modifier.testTag("nav_saved")
                )

                NavigationBarItem(
                    selected = currentNavTab == 2,
                    onClick = { onOpenClientBookings() },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Requests") },
                    label = { Text("Requests", fontSize = 11.sp, fontWeight = if (currentNavTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightDark,
                        selectedTextColor = ZuccoGold,
                        indicatorColor = ZuccoGold,
                        unselectedIconColor = LightSlate,
                        unselectedTextColor = LightSlate
                    ),
                    modifier = Modifier.testTag("nav_requests")
                )

                NavigationBarItem(
                    selected = currentNavTab == 3,
                    onClick = {
                        if (currentUser?.role == UserRole.PROVIDER) {
                            onOpenProviderDashboard()
                        } else {
                            onOpenAuth()
                        }
                    },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Artisan Hub") },
                    label = {
                        Text(
                            if (currentUser?.role == UserRole.PROVIDER) "My Shop" else "Join as Artisan",
                            fontSize = 11.sp,
                            fontWeight = if (currentNavTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightDark,
                        selectedTextColor = ZuccoGold,
                        indicatorColor = ZuccoGold,
                        unselectedIconColor = LightSlate,
                        unselectedTextColor = LightSlate
                    ),
                    modifier = Modifier.testTag("nav_artisan_hub")
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // View Saved Mode
            if (currentNavTab == 1) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Saved Artisans & Services",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NavyBlue
                                    )
                                    Text(
                                        text = "Your bookmarked professionals across Ota & Lagos",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LightSlate
                                    )
                                }
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${savedProviders.size} Saved",
                                        color = Color(0xFF92400E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            if (savedProviders.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                // Search bar inside saved
                                OutlinedTextField(
                                    value = savedSearchQuery,
                                    onValueChange = { savedSearchQuery = it },
                                    placeholder = {
                                        Text(
                                            "Search saved artisans by trade, name, or area...",
                                            fontSize = 13.sp,
                                            color = LightSlate
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = NavyBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        if (savedSearchQuery.isNotBlank()) {
                                            IconButton(onClick = { savedSearchQuery = "" }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear search",
                                                    tint = LightSlate,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = DarkBlack,
                                        unfocusedTextColor = DarkBlack,
                                        cursorColor = NavyBlue,
                                        focusedBorderColor = SkyBlue,
                                        unfocusedBorderColor = SoftGray,
                                        focusedContainerColor = PureWhite,
                                        unfocusedContainerColor = PureWhite,
                                        focusedPlaceholderColor = LightSlate,
                                        unfocusedPlaceholderColor = LightSlate,
                                        focusedLeadingIconColor = NavyBlue,
                                        unfocusedLeadingIconColor = LightSlate
                                    ),
                                    textStyle = TextStyle(color = DarkBlack, fontSize = 14.sp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("saved_search_bar")
                                )

                                if (savedCategories.size > 2) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        savedCategories.forEach { category ->
                                            val isSelected = selectedSavedCategory == category
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { selectedSavedCategory = category },
                                                label = {
                                                    Text(
                                                        text = category,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = NavyBlue,
                                                    selectedLabelColor = PureWhite,
                                                    containerColor = PureWhite,
                                                    labelColor = DarkBlack
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.testTag("saved_filter_$category")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (savedProviders.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 24.dp),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "No Saved Artisans Yet",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyBlue
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Tap the bookmark icon ⭐ on any artisan card to save them here for quick contact and bookings.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LightSlate,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Button(
                                        onClick = { currentNavTab = 0 },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("explore_artisans_from_empty_saved")
                                    ) {
                                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Explore Artisans in Ota & Lagos", color = PureWhite)
                                    }
                                }
                            }
                        }
                    } else if (filteredSavedProviders.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = LightSlate,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No matching saved artisans",
                                    fontWeight = FontWeight.Bold,
                                    color = NavyBlue
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try adjusting your search terms or category filter.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LightSlate
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(onClick = {
                                    savedSearchQuery = ""
                                    selectedSavedCategory = "All"
                                }) {
                                    Text("Reset Filters")
                                }
                            }
                        }
                    } else {
                        items(filteredSavedProviders, key = { it.id }) { provider ->
                            val distKm = LocationCatalog.calculateDistanceKm(
                                currentLocation.latitude,
                                currentLocation.longitude,
                                provider.latitude,
                                provider.longitude
                            )
                            val formattedDistance = LocationCatalog.formatDistance(distKm)
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                ProviderCard(
                                    provider = provider,
                                    distanceFormatted = formattedDistance,
                                    onCardClick = { onProviderClick(provider) },
                                    onToggleSave = { onToggleSave(provider) }
                                )
                            }
                        }
                    }
                }
            } else {
                // EXPLORE MODE (Default)
                if (!dismissedPermissionBanner && (!hasLocationPermission || !hasNotificationPermission)) {
                    PermissionBanner(
                        hasLocationPermission = hasLocationPermission,
                        hasNotificationPermission = hasNotificationPermission,
                        onRequestClick = {
                            requestAllPermissions()
                        },
                        onDismissClick = onDismissPermissionBanner
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // SEARCH BAR & FILTERS (Scrolls away seamlessly with artisan list)
                    item {
                        FilterSection(
                            searchQuery = searchQuery,
                            onSearchChange = onSearchChange,
                            selectedCity = selectedCity,
                            onCitySelect = onCitySelect,
                            selectedCategory = selectedCategory,
                            onCategorySelect = onCategorySelect,
                            sortOption = sortOption,
                            onSortChange = onSortChange,
                            onlyAvailable = onlyAvailable,
                            onToggleAvailable = onToggleAvailable,
                            resultCount = providers.size,
                            savedCount = savedProviders.size,
                            onViewSaved = { currentNavTab = 1 },
                            recentSearches = recentSearches,
                            onClearRecentSearches = onClearRecentSearches,
                            onAddRecentSearch = onAddRecentSearch,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    // Hero Banner Item with dual images for "Find Verified Artisans & Services Close to You"
                    if (searchQuery.isBlank() && selectedCategory == "All Services" && selectedCity == "All") {
                        item {
                            var activeHeroIndex by remember { mutableIntStateOf(0) }
                            val heroImages = listOf(
                                R.drawable.hero_image to "Handshake upon service completion",
                                R.drawable.hero_imagee to "In-person inspection & verification"
                            )

                            LaunchedEffect(Unit) {
                                while (true) {
                                    kotlinx.coroutines.delay(4000)
                                    activeHeroIndex = (activeHeroIndex + 1) % heroImages.size
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                    ) {
                                        val currentHero = heroImages[activeHeroIndex]
                                        Image(
                                            painter = painterResource(id = currentHero.first),
                                            contentDescription = currentHero.second,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable {
                                                    activeHeroIndex = (activeHeroIndex + 1) % heroImages.size
                                                },
                                            contentScale = ContentScale.Crop
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(DarkBlack.copy(alpha = 0.20f))
                                        )

                                        // Apex Digital Hub Badge
                                        Surface(
                                            color = com.example.ui.theme.ZuccoGold,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_zucco_logo_transparent),
                                                    contentDescription = "Zucco Logo",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "POWERED BY APEX DIGITAL HUB",
                                                    color = com.example.ui.theme.ZuccoAppBackground,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }

                                        // Carousel Indicator Dots
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            heroImages.indices.forEach { index ->
                                                val isSelected = index == activeHeroIndex
                                                Box(
                                                    modifier = Modifier
                                                        .size(if (isSelected) 10.dp else 6.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) ZuccoGold else PureWhite.copy(alpha = 0.7f))
                                                        .clickable { activeHeroIndex = index }
                                                )
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "Find Verified Artisans & Services Close to You",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = NavyBlue
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Hire trusted electricians, plumbers, tailors, auto mechanics & beauty professionals across Ota, Ikeja, Lekki, Yaba, VI, and Surulere.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LightSlate,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Providers List
                    if (providers.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        tint = LightSlate,
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No Artisans Found",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyBlue
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (searchQuery.isNotBlank()) "No service providers matched '$searchQuery'. Try checking spelling or picking a location or category above." else "Try switching your city filter to 'All' or clearing your search keywords.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LightSlate
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            onSearchChange("")
                                            onCitySelect("All")
                                            onCategorySelect("All Services")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                                    ) {
                                        Text("Reset Filters", color = PureWhite)
                                    }
                                }
                            }
                        }
                    } else {
                        items(providers, key = { it.provider.id }) { providerWithDist ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                ProviderCard(
                                    provider = providerWithDist.provider,
                                    distanceFormatted = providerWithDist.formattedDistance,
                                    onCardClick = { onProviderClick(providerWithDist.provider) },
                                    onToggleSave = { onToggleSave(providerWithDist.provider) }
                                )
                            }
                        }
                    }

                    item {
                        ApexFooter(modifier = Modifier.padding(top = 16.dp))
                    }
                }
            }
        }
    }

    PermissionRequestDialog(
        showDialog = showPermissionDialog,
        onDismiss = onDismissPermissionDialog,
        hasLocationPermission = hasLocationPermission,
        hasNotificationPermission = hasNotificationPermission,
        onRequestPermissions = {
            requestAllPermissions()
        },
        onOpenSettings = {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        },
        onSendTestNotification = onSendTestNotification
    )
}
