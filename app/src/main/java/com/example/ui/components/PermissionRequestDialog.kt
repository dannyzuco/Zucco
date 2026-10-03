package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LeafGreenContainer
import com.example.ui.theme.LightSlate
import com.example.ui.theme.MilkBackground
import com.example.ui.theme.MilkSurface
import com.example.ui.theme.MilkWhite
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.ZuccoGold
import com.example.ui.theme.ZuccoGoldDark

@Composable
fun PermissionRequestDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    hasLocationPermission: Boolean,
    hasNotificationPermission: Boolean,
    onRequestPermissions: () -> Unit,
    onOpenSettings: () -> Unit,
    onSendTestNotification: (() -> Unit)? = null
) {
    if (!showDialog) return

    val bothGranted = hasLocationPermission && hasNotificationPermission

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, ZuccoGold.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .testTag("permission_request_dialog"),
            color = MilkSurface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ZuccoGold.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Permissions",
                            tint = ZuccoGoldDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_permission_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = LightSlate
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (bothGranted) "Access Permissions Active" else "Enable Location & Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlack,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (bothGranted)
                        "Your location and notification settings are configured for the best artisan discovery experience on Zucco."
                    else
                        "Allow Zucco to find verified artisans closest to you and send real-time booking status alerts.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightSlate,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Location Access Card
                PermissionItemCard(
                    icon = Icons.Default.LocationOn,
                    iconTint = LeafGreen,
                    iconBg = LeafGreenContainer,
                    title = "Location Access",
                    description = "Pinpoint nearby plumbers, electricians, mechanics & workshops in Sango Ota, Lagos & Ogun with exact travel distance.",
                    isGranted = hasLocationPermission,
                    testTag = "location_permission_card"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Notification Access Card
                PermissionItemCard(
                    icon = Icons.Default.NotificationsActive,
                    iconTint = SkyBlue,
                    iconBg = SkyBlueContainer,
                    title = "Notification Alerts",
                    description = "Get instant updates when an artisan accepts your job, gives an arrival time, or updates service milestones.",
                    isGranted = hasNotificationPermission,
                    testTag = "notification_permission_card"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                if (!bothGranted) {
                    Button(
                        onClick = {
                            onRequestPermissions()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("grant_permissions_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZuccoGold,
                            contentColor = DarkBlack
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Grant Access Permissions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.testTag("open_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = LightSlate,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "App Settings",
                                color = LightSlate,
                                fontSize = 13.sp
                            )
                        }

                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("dismiss_permissions_button")
                        ) {
                            Text(
                                text = "Maybe Later",
                                color = LightSlate,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    // Both granted
                    if (onSendTestNotification != null) {
                        OutlinedButton(
                            onClick = onSendTestNotification,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("send_test_notification_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = SkyBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Send Test Notification",
                                color = NavyBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dismiss_permissions_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LeafGreen,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "All Set • Continue Exploring",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionItemCard(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    description: String,
    isGranted: Boolean,
    testTag: String
) {
    Surface(
        color = MilkBackground,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isGranted) LeafGreen.copy(alpha = 0.5f) else Color(0xFFE2DBCF)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkBlack
                    )

                    Surface(
                        color = if (isGranted) LeafGreen.copy(alpha = 0.15f) else ZuccoGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            if (isGranted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = LeafGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = if (isGranted) "Granted" else "Required",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGranted) LeafGreen else ZuccoGoldDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = LightSlate,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun PermissionBanner(
    hasLocationPermission: Boolean,
    hasNotificationPermission: Boolean,
    onRequestClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (hasLocationPermission && hasNotificationPermission) return

    val missingLabel = when {
        !hasLocationPermission && !hasNotificationPermission -> "Location & Notifications"
        !hasLocationPermission -> "Location Access"
        else -> "Notification Alerts"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("permission_banner"),
        shape = RoundedCornerShape(14.dp),
        color = MilkSurface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, ZuccoGold.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ZuccoGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (!hasLocationPermission) Icons.Default.LocationOn else Icons.Default.NotificationsActive,
                        contentDescription = "Permission Alert",
                        tint = ZuccoGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Enable $missingLabel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DarkBlack
                    )
                    Text(
                        text = "Discover nearby artisans & receive booking updates.",
                        fontSize = 11.sp,
                        color = LightSlate,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onRequestClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZuccoGold,
                        contentColor = DarkBlack
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("banner_enable_button")
                ) {
                    Text(
                        text = "Enable",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismissClick,
                    modifier = Modifier
                        .size(28.dp)
                        .padding(start = 4.dp)
                        .testTag("banner_dismiss_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = LightSlate,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
