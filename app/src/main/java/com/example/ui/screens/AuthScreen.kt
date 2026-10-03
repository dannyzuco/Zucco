package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.LocationCatalog
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.ApexDigitalHubBadge
import com.example.ui.components.ApexFooter
import com.example.ui.components.AreasCoveredSelector
import com.example.ui.components.PhotoUploadSlots
import com.example.ui.components.ProfileDpUploadSlot
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenContainer
import com.example.ui.theme.LightSlate
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightInputBg
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.SoftGray
import com.example.ui.theme.ZuccoAppBackground
import com.example.ui.theme.ZuccoGold
import com.example.ui.theme.ZuccoGoldContainer

@Composable
fun zuccoInputFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = PureWhite,
    unfocusedTextColor = PureWhite,
    focusedContainerColor = MidnightInputBg,
    unfocusedContainerColor = MidnightInputBg,
    disabledContainerColor = MidnightInputBg,
    disabledTextColor = LightSlate,
    cursorColor = ZuccoGold,
    focusedBorderColor = ZuccoGold,
    unfocusedBorderColor = MidnightCardBorder,
    focusedLabelColor = ZuccoGold,
    unfocusedLabelColor = LightSlate,
    focusedPlaceholderColor = LightSlate,
    unfocusedPlaceholderColor = LightSlate,
    focusedLeadingIconColor = ZuccoGold,
    unfocusedLeadingIconColor = LightSlate,
    focusedTrailingIconColor = ZuccoGold,
    unfocusedTrailingIconColor = LightSlate,
    focusedPrefixColor = ZuccoGold,
    unfocusedPrefixColor = LightSlate,
    focusedSupportingTextColor = LightSlate,
    unfocusedSupportingTextColor = LightSlate
)

val zuccoInputTextStyle = TextStyle(
    color = PureWhite,
    fontSize = 15.sp,
    fontWeight = FontWeight.Medium
)

object AuthConfig {
    const val ENABLE_DEMO_LOGIN_PANEL: Boolean = false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onLogin: (identifier: String, pass: String, (Boolean, String) -> Unit) -> Unit,
    onRegisterClient: (name: String, email: String, phone: String, pass: String, city: String, district: String, dp: String?, (Boolean, String) -> Unit) -> Unit,
    onRegisterProvider: (
        name: String,
        email: String,
        phone: String,
        whatsapp: String,
        pass: String,
        username: String,
        bizName: String,
        cat: String,
        city: String,
        district: String,
        address: String,
        desc: String,
        pricing: String,
        years: Int,
        bizReg: String?,
        bizEmail: String?,
        openingHours: String,
        servicesOffered: String?,
        areasCovered: String?,
        mapLink: String?,
        dp: String?,
        p1: String?,
        p2: String?,
        p3: String?,
        (Boolean, String) -> Unit
    ) -> Unit,
    onQuickDemoLogin: (email: String) -> Unit,
    onSendPasswordReset: (email: String, (Boolean, String) -> Unit) -> Unit = { _, _ -> },
    onResendVerification: (email: String, pass: String, (Boolean, String) -> Unit) -> Unit = { _, _, _ -> },
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Register
    var selectedRole by remember { mutableStateOf(UserRole.CLIENT) } // Client vs Provider

    // Form inputs
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var signInPasswordVisible by remember { mutableStateOf(false) }
    var registerPasswordVisible by remember { mutableStateOf(false) }
    var fullNameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var whatsappInput by remember { mutableStateOf("") }
    var citySelection by remember { mutableStateOf("Ota") }
    var districtSelection by remember { mutableStateOf("Sango Ota") }

    // Provider specific fields
    var usernameInput by remember { mutableStateOf("") }
    var businessNameInput by remember { mutableStateOf("") }
    var categorySelection by remember { mutableStateOf("Electrician") }
    var addressInput by remember { mutableStateOf("") }
    var googleMapLinkInput by remember { mutableStateOf("") }
    var pricingInput by remember { mutableStateOf("From ₦5,000 / inspection") }
    var descriptionInput by remember { mutableStateOf("") }
    var yearsInput by remember { mutableStateOf("5") }
    var businessRegNoInput by remember { mutableStateOf("") }
    var businessEmailInput by remember { mutableStateOf("") }
    var openingHoursInput by remember { mutableStateOf("Mon - Sat: 8:00 AM - 6:00 PM") }
    var servicesOfferedInput by remember { mutableStateOf("") }
    var profileDpInput by remember { mutableStateOf<String?>(null) }
    val selectedAreasCovered = remember { mutableStateListOf<String>() }

    // Provider work photos: strictly maximum 3 photos
    val photosList = remember { mutableStateListOf<String?>(null, null, null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var unverifiedEmailError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var resendLoading by remember { mutableStateOf(false) }

    // Password Reset Modal State
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmailInput by remember { mutableStateOf("") }
    var forgotPasswordLoading by remember { mutableStateOf(false) }
    var forgotPasswordError by remember { mutableStateOf<String?>(null) }
    var showResetSuccessDialog by remember { mutableStateOf(false) }

    // Email Verification Gate Modal State
    var showRegistrationSuccessDialog by remember { mutableStateOf(false) }
    var registeredEmailForVerification by remember { mutableStateOf("") }

    var categoryDropdownOpen by remember { mutableStateOf(false) }
    var districtDropdownOpen by remember { mutableStateOf(false) }

    // Helper: Automatically clears all text fields, inline field errors, and top error banners upon tab switch
    val clearAllInputsAndErrors = {
        emailInput = ""
        passwordInput = ""
        fullNameInput = ""
        phoneInput = ""
        whatsappInput = ""
        usernameInput = ""
        businessNameInput = ""
        addressInput = ""
        googleMapLinkInput = ""
        descriptionInput = ""
        businessRegNoInput = ""
        businessEmailInput = ""
        servicesOfferedInput = ""
        profileDpInput = null
        photosList[0] = null
        photosList[1] = null
        photosList[2] = null
        selectedAreasCovered.clear()
        errorMessage = null
        successMessage = null
        unverifiedEmailError = null
    }

    // Available categories
    val categories = LocationCatalog.categories.filter { it != "All Services" }

    // Districts by city
    val currentDistricts = if (citySelection == "Ota") {
        listOf("Sango Ota", "Toll Gate Ota", "KM 10 / Canaanland", "Bells / Idiroko Rd", "Iyana Iyesi Ota", "Oju Ore Ota")
    } else {
        listOf("Ikeja", "Lekki Phase 1", "Yaba", "Victoria Island", "Surulere")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (currentUser != null) "Account & Network" else "Welcome to Zucco",
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                },
                navigationIcon = {
                    if (currentUser != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("auth_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PureWhite
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("auth_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings & Privacy",
                            tint = ZuccoGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
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
            // Prominent Brand Logo Header (Secret 5-Tap Target for Admin)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clip(RoundedCornerShape(16.dp))
                            .testTag("brand_logo_icon")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_zucco_logo_transparent
                                    ),
                                    contentDescription = "Zucco Logo",
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "zucco",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ZuccoGold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nigeria's Trusted Artisan Network • Ota & Lagos Hub",
                                style = MaterialTheme.typography.bodySmall,
                                color = LightSlate
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    ApexDigitalHubBadge()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Session Info Card (When User is Logged In)
            currentUser?.let { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (user.role == UserRole.PROVIDER) LeafGreen else ZuccoGold,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = user.fullName.take(1).uppercase(),
                                            color = ZuccoAppBackground,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = user.fullName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                    Text(
                                        text = "${user.email} • ${user.role.name}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LightSlate
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = onLogout,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("logout_button")
                            ) {
                                Text("Log Out", color = PureWhite, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MidnightCardBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = LeafGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${user.district}, ${user.city}",
                                    fontSize = 12.sp,
                                    color = LightSlate
                                )
                            }
                            if (user.role == UserRole.ADMIN) {
                                Surface(
                                    color = LeafGreenContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "⚡ System Administrator",
                                        color = LeafGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Demo Login Panel
            if (currentUser == null && AuthConfig.ENABLE_DEMO_LOGIN_PANEL) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = ZuccoGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1-Tap Quick Demo Login",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ZuccoGold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Test instant client and artisan experiences in Ota & Lagos:",
                            fontSize = 12.sp,
                            color = LightSlate
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    emailInput = "client@zucco.ng"
                                    passwordInput = "password"
                                    isLoading = true
                                    errorMessage = null
                                    unverifiedEmailError = null
                                    onLogin("client@zucco.ng", "password") { success, msg ->
                                        isLoading = false
                                        if (success) onBack() else errorMessage = msg
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quick_login_client")
                            ) {
                                Text("Client (Tayo)", fontSize = 11.sp, color = PureWhite)
                            }

                            Button(
                                onClick = {
                                    emailInput = "adeola@zucco.ng"
                                    passwordInput = "password"
                                    isLoading = true
                                    errorMessage = null
                                    unverifiedEmailError = null
                                    onLogin("adeola@zucco.ng", "password") { success, msg ->
                                        isLoading = false
                                        if (success) onBack() else errorMessage = msg
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quick_login_electrician")
                            ) {
                                Text("Adeola (Ota)", fontSize = 11.sp, color = PureWhite)
                            }

                            Button(
                                onClick = {
                                    emailInput = "kemi@zucco.ng"
                                    passwordInput = "password"
                                    isLoading = true
                                    errorMessage = null
                                    unverifiedEmailError = null
                                    onLogin("kemi@zucco.ng", "password") { success, msg ->
                                        isLoading = false
                                        if (success) onBack() else errorMessage = msg
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quick_login_tailor")
                            ) {
                                Text("Kemi (Lagos)", fontSize = 11.sp, color = ZuccoAppBackground, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

// Authentication Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (currentUser == null) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = MidnightCard,
                            contentColor = PureWhite,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = ZuccoGold
                                )
                            },
                            divider = { HorizontalDivider(color = MidnightCardBorder) }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = {
                                    if (selectedTab != 0) {
                                        selectedTab = 0
                                        clearAllInputsAndErrors()
                                    }
                                },
                                text = {
                                    Text(
                                        "Sign In / Log In",
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == 0) PureWhite else LightSlate
                                    )
                                },
                                modifier = Modifier.testTag("tab_sign_in")
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = {
                                    if (selectedTab != 1) {
                                        selectedTab = 1
                                        clearAllInputsAndErrors()
                                    }
                                },
                                text = {
                                    Text(
                                        "Register Account",
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == 1) PureWhite else LightSlate
                                    )
                                },
                                modifier = Modifier.testTag("tab_register")
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = ZuccoGoldContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = ZuccoGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Onboarding Assistance Tool",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                                Text(
                                    text = "Register an artisan or client to join your verified network",
                                    fontSize = 12.sp,
                                    color = LightSlate
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Role Chooser (Client vs Service Provider)
                    Text(
                        text = if (currentUser != null) "Onboard as:" else if (selectedTab == 1) "I want to register as:" else "Account Type:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ZuccoGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = {
                                if (selectedRole != UserRole.CLIENT) {
                                    selectedRole = UserRole.CLIENT
                                    clearAllInputsAndErrors()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedRole == UserRole.CLIENT) NavyBlue else SoftGray,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (selectedRole == UserRole.CLIENT) ZuccoGold else MidnightCardBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("role_client_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (selectedRole == UserRole.CLIENT) ZuccoGold else LightSlate,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Client / Customer",
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedRole == UserRole.CLIENT) FontWeight.Bold else FontWeight.Normal,
                                    color = PureWhite
                                )
                            }
                        }

                        Surface(
                            onClick = {
                                if (selectedRole != UserRole.PROVIDER) {
                                    selectedRole = UserRole.PROVIDER
                                    clearAllInputsAndErrors()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedRole == UserRole.PROVIDER) NavyBlue else SoftGray,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (selectedRole == UserRole.PROVIDER) LeafGreen else MidnightCardBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("role_provider_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = if (selectedRole == UserRole.PROVIDER) LeafGreen else LightSlate,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Service Provider",
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedRole == UserRole.PROVIDER) FontWeight.Bold else FontWeight.Normal,
                                    color = PureWhite
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Unverified Email Warning Banner
                    if (unverifiedEmailError != null) {
                        Surface(
                            color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ZuccoGold.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MarkEmailUnread,
                                        contentDescription = null,
                                        tint = ZuccoGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Email Verification Required",
                                        color = ZuccoGold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Your email address ($unverifiedEmailError) has not been verified yet. Please check your inbox for the verification link.",
                                    color = PureWhite,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        val pass = passwordInput.trim()
                                        if (pass.isBlank()) {
                                            errorMessage = "Please enter your password above to resend the verification email."
                                            return@Button
                                        }
                                        resendLoading = true
                                        onResendVerification(unverifiedEmailError!!, pass) { success, msg ->
                                            resendLoading = false
                                            if (success) {
                                                successMessage = "Verification email resent! Please check your inbox."
                                                unverifiedEmailError = null
                                            } else {
                                                errorMessage = msg
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("resend_verification_button")
                                ) {
                                    if (resendLoading) {
                                        CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(16.dp))
                                    } else {
                                        Icon(Icons.Default.Send, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Resend Verification Email", color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Error Message Display
                    if (errorMessage != null && unverifiedEmailError == null) {
                        Surface(
                            color = Color(0xFFEF4444).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = Color(0xFFF87171),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Success Message Display
                    if (successMessage != null) {
                        Surface(
                            color = LeafGreenContainer,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LeafGreen.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = successMessage!!,
                                color = LeafGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // ==========================================
                    // TAB 0: SIGN IN (EMAIL & PASSWORD)
                    // ==========================================
                    if (currentUser == null && selectedTab == 0) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                errorMessage = null
                                unverifiedEmailError = null
                            },
                            label = { Text("Email Address") },
                            placeholder = { Text("e.g. yourname@gmail.com") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = ZuccoGold)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            colors = zuccoInputFieldColors(),
                            textStyle = zuccoInputTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = {
                                passwordInput = it
                                errorMessage = null
                            },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = ZuccoGold)
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { signInPasswordVisible = !signInPasswordVisible },
                                    modifier = Modifier.testTag("toggle_signin_password_visibility")
                                ) {
                                    Icon(
                                        imageVector = if (signInPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (signInPasswordVisible) "Hide password" else "Show password",
                                        tint = if (signInPasswordVisible) ZuccoGold else LightSlate
                                    )
                                }
                            },
                            visualTransformation = if (signInPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = zuccoInputFieldColors(),
                            textStyle = zuccoInputTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input")
                        )

                        // "Forgot Password?" Action Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    val cleanEmail = emailInput.trim().lowercase()
                                    forgotPasswordEmailInput = cleanEmail
                                    forgotPasswordError = null
                                    showForgotPasswordDialog = true
                                },
                                modifier = Modifier.testTag("forgot_password_button")
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    color = ZuccoGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val cleanEmail = emailInput.trim().lowercase()
                                val cleanPass = passwordInput.trim()
                                if (cleanEmail.isBlank() || cleanPass.isBlank()) {
                                    errorMessage = "Please enter both your email address and password."
                                    return@Button
                                }
                                isLoading = true
                                errorMessage = null
                                unverifiedEmailError = null
                                onLogin(cleanEmail, cleanPass) { success, msg ->
                                    isLoading = false
                                    if (success) {
                                        successMessage = msg
                                        onBack()
                                    } else {
                                        if (msg.contains("not been verified", ignoreCase = true) || msg.contains("verification link", ignoreCase = true)) {
                                            unverifiedEmailError = cleanEmail
                                            errorMessage = msg
                                        } else {
                                            errorMessage = msg
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ZuccoGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_login_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = ZuccoAppBackground, modifier = Modifier.size(20.dp))
                            } else {
                                Text(
                                    text = if (selectedRole == UserRole.PROVIDER) "Sign In to Artisan Shop" else "Sign In to Zucco",
                                    color = ZuccoAppBackground,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                    // ==========================================
// TAB 1: REGISTRATION (FIREBASE AUTH & FIRESTORE)
// ==========================================
                    if (currentUser != null || selectedTab == 1) {
                        ProfileDpUploadSlot(
                            dpUri = profileDpInput,
                            onDpChanged = { profileDpInput = it },
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        OutlinedTextField(
                            value = fullNameInput,
                            onValueChange = {
                                fullNameInput = it
                                errorMessage = null
                            },
                            label = { Text("Your Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SkyBlue) },
                            singleLine = true,
                            colors = zuccoInputFieldColors(),
                            textStyle = zuccoInputTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_fullname_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                errorMessage = null
                            },
                            label = { Text("Email Address") },
                            placeholder = { Text("e.g. name@gmail.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SkyBlue) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            colors = zuccoInputFieldColors(),
                            textStyle = zuccoInputTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_email_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = {
                                phoneInput = it
                                errorMessage = null
                            },
                            label = { Text("Phone Number (Contact / Inquiries)") },
                            placeholder = { Text("08012345678") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = LeafGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = zuccoInputFieldColors(),
                            textStyle = zuccoInputTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_phone_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = {
                                passwordInput = it
                                errorMessage = null
                            },
                            label = { Text("Create Account Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SkyBlue) },
                            trailingIcon = {
                                IconButton(
                                    onClick = { registerPasswordVisible = !registerPasswordVisible },
                                    modifier = Modifier.testTag("toggle_register_password_visibility")
                                ) {
                                    Icon(
                                        imageVector = if (registerPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (registerPasswordVisible) "Hide password" else "Show password",
                                        tint = if (registerPasswordVisible) SkyBlue else LightSlate
                                    )
                                }
                            },
                            visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = zuccoInputFieldColors(),
                            textStyle = zuccoInputTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_password_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // City Selection Chips
                        Text(
                            text = "Operational City Hub *",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ZuccoGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Ota", "Lagos").forEach { city ->
                                FilterChip(
                                    selected = citySelection == city,
                                    onClick = {
                                        citySelection = city
                                        districtSelection = if (city == "Ota") "Sango Ota" else "Ikeja"
                                    },
                                    label = { Text(city, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ZuccoGold,
                                        selectedLabelColor = DarkBlack,
                                        containerColor = MidnightInputBg,
                                        labelColor = PureWhite
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // District Selection Dropdown
                        Text(
                            text = "District / Neighborhood *",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ZuccoGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                onClick = { districtDropdownOpen = true },
                                shape = RoundedCornerShape(8.dp),
                                color = MidnightInputBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
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
                                        text = districtSelection,
                                        color = PureWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.Default.LocationCity,
                                        contentDescription = null,
                                        tint = ZuccoGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = districtDropdownOpen,
                                onDismissRequest = { districtDropdownOpen = false },
                                modifier = Modifier.background(MidnightCard)
                            ) {
                                currentDistricts.forEach { dist ->
                                    DropdownMenuItem(
                                        text = { Text(dist, color = PureWhite) },
                                        onClick = {
                                            districtSelection = dist
                                            districtDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        // ==========================================
                        // PROVIDER SPECIFIC FIELDS
                        // ==========================================
                        if (selectedRole == UserRole.PROVIDER) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MidnightCardBorder)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Artisan Business & Shop Profile",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = LeafGreen
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = usernameInput,
                                onValueChange = {
                                    usernameInput = it.lowercase().replace(" ", "_")
                                    errorMessage = null
                                },
                                label = { Text("Unique Shop Username (@handle) *") },
                                prefix = { Text("@", color = LeafGreen) },
                                leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = LeafGreen) },
                                singleLine = true,
                                colors = zuccoInputFieldColors(),
                                textStyle = zuccoInputTextStyle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_username_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = businessNameInput,
                                onValueChange = {
                                    businessNameInput = it
                                    errorMessage = null
                                },
                                label = { Text("Business / Trade Name *") },
                                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = LeafGreen) },
                                singleLine = true,
                                colors = zuccoInputFieldColors(),
                                textStyle = zuccoInputTextStyle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_business_name_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Category Dropdown
                            Text(
                                text = "Primary Trade Category *",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = ZuccoGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    onClick = { categoryDropdownOpen = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MidnightInputBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
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
                                            text = categorySelection,
                                            color = PureWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Handyman,
                                            contentDescription = null,
                                            tint = LeafGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = categoryDropdownOpen,
                                    onDismissRequest = { categoryDropdownOpen = false },
                                    modifier = Modifier.background(MidnightCard)
                                ) {
                                    categories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat, color = PureWhite) },
                                            onClick = {
                                                categorySelection = cat
                                                categoryDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = whatsappInput,
                                onValueChange = {
                                    whatsappInput = it
                                    errorMessage = null
                                },
                                label = { Text("WhatsApp Number (for direct booking)") },
                                placeholder = { Text("e.g. 08120942172") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = LeafGreen) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                colors = zuccoInputFieldColors(),
                                textStyle = zuccoInputTextStyle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_whatsapp_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = addressInput,
                                onValueChange = {
                                    addressInput = it
                                    errorMessage = null
                                },
                                label = { Text("Workshop / Physical Address") },
                                placeholder = { Text("e.g. Shop 4, Sango Market Plaza, Ota") },
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = LeafGreen) },
                                singleLine = true,
                                colors = zuccoInputFieldColors(),
                                textStyle = zuccoInputTextStyle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_address_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = yearsInput,
                                    onValueChange = {
                                        yearsInput = it
                                        errorMessage = null
                                    },
                                    label = { Text("Years Exp.") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = zuccoInputFieldColors(),
                                    textStyle = zuccoInputTextStyle,
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .testTag("register_years_input")
                                )

                                OutlinedTextField(
                                    value = pricingInput,
                                    onValueChange = {
                                        pricingInput = it
                                        errorMessage = null
                                    },
                                    label = { Text("Inspection / Pricing info") },
                                    singleLine = true,
                                    colors = zuccoInputFieldColors(),
                                    textStyle = zuccoInputTextStyle,
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .testTag("register_pricing_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = businessRegNoInput,
                                onValueChange = {
                                    businessRegNoInput = it
                                    errorMessage = null
                                },
                                label = { Text("CAC / Business Reg. No (Optional)") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = LeafGreen) },
                                singleLine = true,
                                colors = zuccoInputFieldColors(),
                                textStyle = zuccoInputTextStyle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_cac_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = openingHoursInput,
                                onValueChange = {
                                    openingHoursInput = it
                                    errorMessage = null
                                },
                                label = { Text("Working / Opening Hours") },
                                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = LeafGreen) },
                                singleLine = true,
                                colors = zuccoInputFieldColors(),
                                textStyle = zuccoInputTextStyle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_hours_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = descriptionInput,
                                onValueChange = {
                                    descriptionInput = it
                                    errorMessage = null
                                },
                                label = { Text("Business Description & Specialties") },
                                placeholder = { Text("Describe your services, safety standards, and guarantees...") },
                                minLines = 3,
                                colors = zuccoInputFieldColors(),
                                textStyle = zuccoInputTextStyle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_description_input")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Areas Covered Selector
                            AreasCoveredSelector(
                                selectedAreas = selectedAreasCovered.toList(),
                                onAreaToggle = { area ->
                                    if (selectedAreasCovered.contains(area)) {
                                        selectedAreasCovered.remove(area)
                                    } else {
                                        selectedAreasCovered.add(area)
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Business profile verification badge
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = LeafGreenContainer.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = LeafGreen, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Your business profile will be instantly activated on Zucco.",
                                        fontSize = 11.sp,
                                        color = PureWhite
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Portfolio Photos (Max 3)
                            PhotoUploadSlots(
                                photos = photosList.toList(),
                                onPhotoChanged = { index, uri ->
                                    if (index in 0..2) {
                                        photosList[index] = uri
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Submit Registration Button
                        Button(
                            onClick = {
                                val cleanName = fullNameInput.trim()
                                val cleanEmail = emailInput.trim().lowercase()
                                val cleanPass = passwordInput.trim()
                                val cleanPhone = phoneInput.trim()

                                if (cleanName.isBlank() || cleanEmail.isBlank() || cleanPass.isBlank()) {
                                    errorMessage = "Please enter your name, email address, and password."
                                    return@Button
                                }

                                if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
                                    errorMessage = "Please enter a valid email address (e.g. name@gmail.com)."
                                    return@Button
                                }

                                if (cleanPass.length < 6) {
                                    errorMessage = "Password must be at least 6 characters long."
                                    return@Button
                                }

                                if (selectedRole == UserRole.PROVIDER && usernameInput.isBlank()) {
                                    errorMessage = "Please pick a unique username for your business."
                                    return@Button
                                }
                                errorMessage = null
                                isLoading = true

                                if (selectedRole == UserRole.CLIENT) {
                                    onRegisterClient(
                                        cleanName,
                                        cleanEmail,
                                        cleanPhone,
                                        cleanPass,
                                        citySelection,
                                        districtSelection,
                                        profileDpInput
                                    ) { success, msg ->
                                        isLoading = false
                                        if (success) {
                                            registeredEmailForVerification = cleanEmail
                                            showRegistrationSuccessDialog = true
                                        } else {
                                            errorMessage = msg
                                        }
                                    }
                                } else {
                                    val finalWhatsapp = if (whatsappInput.isNotBlank()) whatsappInput.trim() else cleanPhone
                                    val areasCoveredStr = if (selectedAreasCovered.isNotEmpty()) selectedAreasCovered.joinToString(", ") else "$districtSelection, $citySelection"
                                    onRegisterProvider(
                                        cleanName,
                                        cleanEmail,
                                        cleanPhone,
                                        finalWhatsapp,
                                        cleanPass,
                                        usernameInput.trim(),
                                        businessNameInput.ifBlank { "$cleanName Services" }.trim(),
                                        categorySelection,
                                        citySelection,
                                        districtSelection,
                                        addressInput.ifBlank { "$districtSelection, $citySelection" }.trim(),
                                        descriptionInput.ifBlank { "Experienced $categorySelection in $citySelection." }.trim(),
                                        pricingInput.trim(),
                                        yearsInput.toIntOrNull() ?: 5,
                                        businessRegNoInput.trim().ifEmpty { null },
                                        businessEmailInput.trim().ifEmpty { null },
                                        openingHoursInput.trim().ifEmpty { "Mon - Sat: 8:00 AM - 6:00 PM" },
                                        servicesOfferedInput.trim().ifEmpty { null },
                                        areasCoveredStr,
                                        googleMapLinkInput.trim().ifEmpty { null },
                                        profileDpInput,
                                        photosList.getOrNull(0),
                                        photosList.getOrNull(1),
                                        photosList.getOrNull(2)
                                    ) { success, msg ->
                                        isLoading = false
                                        if (success) {
                                            registeredEmailForVerification = cleanEmail
                                            showRegistrationSuccessDialog = true
                                        } else {
                                            errorMessage = msg
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedRole == UserRole.PROVIDER) LeafGreen else ZuccoGold
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_register_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = if (selectedRole == UserRole.PROVIDER) PureWhite else ZuccoAppBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = if (selectedRole == UserRole.PROVIDER) Icons.Default.Handyman else Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = if (selectedRole == UserRole.PROVIDER) PureWhite else ZuccoAppBackground,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Create New Account",
                                    color = if (selectedRole == UserRole.PROVIDER) PureWhite else ZuccoAppBackground,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ApexFooter()
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==========================================
        // FORGOT PASSWORD DIALOG
        // ==========================================
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = {
                    showForgotPasswordDialog = false
                    forgotPasswordError = null
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ZuccoGold,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "Reset Your Password",
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Enter your registered email address to receive a secure password reset link:",
                            fontSize = 13.sp,
                            color = LightSlate
                        )
                        OutlinedTextField(
                            value = forgotPasswordEmailInput,
                            onValueChange = {
                                forgotPasswordEmailInput = it
                                forgotPasswordError = null
                            },
                            label = { Text("Registered Email Address") },
                            placeholder = { Text("e.g. name@gmail.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ZuccoGold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            colors = zuccoInputFieldColors(),
                            textStyle = zuccoInputTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("forgot_password_email_input")
                        )
                        if (forgotPasswordError != null) {
                            Text(
                                text = forgotPasswordError!!,
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val cleanEmail = forgotPasswordEmailInput.trim().lowercase()
                            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                                forgotPasswordError = "Please enter your registered email address above to reset your password."
                                return@Button
                            }
                            forgotPasswordLoading = true
                            forgotPasswordError = null
                            onSendPasswordReset(cleanEmail) { success, msg ->
                                forgotPasswordLoading = false
                                if (success) {
                                    showForgotPasswordDialog = false
                                    showResetSuccessDialog = true
                                } else {
                                    forgotPasswordError = msg
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZuccoGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("submit_reset_password_button")
                    ) {
                        if (forgotPasswordLoading) {
                            CircularProgressIndicator(color = DarkBlack, modifier = Modifier.size(16.dp))
                        } else {
                            Text("Send Reset Link", color = DarkBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showForgotPasswordDialog = false
                            forgotPasswordError = null
                        }
                    ) {
                        Text("Cancel", color = LightSlate)
                    }
                },
                containerColor = DarkBlack,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // ==========================================
        // PASSWORD RESET SUCCESS DIALOG
        // ==========================================
        if (showResetSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showResetSuccessDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = LeafGreen,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "Password Reset Link Sent!",
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "We have sent a password reset link to $forgotPasswordEmailInput. Please check your email inbox (and spam folder) to reset your password.",
                        fontSize = 13.sp,
                        color = LightSlate,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showResetSuccessDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = ZuccoGold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Done", color = DarkBlack, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = DarkBlack,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // ==========================================
        // REGISTRATION EMAIL VERIFICATION SENT DIALOG
        // ==========================================
        if (showRegistrationSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showRegistrationSuccessDialog = false
                    selectedTab = 0
                    emailInput = registeredEmailForVerification
                    passwordInput = ""
                    errorMessage = null
                    successMessage = "Verification link sent! Please verify your email before logging in."
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = LeafGreen,
                        modifier = Modifier.size(40.dp)
                    )
                },
                title = {
                    Text(
                        text = "Verification Link Sent!",
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Registration successful! A verification link has been sent to your email address:",
                            fontSize = 13.sp,
                            color = LightSlate,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = registeredEmailForVerification,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZuccoGold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Please verify your email before logging in. Once verified, sign in with your email and password.",
                            fontSize = 12.sp,
                            color = LightSlate,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRegistrationSuccessDialog = false
                            selectedTab = 0
                            emailInput = registeredEmailForVerification
                            passwordInput = ""
                            errorMessage = null
                            successMessage = "Verification link sent! Check your inbox to verify, then sign in."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZuccoGold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Got It, Sign In", color = DarkBlack, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = DarkBlack,
                shape = RoundedCornerShape(16.dp)
            )
        }

    }
}