package com.example.ui.screens

import com.example.ui.components.ApexFooter
import com.example.ui.components.PhotoUploadSlots
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingRequestEntity
import com.example.data.model.BookingStatus
import com.example.data.model.ServiceProviderEntity
import com.example.data.model.UserEntity
import com.example.ui.components.PhotoUploadSlots
import com.example.ui.theme.BorderGray
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenContainer
import com.example.ui.theme.LightSlate
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.SoftGray
import com.example.ui.theme.ZuccoGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    provider: ServiceProviderEntity?,
    bookings: List<BookingRequestEntity>,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onToggleAvailability: (Boolean) -> Unit,
    onUpdatePhotos: (p1: String?, p2: String?, p3: String?) -> Unit,
    onUpdateBookingStatus: (bookingId: Long, status: BookingStatus) -> Unit,
    onOpenShopDesigner: () -> Unit = {},
    onUpdateShopBrandingAndOwnerBio: (
        shopTagline: String?,
        shopThemeColor: String,
        shopBannerStyle: String,
        specialtyBadges: String?,
        ownerName: String?,
        ownerTitle: String?,
        ownerBio: String?,
        ownerMotto: String?
    ) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onUpdateProfilePhoto: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val profilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdateProfilePhoto(uri.toString())
        }
    }

    if (provider == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Provider Hub", color = PureWhite, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PureWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No Service Provider Profile Linked",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You are currently signed in as a client. Register or switch to a provider account to manage services and work photos in Ota or Lagos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightSlate,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                    ) {
                        Text("Return to Marketplace", color = PureWhite)
                    }
                }
            }
        }
        return
    }

    val displayShopName = when {
        provider.businessName.isNotBlank() -> provider.businessName
        !provider.username.isNullOrBlank() -> "@${provider.username.removePrefix("@")}"
        else -> provider.providerName
    }

    // Editable status states dynamically bound to provider profile
    val defaultTagline = provider.shopTagline ?: if (provider.description.isNotBlank()) provider.description else "Professional ${provider.serviceCategory} Services in ${provider.city}"
    var shopTagline by remember(provider) { mutableStateOf(defaultTagline) }
    var shopThemeColor by remember(provider) { mutableStateOf(provider.shopThemeColor.ifBlank { "Gold" }) }
    var shopBannerStyle by remember(provider) { mutableStateOf(provider.shopBannerStyle.ifBlank { "Modern Gradient" }) }
    
    // Dynamic owner combined profile text
    val defaultBio = provider.ownerBio ?: "${provider.providerName}, Lead ${provider.serviceCategory} Professional. Quality service guaranteed."
    var ownerCombinedProfile by remember(provider) { mutableStateOf(defaultBio) }

    // Pricing Transparency Grid States dynamically derived from provider services
    val userServicesList = provider.servicesOffered?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
    var service1Name by remember(provider) { mutableStateOf(userServicesList.getOrNull(0) ?: "${provider.serviceCategory} Inspection & Estimate") }
    var service1Price by remember(provider) { mutableStateOf(provider.pricingInfo.ifBlank { "From ₦5,000" }) }
    var service2Name by remember(provider) { mutableStateOf(userServicesList.getOrNull(1) ?: "Standard Service & Maintenance") }
    var service2Price by remember(provider) { mutableStateOf("From ₦10,000") }
    var service3Name by remember(provider) { mutableStateOf(userServicesList.getOrNull(2) ?: "Full Diagnostics & Repair") }
    var service3Price by remember(provider) { mutableStateOf("From ₦25,000") }

    Scaffold(
        topBar = {
            Column {
                // ========================================================
                // 📱 PHOTOREALISTIC STATUS BAR WITH TIME & DYNAMIC ISLAND
                // ========================================================
                Surface(
                    color = NavyBlue,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Time on left
                        Text(
                            text = "02:33 AM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )

                        // Pill-shaped Dynamic Island in center
                        Surface(
                            color = Color.Black,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .width(90.dp)
                                .height(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3B82F6))
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 8.dp)
                                )
                            }
                        }

                        // Status icons on right
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LTE  📶  🔋 84%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PureWhite
                            )
                        }
                    }
                }

                // Main Top App Bar
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = displayShopName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PureWhite
                            )
                            Text(
                                text = "${provider.serviceCategory} Shop • ${provider.city}",
                                fontSize = 11.sp,
                                color = SkyBlue
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("dashboard_back_button")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PureWhite)
                        }
                    },
                    actions = {
                        // Streamlined "Help Center" Gold Button
                        Surface(
                            onClick = {
                                Toast.makeText(context, "Opening Zucco Help & Support Center...", Toast.LENGTH_SHORT).show()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://zucco.app/help"))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
                            },
                            color = ZuccoGold,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Help, contentDescription = null, tint = DarkBlack, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Help Center",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkBlack
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // ========================================================
            // 🚀 MODIFICATION 1: AVAILABILITY & INITIAL STATUS (SUMMARY CARD)
            // ========================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Profile Avatar with Camera Button
                            val currentDp = provider.profileDp ?: currentUser?.profileDp
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        profilePickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (!currentDp.isNullOrBlank()) {
                                    AsyncImage(
                                        model = currentDp,
                                        contentDescription = "Profile Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(NavyBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = provider.providerName.take(1).uppercase(),
                                            color = ZuccoGold,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                                Surface(
                                    color = LeafGreen,
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.BottomEnd)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Upload Profile Photo",
                                            tint = PureWhite,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = displayShopName,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = NavyBlue
                                )
                                Text(
                                    text = "Artisan Dashboard • ${provider.city}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LightSlate
                                )
                                Text(
                                    text = "📷 Tap photo to change",
                                    fontSize = 10.sp,
                                    color = ZuccoGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Availability Toggle Switch (Accepting new jobs)
                        Switch(
                            checked = provider.isAvailable,
                            onCheckedChange = { onToggleAvailability(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = LeafGreen
                            ),
                            modifier = Modifier.testTag("toggle_availability_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Verification & Approval Status Banner
                    val isLiveOnMarketplace = provider.isVerified || provider.isDemoAccount
                    Surface(
                        color = if (isLiveOnMarketplace) LeafGreenContainer else ZuccoGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isLiveOnMarketplace) LeafGreen.copy(alpha = 0.5f) else ZuccoGold.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isLiveOnMarketplace) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (isLiveOnMarketplace) LeafGreen else ZuccoGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isLiveOnMarketplace) "Account Status: Approved & Live on Marketplace" else "Account Status: Pending Admin Review & Approval",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isLiveOnMarketplace) LeafGreen else ZuccoGold
                                )
                            }
                            if (!isLiveOnMarketplace) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Your artisan profile is undergoing admin verification. Once approved by Zucco Admin, your shop will go live for clients in ${provider.city}.",
                                    fontSize = 11.sp,
                                    color = DarkBlack,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = BorderGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Verified Trust Badges Row
                    Text(
                        text = "Verified Trust Badges",
                        style = MaterialTheme.typography.labelSmall,
                        color = LightSlate,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("⚡ Fast Response", "🏆 Top Rated", "🛡️ 100% Guaranteed").forEach { badge ->
                            Surface(
                                color = LeafGreenContainer,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, LeafGreen)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = badge,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LeafGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========================================================
            // 🚀 MODIFICATION 2: ELEVATED WORK PORTFOLIO & CAP WARNING
            // ========================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Work Portfolio (3 / 3 Photos Uploaded)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NavyBlue
                            )
                            Text(
                                text = "Maximum 3 photos allowed (Upgrade to Pro for More)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    PhotoUploadSlots(
                        photos = listOf(provider.photo1, provider.photo2, provider.photo3),
                        onPhotoChanged = { index, uri ->
                            onUpdatePhotos(provider.photo1, provider.photo2, provider.photo3)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========================================================
            // 🚀 MODIFICATION 3: PRICING TRANSPARENCY GRID BLOCK
            // ========================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = SkyBlueContainer,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = NavyBlue, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Services & Pricing Grid",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NavyBlue
                            )
                            Text(
                                text = "Transparency builds client trust before first contact.",
                                fontSize = 11.sp,
                                color = LightSlate
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamic Pricing Fields Grid Rows
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Row 1
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = service1Name,
                                onValueChange = { service1Name = it },
                                label = { Text("Service Line 1") },
                                textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SkyBlue,
                                    unfocusedBorderColor = BorderGray,
                                    focusedContainerColor = SoftGray,
                                    unfocusedContainerColor = SoftGray
                                ),
                                modifier = Modifier.weight(1.5f)
                            )
                            OutlinedTextField(
                                value = service1Price,
                                onValueChange = { service1Price = it },
                                label = { Text("Base Cost") },
                                textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyBlue),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SkyBlue,
                                    unfocusedBorderColor = BorderGray,
                                    focusedContainerColor = SoftGray,
                                    unfocusedContainerColor = SoftGray
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Row 2
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = service2Name,
                                onValueChange = { service2Name = it },
                                label = { Text("Service Line 2") },
                                textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SkyBlue,
                                    unfocusedBorderColor = BorderGray,
                                    focusedContainerColor = SoftGray,
                                    unfocusedContainerColor = SoftGray
                                ),
                                modifier = Modifier.weight(1.5f)
                            )
                            OutlinedTextField(
                                value = service2Price,
                                onValueChange = { service2Price = it },
                                label = { Text("Base Cost") },
                                textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyBlue),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SkyBlue,
                                    unfocusedBorderColor = BorderGray,
                                    focusedContainerColor = SoftGray,
                                    unfocusedContainerColor = SoftGray
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Row 3
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = service3Name,
                                onValueChange = { service3Name = it },
                                label = { Text("Service Line 3") },
                                textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SkyBlue,
                                    unfocusedBorderColor = BorderGray,
                                    focusedContainerColor = SoftGray,
                                    unfocusedContainerColor = SoftGray
                                ),
                                modifier = Modifier.weight(1.5f)
                            )
                            OutlinedTextField(
                                value = service3Price,
                                onValueChange = { service3Price = it },
                                label = { Text("Base Cost") },
                                textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyBlue),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SkyBlue,
                                    unfocusedBorderColor = BorderGray,
                                    focusedContainerColor = SoftGray,
                                    unfocusedContainerColor = SoftGray
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========================================================
            // 🚀 MODIFICATION 4: CONSOLIDATED ABOUT THE BUSINESS OWNER
            // ========================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = LeafGreenContainer,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = LeafGreen, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About the Business Owner",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Full Profile Narrative Story",
                        fontSize = 11.sp,
                        color = LightSlate,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Consolidated Single Gray Input Card
                    OutlinedTextField(
                        value = ownerCombinedProfile,
                        onValueChange = { ownerCombinedProfile = it },
                        placeholder = { Text("Enter owner background and service commitment...") },
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkBlack,
                            unfocusedTextColor = DarkBlack,
                            focusedBorderColor = LeafGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = SoftGray,
                            unfocusedContainerColor = SoftGray
                        ),
                        textStyle = TextStyle(fontSize = 13.sp, color = DarkBlack, lineHeight = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("owner_combined_profile_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========================================================
            // 🚀 MODIFICATION 5: BRANDING SIMPLIFIED
            // ========================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = SkyBlueContainer,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = NavyBlue, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Design My Shop & Branding",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Only One Slogan Field
                    Text("Shop Tagline / Slogan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBlack)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = shopTagline,
                        onValueChange = { shopTagline = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkBlack,
                            unfocusedTextColor = DarkBlack,
                            focusedBorderColor = SkyBlue,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = SoftGray,
                            unfocusedContainerColor = SoftGray
                        ),
                        textStyle = TextStyle(fontSize = 13.sp, color = DarkBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_tagline_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simplified Accent Color selection block
                    Text(
                        text = "Choose a Shop Accent Color",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlack
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1: Default Platform Theme
                        Surface(
                            onClick = { shopThemeColor = "Gold" },
                            shape = RoundedCornerShape(10.dp),
                            color = if (shopThemeColor == "Gold") SkyBlueContainer else SoftGray,
                            border = BorderStroke(1.dp, if (shopThemeColor == "Gold") SkyBlue else BorderGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = NavyBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Default Theme",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyBlue
                                )
                            }
                        }

                        // Option 2: Unlock Premium Shop Styles (GOLD MONETIZED BUTTON)
                        Surface(
                            onClick = {
                                Toast.makeText(context, "Upgrade to Zucco PRO to unlock custom shop color themes!", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = ZuccoGold,
                            border = BorderStroke(1.dp, ZuccoGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DarkBlack, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Unlock Premium",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkBlack
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========================================================
            // 📸 WORK PORTFOLIO PHOTOS UPLOAD CARD
            // ========================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val currentWorkPhotos = listOf(provider.photo1, provider.photo2, provider.photo3)
                    PhotoUploadSlots(
                        photos = currentWorkPhotos,
                        onPhotoChanged = { index, uri ->
                            val p1 = if (index == 0) uri else provider.photo1
                            val p2 = if (index == 1) uri else provider.photo2
                            val p3 = if (index == 2) uri else provider.photo3
                            onUpdatePhotos(p1, p2, p3)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========================================================
            // 🚀 LIVE CLIENT VIEW PREVIEW (DEFAULT COHESIVE STYLING)
            // ========================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBlue),
                border = BorderStroke(1.dp, ZuccoGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👁️ Live Client View Preview",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ZuccoGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayShopName.take(1).uppercase(),
                                color = NavyBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = displayShopName,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "✨ $shopTagline",
                                color = ZuccoGold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "About Owner & Shop commitment:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZuccoGold
                            )
                            Text(
                                text = ownerCombinedProfile,
                                fontSize = 11.sp,
                                color = PureWhite,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Client Requests & Inquiries List
            Text(
                text = "Client Requests & Inquiries (${bookings.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NavyBlue
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (bookings.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftGray)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No pending service requests from clients yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = LightSlate
                        )
                    }
                }
            } else {
                bookings.forEach { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = booking.clientName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DarkBlack
                                )

                                Surface(
                                    color = when (booking.status) {
                                        BookingStatus.PENDING -> Color(0xFFFEF3C7)
                                        BookingStatus.ACCEPTED -> LeafGreenContainer
                                        BookingStatus.COMPLETED -> SkyBlueContainer
                                        BookingStatus.CANCELLED -> Color(0xFFFEE2E2)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = booking.status.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (booking.status) {
                                            BookingStatus.PENDING -> Color(0xFF92400E)
                                            BookingStatus.ACCEPTED -> LeafGreen
                                            BookingStatus.COMPLETED -> SkyBlue
                                            BookingStatus.CANCELLED -> Color(0xFF991B1B)
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = LightSlate, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${booking.clientLocation} • Urgency: ${booking.urgency}",
                                    fontSize = 11.sp,
                                    color = LightSlate
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = booking.issueDescription,
                                fontSize = 13.sp,
                                color = DarkBlack,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Preferred Schedule: ${booking.scheduledDate}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyBlue
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${booking.clientPhone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp), tint = PureWhite)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call Client", fontSize = 11.sp, color = PureWhite)
                                }

                                if (booking.status == BookingStatus.PENDING) {
                                    Button(
                                        onClick = { onUpdateBookingStatus(booking.id, BookingStatus.ACCEPTED) },
                                        colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = PureWhite)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Accept Job", fontSize = 11.sp, color = PureWhite)
                                    }
                                } else if (booking.status == BookingStatus.ACCEPTED) {
                                    FilledTonalButton(
                                        onClick = { onUpdateBookingStatus(booking.id, BookingStatus.COMPLETED) },
                                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = SkyBlueContainer),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(14.dp), tint = SkyBlue)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mark Done", fontSize = 11.sp, color = SkyBlue)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========================================================
            // 💾 CONFIRMATION / SAVE BUTTON AT THE BOTTOM
            // ========================================================
            Button(
                onClick = {
                    onUpdateShopBrandingAndOwnerBio(
                        shopTagline.ifBlank { null },
                        "Gold",
                        "Modern Gradient",
                        "⚡ Fast Response,🏆 Top Rated,🛡️ 100% Guaranteed",
                        provider.providerName,
                        "Founder & Lead Master Electrician",
                        ownerCombinedProfile.ifBlank { null },
                        "Safety first, precision always."
                    )
                    Toast.makeText(context, "Simplified Details saved successfully!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_simplified_shop_button")
            ) {
                Text(
                    text = "✨ ✨ Save Simplified Shop & Owner Details",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            ApexFooter()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
