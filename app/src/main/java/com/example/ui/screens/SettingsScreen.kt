package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceProviderEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LightSlate
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ZuccoAppBackground
import com.example.ui.theme.ZuccoGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentUser: UserEntity?,
    currentProvider: ServiceProviderEntity?,
    onBack: () -> Unit,
    onToggleProviderVisibility: (Boolean) -> Unit = {},
    onChangePassword: (oldPass: String, newPass: String, (Boolean, String) -> Unit) -> Unit = { _, _, _ -> },
    onDeleteAccount: () -> Unit = {},
    onLogout: () -> Unit = {},
    onUpdateProfilePhoto: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val isArtisan = currentUser?.role == UserRole.PROVIDER || currentProvider != null

    val profilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdateProfilePhoto(uri.toString())
        }
    }

    // Setting State Toggles
    var jobAlertsEnabled by remember { mutableStateOf(true) }
    var bookingUpdatesEnabled by remember { mutableStateOf(true) }
    var smsWhatsappAlertsEnabled by remember { mutableStateOf(true) }
    var gpsAutoLocationEnabled by remember { mutableStateOf(true) }
    var isProfileVisible by remember { mutableStateOf(currentProvider?.isAvailable ?: true) }
    var biometricLoginEnabled by remember { mutableStateOf(false) }

    // Dialog & Modal States
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showSessionsDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    var selectedLanguage by remember { mutableStateOf("English") }
    val languages = listOf("English", "Nigerian Pidgin", "Yorùbá", "Hausa", "Igbo")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & Privacy",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PureWhite
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PureWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        containerColor = ZuccoAppBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Header Pill with Profile Photo Selector
            currentUser?.let { user ->
                val displayDp = user.profileDp ?: currentProvider?.profileDp
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightCard),
                    border = BorderStroke(1.dp, MidnightCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Avatar with Camera Icon Overlay
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .clickable {
                                    profilePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!displayDp.isNullOrBlank()) {
                                AsyncImage(
                                    model = displayDp,
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
                                        text = user.fullName.take(1).uppercase(),
                                        color = ZuccoGold,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp
                                    )
                                }
                            }
                            // Camera Overlay Badge
                            Surface(
                                color = LeafGreen,
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.BottomEnd)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Upload Profile Photo",
                                        tint = PureWhite,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.fullName,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = PureWhite
                            )
                            Text(
                                text = "${user.email} • ${user.role.name}",
                                fontSize = 12.sp,
                                color = LightSlate
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "📷 Tap photo to update profile picture",
                                fontSize = 11.sp,
                                color = ZuccoGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // ================= SECTION A: NOTIFICATIONS & ALERTS =================
            SettingsSectionHeader(title = "NOTIFICATIONS & ALERTS")

            SettingsGroupCard {
                if (isArtisan) {
                    SettingsToggleRow(
                        icon = Icons.Default.NotificationsActive,
                        iconTint = ZuccoGold,
                        title = "New Lead & Job Alerts",
                        subtitle = "Instant push notifications for job requests in Ota & Lagos",
                        checked = jobAlertsEnabled,
                        onCheckedChange = { jobAlertsEnabled = it },
                        testTag = "toggle_job_alerts"
                    )
                    SettingsDivider()
                }

                SettingsToggleRow(
                    icon = Icons.Default.Notifications,
                    iconTint = ZuccoGold,
                    title = "Booking & Chat Updates",
                    subtitle = "Real-time alerts for booking statuses, invoices & customer chats",
                    checked = bookingUpdatesEnabled,
                    onCheckedChange = { bookingUpdatesEnabled = it },
                    testTag = "toggle_booking_updates"
                )
                SettingsDivider()

                SettingsToggleRow(
                    icon = Icons.Default.Sms,
                    iconTint = ZuccoGold,
                    title = "SMS & WhatsApp Alerts",
                    subtitle = "Receive urgent notifications via SMS/WhatsApp when offline",
                    checked = smsWhatsappAlertsEnabled,
                    onCheckedChange = { smsWhatsappAlertsEnabled = it },
                    testTag = "toggle_sms_alerts"
                )
            }

            // ================= SECTION B: LOCATION & AVAILABILITY (PRIVACY) =================
            SettingsSectionHeader(title = "LOCATION & PRIVACY")

            SettingsGroupCard {
                SettingsToggleRow(
                    icon = Icons.Default.MyLocation,
                    iconTint = ZuccoGold,
                    title = "GPS Auto-Location",
                    subtitle = if (gpsAutoLocationEnabled) "Active • Real-time distance calculation (e.g., 1.9 km away)" else "Disabled • Using manual district selection (e.g., Sango Ota)",
                    checked = gpsAutoLocationEnabled,
                    onCheckedChange = { gpsAutoLocationEnabled = it },
                    testTag = "toggle_gps_location"
                )

                if (isArtisan) {
                    SettingsDivider()
                    SettingsToggleRow(
                        icon = if (isProfileVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        iconTint = if (isProfileVisible) LeafGreen else Color(0xFFEF4444),
                        title = if (isProfileVisible) "Shop Visibility: LIVE" else "Shop Visibility: PAUSED",
                        subtitle = if (isProfileVisible) "Clients can find and book your shop in search" else "Temporarily hidden from search while fully booked",
                        checked = isProfileVisible,
                        onCheckedChange = {
                            isProfileVisible = it
                            onToggleProviderVisibility(it)
                        },
                        testTag = "toggle_provider_visibility"
                    )
                }
            }

            // ================= SECTION C: SECURITY & ACCOUNT CONTROL =================
            SettingsSectionHeader(title = "SECURITY & ACCOUNT CONTROL")

            SettingsGroupCard {
                SettingsActionRow(
                    icon = Icons.Default.Lock,
                    iconTint = ZuccoGold,
                    title = "Change Password",
                    subtitle = "Update your account password or security PIN",
                    onClick = { showChangePasswordDialog = true },
                    testTag = "action_change_password"
                )
                SettingsDivider()

                SettingsToggleRow(
                    icon = Icons.Default.Fingerprint,
                    iconTint = ZuccoGold,
                    title = "Biometric Authentication",
                    subtitle = "Use Fingerprint or Face ID for fast login",
                    checked = biometricLoginEnabled,
                    onCheckedChange = { biometricLoginEnabled = it },
                    testTag = "toggle_biometrics"
                )
                SettingsDivider()

                SettingsActionRow(
                    icon = Icons.Default.Devices,
                    iconTint = ZuccoGold,
                    title = "Linked Devices & Sessions",
                    subtitle = "View active logins and manage authorized devices",
                    onClick = { showSessionsDialog = true },
                    testTag = "action_manage_sessions"
                )
            }

            // ================= SECTION D: APP PREFERENCES & SUPPORT =================
            SettingsSectionHeader(title = "APP PREFERENCES & SUPPORT")

            SettingsGroupCard {
                SettingsActionRow(
                    icon = Icons.Default.Language,
                    iconTint = ZuccoGold,
                    title = "App Language",
                    subtitle = "Current: $selectedLanguage",
                    onClick = { showLanguageDialog = true },
                    testTag = "action_language_select"
                )
                SettingsDivider()

                SettingsActionRow(
                    icon = Icons.AutoMirrored.Filled.Help,
                    iconTint = ZuccoGold,
                    title = "Help Center & WhatsApp Support",
                    subtitle = "Direct line to Zucco 24/7 customer care & FAQs",
                    onClick = {
                        val url = "https://wa.me/2348123456789?text=Hello%20Zucco%20Support,%20I%20need%20assistance"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    testTag = "action_help_support"
                )
                SettingsDivider()

                SettingsActionRow(
                    icon = Icons.Default.Policy,
                    iconTint = ZuccoGold,
                    title = "Terms of Service & Privacy Policy",
                    subtitle = "NDPR compliant artisan network policies & terms",
                    onClick = { showTermsDialog = true },
                    testTag = "action_terms_privacy"
                )
            }

            // ================= SECTION E: DANGER ZONE & COMPLIANCE (FOOTER) =================
            SettingsSectionHeader(title = "ACCOUNT & DATA COMPLIANCE", isDanger = true)

            SettingsGroupCard(borderColor = Color(0xFFEF4444).copy(alpha = 0.35f)) {
                SettingsActionRow(
                    icon = Icons.Default.DeleteForever,
                    iconTint = Color(0xFFEF4444),
                    title = "Delete Account & Data",
                    subtitle = "Permanently remove your profile, bookings, reviews & files",
                    titleColor = Color(0xFFEF4444),
                    onClick = { showDeleteAccountDialog = true },
                    testTag = "action_delete_account"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // App Version Tag
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Zucco v1.0.4 • Powered by Apex Digital Hub",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightSlate.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nigeria's Trusted Artisan Network • Ota & Lagos Hub",
                    style = MaterialTheme.typography.labelSmall,
                    color = LightSlate.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // ================= DIALOGS =================

    // 1. Change Password Dialog
    if (showChangePasswordDialog) {
        ChangePasswordModal(
            onDismiss = { showChangePasswordDialog = false },
            onSubmit = { oldPass, newPass, cb ->
                onChangePassword(oldPass, newPass, cb)
            }
        )
    }

    // 2. Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text("Select App Language", fontWeight = FontWeight.Bold, color = PureWhite)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    languages.forEach { lang ->
                        val isSelected = lang == selectedLanguage
                        Surface(
                            onClick = {
                                selectedLanguage = lang
                                showLanguageDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) NavyBlue else MidnightCard,
                            border = BorderStroke(1.dp, if (isSelected) ZuccoGold else MidnightCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lang,
                                    color = if (isSelected) ZuccoGold else PureWhite,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = ZuccoGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = ZuccoGold)
                }
            },
            containerColor = DarkBlack,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 3. Linked Sessions Dialog
    if (showSessionsDialog) {
        AlertDialog(
            onDismissRequest = { showSessionsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Devices, contentDescription = null, tint = ZuccoGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Active Sessions", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MidnightCard),
                        border = BorderStroke(1.dp, LeafGreen.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(LeafGreen)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Android Device • Current Session", fontWeight = FontWeight.Bold, color = PureWhite, fontSize = 13.sp)
                                Text("Last active: Just now • Ota/Lagos IP", fontSize = 11.sp, color = LightSlate)
                            }
                        }
                    }

                    Text(
                        text = "You are securely signed in on this device. If you notice any suspicious activity, you can sign out of all other sessions.",
                        fontSize = 12.sp,
                        color = LightSlate
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSessionsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSessionsDialog = false
                        onLogout()
                    }
                ) {
                    Text("Log Out of App", color = Color(0xFFEF4444))
                }
            },
            containerColor = DarkBlack,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 4. Terms & Privacy Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text("Terms & Privacy Policy", fontWeight = FontWeight.Bold, color = PureWhite)
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Nigeria Data Protection Regulation (NDPR) Compliance",
                        fontWeight = FontWeight.Bold,
                        color = ZuccoGold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "1. Information We Collect: Zucco collects registration details (name, phone number, business description, location) to connect clients with verified local artisans in Ota, Lagos, and surrounding communities.\n\n" +
                                "2. Direct Contact: Client phone numbers and addresses are only shared with service providers when a booking request is accepted.\n\n" +
                                "3. Location Privacy: GPS coordinates are used exclusively for calculating accurate proximity estimates ('X km away') and are never sold to third parties.\n\n" +
                                "4. Account Deletion: Users retain the right to permanently purge their account, reviews, and portfolio images at any time via Settings.",
                        color = LightSlate,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTermsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("I Understand")
                }
            },
            containerColor = DarkBlack,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 5. Account Deletion Modal (Play Store Policy Compliance)
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Permanently Delete Account?",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFEF4444),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "This action is irreversible. All your profile information, ratings, reviews, bookings, and uploaded photos will be permanently deleted from our servers immediately in accordance with data privacy regulations.",
                    fontSize = 13.sp,
                    color = LightSlate,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.testTag("confirm_delete_account_button")
                ) {
                    Text("Delete My Account", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteAccountDialog = false },
                    border = BorderStroke(1.dp, MidnightCardBorder)
                ) {
                    Text("Cancel", color = PureWhite)
                }
            },
            containerColor = DarkBlack,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ================= SUB-COMPONENTS =================

@Composable
private fun SettingsSectionHeader(title: String, isDanger: Boolean = false) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = if (isDanger) Color(0xFFEF4444) else ZuccoGold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, top = 6.dp)
    )
}

@Composable
private fun SettingsGroupCard(
    borderColor: Color = MidnightCardBorder,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(NavyBlue.copy(alpha = 0.8f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PureWhite
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = LightSlate,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PureWhite,
                checkedTrackColor = LeafGreen,
                uncheckedThumbColor = LightSlate,
                uncheckedTrackColor = DarkBlack
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    titleColor: Color = PureWhite,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(NavyBlue.copy(alpha = 0.8f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = LightSlate,
                lineHeight = 16.sp
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = LightSlate,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        color = MidnightCardBorder.copy(alpha = 0.6f),
        thickness = 0.8.dp,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun ChangePasswordModal(
    onDismiss: () -> Unit,
    onSubmit: (oldPass: String, newPass: String, (Boolean, String) -> Unit) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Change Password", fontWeight = FontWeight.Bold, color = PureWhite)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Current Password") },
                    visualTransformation = if (oldPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                            Icon(
                                imageVector = if (oldPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (oldPasswordVisible) "Hide password" else "Show password",
                                tint = if (oldPasswordVisible) ZuccoGold else LightSlate
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_current_password"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZuccoGold,
                        unfocusedBorderColor = MidnightCardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedLabelColor = ZuccoGold,
                        unfocusedLabelColor = LightSlate
                    )
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password (min 6 chars)") },
                    visualTransformation = if (newPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(
                                imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (newPasswordVisible) "Hide password" else "Show password",
                                tint = if (newPasswordVisible) ZuccoGold else LightSlate
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_new_password"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZuccoGold,
                        unfocusedBorderColor = MidnightCardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedLabelColor = ZuccoGold,
                        unfocusedLabelColor = LightSlate
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = if (confirmPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = if (confirmPasswordVisible) ZuccoGold else LightSlate
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_confirm_password"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZuccoGold,
                        unfocusedBorderColor = MidnightCardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedLabelColor = ZuccoGold,
                        unfocusedLabelColor = LightSlate
                    )
                )

                errorMessage?.let {
                    Text(text = it, color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                successMessage?.let {
                    Text(text = it, color = LeafGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    errorMessage = null
                    if (oldPassword.isBlank()) {
                        errorMessage = "Please enter your current password"
                        return@Button
                    }
                    if (newPassword.length < 6) {
                        errorMessage = "New password must be at least 6 characters"
                        return@Button
                    }
                    if (newPassword != confirmPassword) {
                        errorMessage = "New passwords do not match"
                        return@Button
                    }

                    isLoading = true
                    onSubmit(oldPassword, newPassword) { success, msg ->
                        isLoading = false
                        if (success) {
                            successMessage = msg
                            errorMessage = null
                        } else {
                            errorMessage = msg
                        }
                    }
                },
                enabled = !isLoading && successMessage == null,
                colors = ButtonDefaults.buttonColors(containerColor = ZuccoGold),
                modifier = Modifier.testTag("btn_submit_change_password")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = DarkBlack, modifier = Modifier.size(16.dp))
                } else {
                    Text("Save Password", color = DarkBlack, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (successMessage != null) "Done" else "Cancel", color = PureWhite)
            }
        },
        containerColor = DarkBlack,
        shape = RoundedCornerShape(16.dp)
    )
}
