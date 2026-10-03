package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LightSlate
import com.example.ui.theme.MidnightInputBg
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.ZuccoGold

@Composable
fun PhotoUploadSlots(
    photos: List<String?>, // Exactly 3 elements [p1, p2, p3]
    onPhotoChanged: (index: Int, uri: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val filledCount = photos.take(3).count { !it.isNullOrBlank() }

    // Modern Android Photo Picker (zero-permission, Play Store policy safe)
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // Find first empty slot among the 3
            val firstEmptyIndex = photos.take(3).indexOfFirst { it.isNullOrBlank() }
            if (firstEmptyIndex in 0..2) {
                onPhotoChanged(firstEmptyIndex, uri.toString())
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Work Portfolio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = "Upload up to three photos of your work or shop",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = if (filledCount == 3) LeafGreen.copy(alpha = 0.15f) else SkyBlueContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$filledCount / 3 Photos",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (filledCount == 3) LeafGreen else SkyBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3 Upload Slots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (index in 0..2) {
                val photoUri = photos.getOrNull(index)
                val isOccupied = !photoUri.isNullOrBlank()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.5.dp,
                            color = if (isOccupied) SkyBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(if (isOccupied) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable {
                            if (!isOccupied) {
                                pickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        }
                        .testTag("portfolio_slot_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isOccupied && photoUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(photoUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Work sample ${index + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Delete button
                        IconButton(
                            onClick = { onPhotoChanged(index, null) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(26.dp)
                                .background(DarkBlack.copy(alpha = 0.7f), CircleShape)
                                .testTag("remove_photo_$index")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Slot badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(4.dp),
                            color = DarkBlack.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "#${index + 1}",
                                color = PureWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Photo",
                                tint = SkyBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+ Photo ${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyBlue
                            )
                            Text(
                                text = "Tap to pick",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Demo sample quick loader buttons (helpful for quick testing without emulator gallery images)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Maximum 3 photos allowed",
                style = MaterialTheme.typography.labelSmall,
                color = LeafGreen,
                fontWeight = FontWeight.Medium
            )

            // Quick preset injector if slots are open
            if (filledCount < 3) {
                OutlinedButton(
                    onClick = {
                        val packageName = context.packageName
                        val emptyIdx = photos.take(3).indexOfFirst { it.isNullOrBlank() }
                        if (emptyIdx != -1) {
                            val sampleUri = when (emptyIdx) {
                                0 -> "android.resource://$packageName/${R.drawable.ic_zucco_app_icon}"
                                1 -> "android.resource://$packageName/${R.drawable.ic_zucco_app_icon}"
                                else -> "android.resource://$packageName/${R.drawable.ic_zucco_app_icon}"
                            }
                            onPhotoChanged(emptyIdx, sampleUri)
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_sample_photo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Sample Work", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileDpUploadSlot(
    dpUri: String?,
    onDpChanged: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onDpChanged(uri.toString())
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Artisan / Business Display Picture (DP) *",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PureWhite
        )
        Text(
            text = "Upload your personal profile photo or business avatar",
            style = MaterialTheme.typography.bodySmall,
            color = LightSlate
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .border(2.5.dp, ZuccoGold, CircleShape)
                .background(MidnightInputBg)
                .clickable {
                    pickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                .testTag("dp_upload_slot"),
            contentAlignment = Alignment.Center
        ) {
            if (!dpUri.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(dpUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Display Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Delete button
                IconButton(
                    onClick = { onDpChanged(null) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(24.dp)
                        .background(DarkBlack.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove DP",
                        tint = PureWhite,
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Upload DP",
                        tint = ZuccoGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Add DP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZuccoGold
                    )
                }
            }
        }

        if (dpUri.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedButton(
                onClick = {
                    val packageName = context.packageName
                    val sampleDp = "android.resource://$packageName/${R.drawable.ic_zucco_logo_transparent
                    }"
                    onDpChanged(sampleDp)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_sample_dp_button")
            ) {
                Text("Use Sample DP", fontSize = 11.sp, color = ZuccoGold)
            }
        }
    }
}
