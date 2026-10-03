package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceProviderEntity
import com.example.ui.components.ApexFooter
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
fun ShopDesignerScreen(
    provider: ServiceProviderEntity?,
    onBack: () -> Unit,
    onSaveShopBrandingAndOwnerBio: (
        shopTagline: String?,
        shopThemeColor: String,
        shopBannerStyle: String,
        specialtyBadges: String?,
        ownerName: String?,
        ownerTitle: String?,
        ownerBio: String?,
        ownerMotto: String?
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    if (provider == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Shop Designer", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No service provider profile logged in.", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    // Editable States
    var shopTagline by remember(provider) { mutableStateOf(provider.shopTagline ?: "") }
    var shopThemeColor by remember(provider) { mutableStateOf(provider.shopThemeColor.ifBlank { "Gold" }) }
    var shopBannerStyle by remember(provider) { mutableStateOf(provider.shopBannerStyle.ifBlank { "Modern Gradient" }) }
    var selectedBadges by remember(provider) {
        mutableStateOf(
            provider.specialtyBadges?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }
                ?: listOf("⚡ Fast Response", "🏆 Top Rated", "🛡️ 100% Guaranteed")
        )
    }

    var ownerName by remember(provider) { mutableStateOf(provider.ownerName ?: provider.providerName) }
    var ownerTitle by remember(provider) { mutableStateOf(provider.ownerTitle ?: "Founder & Master Craftsman") }
    var ownerBio by remember(provider) { mutableStateOf(provider.ownerBio ?: provider.description) }
    var ownerMotto by remember(provider) { mutableStateOf(provider.ownerMotto ?: "Quality work is my signature.") }

    var saveMessage by remember { mutableStateOf<String?>(null) }

    // Color Theme Resolution
    val activeThemeColor = when (shopThemeColor) {
        "Emerald" -> Color(0xFF059669)
        "SkyBlue" -> Color(0xFF0284C7)
        "SunsetOrange" -> Color(0xFFEA580C)
        "MidnightPurple" -> Color(0xFF7C3AED)
        "ClassicNavy" -> NavyBlue
        else -> Color(0xFFD97706) // Gold
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🎨 Shop Designer Studio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = PureWhite
                        )
                        Text(
                            text = provider.businessName,
                            fontSize = 12.sp,
                            color = SkyBlueContainer
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PureWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ========================================================
            // 🚀 PRO FEATURE - COMING SOON BANNER / NOTICE
            // ========================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)), // Dark Indigo
                border = BorderStroke(1.5.dp, ZuccoGold),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = ZuccoGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ZUCCO PRO FEATURE",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = ZuccoGold
                            )
                        }

                        Surface(
                            color = ZuccoGold,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "COMING SOON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkBlack,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Customize your shop banner, profile theme, and owner story to stand out to premium clients. In future updates, custom shop styling will require a Zucco PRO Subscription.",
                        fontSize = 12.sp,
                        color = PureWhite,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color(0xFF312E81),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ZuccoGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🎁 FREE BETA ACCESS: Customize & Save your shop design for free!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SkyBlueContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ========================================================
            // 🎨 SECTION 1: SHOP BANNER & BRAND SLOGAN
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
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Brush, contentDescription = null, tint = NavyBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "1. Shop Banner & Slogan",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = NavyBlue
                            )
                            Text(
                                text = "Create a memorable tagline that appears under your business name",
                                fontSize = 11.sp,
                                color = LightSlate
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Shop Tagline / Slogan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBlack)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = shopTagline,
                        onValueChange = { shopTagline = it },
                        placeholder = { Text("e.g. Precision Electricals & Solar Inverter Fixes in Sango Ota") },
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
                            .testTag("shop_designer_tagline_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Banner Header Theme Style", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBlack)
                    Spacer(modifier = Modifier.height(6.dp))
                    val bannerStyles = listOf("Modern Gradient", "Gold Luxury", "Workshop Wood", "Tech Glow", "Minimalist Slate")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bannerStyles.forEach { style ->
                            val isSel = shopBannerStyle == style
                            Surface(
                                onClick = { shopBannerStyle = style },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) NavyBlue else SoftGray,
                                border = BorderStroke(1.dp, if (isSel) SkyBlue else BorderGray)
                            ) {
                                Text(
                                    text = style,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) PureWhite else DarkBlack,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ========================================================
            // 🌈 SECTION 2: COLOR THEME SELECTION
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
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = NavyBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "2. Profile Accent Color Theme",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = NavyBlue
                            )
                            Text(
                                text = "Choose the brand accent color for your shop card & detail page",
                                fontSize = 11.sp,
                                color = LightSlate
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val colorOptions = listOf(
                        "Gold" to Color(0xFFD97706),
                        "Emerald" to Color(0xFF059669),
                        "SkyBlue" to Color(0xFF0284C7),
                        "SunsetOrange" to Color(0xFFEA580C),
                        "MidnightPurple" to Color(0xFF7C3AED),
                        "ClassicNavy" to NavyBlue
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        colorOptions.chunked(2).forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                pair.forEach { (name, color) ->
                                    val isSelected = shopThemeColor.equals(name, ignoreCase = true)
                                    Surface(
                                        onClick = { shopThemeColor = name },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) color.copy(alpha = 0.12f) else SoftGray,
                                        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) color else BorderGray),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = name,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = DarkBlack,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (isSelected) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ========================================================
            // 👤 SECTION 3: ABOUT THE BUSINESS OWNER TEXT DESCRIPTION
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
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = LeafGreen, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "3. About the Business Owner",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = NavyBlue
                            )
                            Text(
                                text = "Personal story & bio saved directly to your business profile",
                                fontSize = 11.sp,
                                color = LightSlate
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Owner / Artisan Full Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBlack)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        placeholder = { Text("e.g. Engr. Adeola Balogun") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkBlack,
                            unfocusedTextColor = DarkBlack,
                            focusedBorderColor = LeafGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = SoftGray,
                            unfocusedContainerColor = SoftGray
                        ),
                        textStyle = TextStyle(fontSize = 13.sp, color = DarkBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_designer_owner_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Owner Role / Title", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBlack)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = ownerTitle,
                        onValueChange = { ownerTitle = it },
                        placeholder = { Text("e.g. Lead Master Electrician & Solar Installer") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkBlack,
                            unfocusedTextColor = DarkBlack,
                            focusedBorderColor = LeafGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = SoftGray,
                            unfocusedContainerColor = SoftGray
                        ),
                        textStyle = TextStyle(fontSize = 13.sp, color = DarkBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_designer_owner_title")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("About the Owner Description (Bio / Background Story)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBlack)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = ownerBio,
                        onValueChange = { ownerBio = it },
                        placeholder = { Text("Share your background, apprenticeship, experience, and why clients in Ota & Lagos trust your work...") },
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkBlack,
                            unfocusedTextColor = DarkBlack,
                            focusedBorderColor = LeafGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = SoftGray,
                            unfocusedContainerColor = SoftGray
                        ),
                        textStyle = TextStyle(fontSize = 13.sp, color = DarkBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_designer_owner_bio")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Owner Personal Motto / Quality Guarantee Quote", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBlack)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = ownerMotto,
                        onValueChange = { ownerMotto = it },
                        placeholder = { Text("e.g. Safety first, precision always.") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkBlack,
                            unfocusedTextColor = DarkBlack,
                            focusedBorderColor = LeafGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = SoftGray,
                            unfocusedContainerColor = SoftGray
                        ),
                        textStyle = TextStyle(fontSize = 13.sp, color = DarkBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_designer_owner_motto")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ========================================================
            // 🏷️ SECTION 4: TRUST & SPECIALTY BADGES
            // ========================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Trust Badges & Service Highlights",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select highlights that show up on your shop card",
                        fontSize = 11.sp,
                        color = LightSlate
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val availableBadges = listOf(
                        "⚡ Fast Response",
                        "🏆 Top Rated",
                        "🛡️ 100% Guaranteed",
                        "🚚 Emergency On-Call",
                        "💬 Free Consultation",
                        "☀️ Solar Specialist",
                        "👗 Custom Fitting",
                        "🪵 Solid Hardwood",
                        "🔧 Warranty Included"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableBadges.forEach { badge ->
                            val isSelected = selectedBadges.contains(badge)
                            Surface(
                                onClick = {
                                    selectedBadges = if (isSelected) {
                                        selectedBadges.filter { it != badge }
                                    } else {
                                        selectedBadges + badge
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) LeafGreenContainer else SoftGray,
                                border = BorderStroke(1.dp, if (isSelected) LeafGreen else BorderGray)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = badge,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) LeafGreen else DarkBlack
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Check, contentDescription = null, tint = LeafGreen, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ========================================================
            // 👁️ SECTION 5: LIVE CLIENT VIEW PREVIEW
            // ========================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBlue),
                border = BorderStroke(1.5.dp, activeThemeColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👁️ Live Client View Preview",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Simulated Card Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(activeThemeColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = provider.businessName.take(1).uppercase(),
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = provider.businessName,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (shopTagline.isNotBlank()) {
                                Text(
                                    text = "✨ $shopTagline",
                                    color = activeThemeColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    if (ownerBio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "About $ownerName ($ownerTitle):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = activeThemeColor
                                )
                                Text(
                                    text = ownerBio,
                                    fontSize = 11.sp,
                                    color = PureWhite,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Confirmation Banner
            if (saveMessage != null) {
                Surface(
                    color = LeafGreenContainer,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LeafGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = LeafGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = saveMessage!!,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LeafGreen
                        )
                    }
                }
            }

            // ========================================================
            // 💾 SAVE BUTTON (SAVING TO FIRESTORE BUSINESS DOCUMENT)
            // ========================================================
            Button(
                onClick = {
                    onSaveShopBrandingAndOwnerBio(
                        shopTagline.ifBlank { null },
                        shopThemeColor,
                        shopBannerStyle,
                        selectedBadges.joinToString(","),
                        ownerName.ifBlank { null },
                        ownerTitle.ifBlank { null },
                        ownerBio.ifBlank { null },
                        ownerMotto.ifBlank { null }
                    )
                    saveMessage = "✅ Saved successfully to your business profile!"
                },
                colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_shop_designer_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = PureWhite, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Shop Design",
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
