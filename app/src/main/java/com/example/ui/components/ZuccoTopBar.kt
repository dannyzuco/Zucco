package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.MilkWhite
import com.example.ui.theme.ZuccoGold
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationArea
import com.example.data.model.LocationCatalog
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.BorderGray
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenContainer
import com.example.ui.theme.LightSlate
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightElevated
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.ZuccoGold
import com.example.ui.theme.ZuccoGoldDark
import com.example.ui.theme.ZuccoGoldLight

@Composable
fun ZuccoTopBar(
    currentLocation: LocationArea,
    currentUser: UserEntity?,
    hasLocationPermission: Boolean = false,
    hasNotificationPermission: Boolean = false,
    onLocationChange: (LocationArea) -> Unit,
    onDetectLocationClick: () -> Unit = {},
    onOpenPermissionDialog: () -> Unit = {},
    onAuthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var locationMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = MidnightDark,
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sleek Brand Logo & Title (Transparent mark on midnight dark background)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("brand_title_row")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_zucco_logo_transparent),
                        contentDescription = "Zucco Logo Mark",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "zucco",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = ZuccoGold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(LeafGreen)
                            )
                        }
                        Text(
                            text = "Ota & Lagos Artisans",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = LightSlate
                        )
                    }
                }

                // Actions: Notification/Permissions button + User account badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Notification / Permission Bell Icon
                    Surface(
                        color = if (hasLocationPermission && hasNotificationPermission) SkyBlue.copy(alpha = 0.2f) else ZuccoGold.copy(alpha = 0.25f),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onOpenPermissionDialog() }
                            .testTag("notification_permission_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (hasNotificationPermission) Icons.Default.Notifications else Icons.Default.NotificationsActive,
                                contentDescription = "Alerts & Permissions",
                                tint = if (hasLocationPermission && hasNotificationPermission) PureWhite else ZuccoGold,
                                modifier = Modifier.size(19.dp)
                            )
                            if (!hasLocationPermission || !hasNotificationPermission) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .align(Alignment.TopEnd)
                                        .padding(1.dp)
                                        .clip(CircleShape)
                                        .background(ZuccoGoldDark)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // User account badge / login button
                    Surface(
                        color = if (currentUser != null) SkyBlue.copy(alpha = 0.2f) else LeafGreen,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onAuthClick() }
                            .testTag("auth_action_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Profile",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentUser != null) {
                                    if (currentUser.role == UserRole.PROVIDER) {
                                        val handle = if (!currentUser.username.isNullOrBlank()) "@${currentUser.username.removePrefix("@")}" else currentUser.fullName.split(" ").first()
                                        "Artisan: $handle"
                                    } else currentUser.fullName.split(" ").first()
                                } else "Sign In",
                                color = PureWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Location Bar with quick switcher
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = MidnightCard,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { locationMenuExpanded = true }
                        .testTag("location_selector_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = ZuccoGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Searching near: ",
                                color = PureWhite.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${currentLocation.name} (${currentLocation.city})",
                                color = ZuccoGoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Change",
                                color = ZuccoGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = ZuccoGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = locationMenuExpanded,
                    onDismissRequest = { locationMenuExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(MidnightElevated)
                        .border(1.dp, MidnightCardBorder, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "LOCATION & DEVICE GPS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZuccoGold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // GPS Auto-detect option
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Device GPS",
                                tint = ZuccoGold,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        text = {
                            Column {
                                Text(
                                    text = "Auto-Detect My GPS Location",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = PureWhite
                                )
                                Text(
                                    text = if (hasLocationPermission) "Detect exact position in Ota / Lagos" else "Request location permission & locate",
                                    fontSize = 11.sp,
                                    color = LightSlate
                                )
                            }
                        },
                        onClick = {
                            locationMenuExpanded = false
                            onDetectLocationClick()
                        },
                        modifier = Modifier.testTag("location_option_gps")
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MidnightCardBorder
                    )

                    Text(
                        text = "SELECT SPECIFIC AREA IN OTA / LAGOS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZuccoGold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    LocationCatalog.predefinedLocations.forEach { loc ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = loc.name,
                                            fontWeight = if (loc.name == currentLocation.name) FontWeight.Bold else FontWeight.Normal,
                                            color = if (loc.name == currentLocation.name) ZuccoGold else PureWhite
                                        )
                                        Text(
                                            text = "${loc.city}, ${loc.state} State",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (loc.city == "Ota") {
                                        Surface(
                                            color = LeafGreenContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "OTA",
                                                color = LeafGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            color = SkyBlueContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "LAGOS",
                                                color = SkyBlue,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            onClick = {
                                onLocationChange(loc)
                                locationMenuExpanded = false
                            },
                            modifier = Modifier.testTag("location_option_${loc.name.replace(" ", "_")}")
                        )
                    }
                }
            }
        }
    }
}
