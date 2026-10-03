package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.LocationArea
import com.example.data.model.LocationCatalog
import com.example.data.model.ReviewEntity
import com.example.data.model.ServiceProviderEntity
import com.example.data.model.UserEntity
import com.example.util.TradeBannerImage
import java.util.Locale
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDetailScreen(
    provider: ServiceProviderEntity,
    reviews: List<ReviewEntity>,
    currentUser: UserEntity?,
    userLocation: LocationArea,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
    onRequestBooking: (description: String, scheduledDate: String, urgency: String) -> Unit,
    onSubmitReview: (rating: Float, comment: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val photos = provider.photoList

    var showBookingDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    val distanceKm = LocationCatalog.calculateDistanceKm(
        userLocation.latitude,
        userLocation.longitude,
        provider.latitude,
        provider.longitude
    )
    val formattedDistance = LocationCatalog.formatDistance(distanceKm)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = provider.businessName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PureWhite
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PureWhite
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier.testTag("detail_save_button")
                    ) {
                        Icon(
                            imageVector = if (provider.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (provider.isSaved) "Remove from favorites" else "Save to favorites",
                            tint = if (provider.isSaved) Color(0xFFFBBF24) else PureWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        bottomBar = {
            // Persistent bottom contact & booking bar
            Surface(
                color = PureWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${provider.phoneNumber}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(NavyBlue, RoundedCornerShape(12.dp))
                            .testTag("detail_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            tint = PureWhite
                        )
                    }

                    IconButton(
                        onClick = {
                            val cleanNumber = provider.whatsappNumber.replace("+", "").replace(" ", "")
                            val url = "https://wa.me/$cleanNumber?text=Hello%20${provider.providerName},%20I%20am%20interested%20in%20your%20services%20on%20Zucco"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(LeafGreen, RoundedCornerShape(12.dp))
                            .testTag("detail_whatsapp_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = "WhatsApp",
                            tint = PureWhite
                        )
                    }

                    Button(
                        onClick = { showBookingDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("request_service_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Request Service",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PureWhite
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Portfolio Gallery Header (Displays up to 3 photos max or Contextual Trade Fallback Banner)
            if (photos.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        photos.forEachIndexed { idx, photoUri ->
                            Box(
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(NavyBlue)
                            ) {
                                TradeBannerImage(
                                    photoUrl = photoUri,
                                    category = provider.serviceCategory,
                                    contentDescription = "Portfolio sample ${idx + 1}",
                                    modifier = Modifier.fillMaxSize()
                                )

                                Surface(
                                    color = DarkBlack.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Photo ${idx + 1} of ${photos.size}",
                                        color = PureWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Work Portfolio (${photos.size} of 3 photos uploaded)",
                        style = MaterialTheme.typography.labelSmall,
                        color = LightSlate,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                // High-quality contextual trade fallback banner for accounts without custom uploads
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NavyBlue)
                    ) {
                        TradeBannerImage(
                            photoUrl = null,
                            category = provider.serviceCategory,
                            contentDescription = "${provider.serviceCategory} showcase",
                            modifier = Modifier.fillMaxSize()
                        )

                        Surface(
                            color = DarkBlack.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Verified ${provider.serviceCategory} Trade Showcase",
                                color = PureWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Custom Theme Accent Color Resolution
            val themeAccentColor = when (provider.shopThemeColor) {
                "Emerald" -> Color(0xFF059669)
                "SkyBlue" -> Color(0xFF0284C7)
                "SunsetOrange" -> Color(0xFFEA580C)
                "MidnightPurple" -> Color(0xFF7C3AED)
                "ClassicNavy" -> NavyBlue
                else -> Color(0xFFD97706) // Gold
            }

            // Core Profile Card with Custom Theme Header & Tagline
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, themeAccentColor.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = provider.businessName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyBlue
                                )
                                if (provider.isVerified) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = LeafGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Shop Tagline / Slogan
                            if (!provider.shopTagline.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "✨ ${provider.shopTagline}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = themeAccentColor
                                )
                            }

                            Text(
                                text = "Contact Artisan: ${provider.providerName}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlack
                            )
                        }

                        // Availability badge
                        Surface(
                            color = if (provider.isAvailable) LeafGreenContainer else SoftGray,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (provider.isAvailable) "Available" else "Busy",
                                color = if (provider.isAvailable) LeafGreen else LightSlate,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Specialty Badges / Trust Badges
                    val badges = provider.specialtyBadges?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
                    if (badges.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            badges.forEach { badge ->
                                Surface(
                                    color = themeAccentColor.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, themeAccentColor.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = badge,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = themeAccentColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Trade & Experience & Rating row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = SkyBlueContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = provider.serviceCategory,
                                color = NavyBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = SoftGray,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${provider.safeExperienceYears} Years Experience",
                                color = DarkBlack,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${provider.formattedRating} (${provider.reviewCount})",
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderGray)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Location & Distance details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = if (provider.city == "Ota") LeafGreen else SkyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "${provider.district}, ${provider.city}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DarkBlack
                            )
                            Text(
                                text = provider.address,
                                fontSize = 12.sp,
                                color = LightSlate
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val mapsTargetUrl = provider.googleMapLink?.ifBlank { null }
                        ?: "https://www.google.com/maps/search/?api=1&query=${Uri.encode("${provider.businessName}, ${provider.address}, ${provider.district}, ${provider.city}")}"

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsTargetUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("detail_google_maps_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Google Maps",
                            tint = PureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (provider.googleMapLink.isNullOrBlank()) "📍 Open Location in Google Maps" else "📍 Open Verified Google Maps Pin",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PureWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = LeafGreenContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                tint = LeafGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Proximity: $formattedDistance away from your selected location (${userLocation.name})",
                                color = LeafGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderGray)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Pricing
                    Text(
                        text = "Estimated Rates / Pricing",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DarkBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = provider.pricingInfo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LeafGreen
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderGray)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Description / Bio
                    Text(
                        text = "About This Service Provider",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = provider.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkBlack,
                        lineHeight = 20.sp
                    )
                }
            }

            // ========================================================
            // 👤 MEET THE BUSINESS OWNER / STORY CARD
            // ========================================================
            val displayOwnerName = provider.ownerName ?: provider.providerName
            val displayOwnerTitle = provider.ownerTitle ?: "Lead Master Craftsman & Founder"
            val displayOwnerBio = provider.ownerBio ?: provider.description
            val displayOwnerMotto = provider.ownerMotto

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, themeAccentColor.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(themeAccentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayOwnerName.take(1).uppercase(),
                                color = PureWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = displayOwnerName,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = NavyBlue
                            )
                            Text(
                                text = displayOwnerTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeAccentColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = displayOwnerBio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkBlack,
                        lineHeight = 20.sp
                    )

                    if (!displayOwnerMotto.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = themeAccentColor.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, themeAccentColor.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "“",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeAccentColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = displayOwnerMotto,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NavyBlue,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Customer Reviews Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Client Reviews (${reviews.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue
                        )

                        TextButton(
                            onClick = { showReviewDialog = true },
                            modifier = Modifier.testTag("write_review_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = SkyBlue
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rate & Review", color = SkyBlue, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (reviews.isEmpty()) {
                        Text(
                            text = "No reviews yet. Be the first client in Ota or Lagos to leave a review!",
                            style = MaterialTheme.typography.bodySmall,
                            color = LightSlate,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        reviews.forEach { rev ->
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = rev.clientName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DarkBlack
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = String.format(Locale.US, "%.1f", rev.rating),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = rev.date,
                                            fontSize = 11.sp,
                                            color = LightSlate
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = rev.comment,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = BorderGray.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Request Service / Booking Dialog
    if (showBookingDialog) {
        var issueDesc by remember { mutableStateOf("") }
        var preferredDate by remember { mutableStateOf("Tomorrow morning") }
        var urgency by remember { mutableStateOf("Standard") }

        AlertDialog(
            onDismissRequest = { showBookingDialog = false },
            title = {
                Text(
                    text = "Request Service from ${provider.businessName}",
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Describe the job or fault you need fixed:",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightSlate
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = issueDesc,
                        onValueChange = { issueDesc = it },
                        placeholder = { Text("e.g. Electrical wiring tripping in our 2-bedroom flat in Sango Ota...") },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkBlack,
                            unfocusedTextColor = DarkBlack,
                            cursorColor = NavyBlue,
                            focusedBorderColor = SkyBlue,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite,
                            focusedPlaceholderColor = LightSlate,
                            unfocusedPlaceholderColor = LightSlate
                        ),
                        textStyle = TextStyle(color = DarkBlack, fontSize = 14.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_issue_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Preferred Date / Time:",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightSlate
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = preferredDate,
                        onValueChange = { preferredDate = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkBlack,
                            unfocusedTextColor = DarkBlack,
                            cursorColor = NavyBlue,
                            focusedBorderColor = SkyBlue,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite,
                            focusedPlaceholderColor = LightSlate,
                            unfocusedPlaceholderColor = LightSlate
                        ),
                        textStyle = TextStyle(color = DarkBlack, fontSize = 14.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_date_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Urgency:",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightSlate
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Standard", "Urgent / Emergency", "Weekend").forEach { opt ->
                            OutlinedButton(
                                onClick = { urgency = opt },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (urgency == opt) LeafGreenContainer else Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 11.sp,
                                    color = if (urgency == opt) LeafGreen else DarkBlack,
                                    fontWeight = if (urgency == opt) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (issueDesc.isNotBlank()) {
                            onRequestBooking(issueDesc, preferredDate, urgency)
                            showBookingDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    modifier = Modifier.testTag("submit_booking_button")
                ) {
                    Text("Send Request", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Rate & Review Dialog
    if (showReviewDialog) {
        var rating by remember { mutableFloatStateOf(5.0f) }
        var comment by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = {
                Text("Write Review for ${provider.businessName}", fontWeight = FontWeight.Bold, color = NavyBlue)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select Rating: ${String.format("%.1f", rating)} Stars", fontWeight = FontWeight.Bold)
                    Slider(
                        value = rating,
                        onValueChange = { rating = it },
                        valueRange = 1.0f..5.0f,
                        steps = 7,
                        modifier = Modifier.testTag("rating_slider")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        placeholder = { Text("Share your experience with this artisan...") },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DarkBlack,
                            unfocusedTextColor = DarkBlack,
                            cursorColor = NavyBlue,
                            focusedBorderColor = SkyBlue,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite,
                            focusedPlaceholderColor = LightSlate,
                            unfocusedPlaceholderColor = LightSlate
                        ),
                        textStyle = TextStyle(color = DarkBlack, fontSize = 14.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_comment_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (comment.isNotBlank()) {
                            onSubmitReview(rating, comment)
                            showReviewDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                    modifier = Modifier.testTag("submit_review_button")
                ) {
                    Text("Post Review", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
