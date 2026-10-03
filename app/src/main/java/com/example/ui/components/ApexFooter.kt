package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LightSlate
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ZuccoAppBackground
import com.example.ui.theme.ZuccoGold

@Composable
fun ApexDigitalHubBadge(
    modifier: Modifier = Modifier,
    textColor: Color = PureWhite,
    subtitleColor: Color = ZuccoGold,
    showPoweredBy: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = ZuccoGold,
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = "Apex Digital Hub Logo",
                    tint = ZuccoAppBackground,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            if (showPoweredBy) {
                Text(
                    text = "POWERED BY",
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    color = subtitleColor
                )
            }
            Text(
                text = "Apex Digital Hub",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
fun ApexFooter(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = LightSlate
) {
    val context = LocalContext.current
    Surface(
        color = backgroundColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.apexdigitahub.com.ng"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Safe fallback
                        }
                    }
                    .padding(4.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = ZuccoGold,
                    modifier = Modifier.size(18.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = "Apex Digital Hub Icon",
                            tint = ZuccoAppBackground,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Powered by ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
                Text(
                    text = "Apex Digital Hub",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ZuccoGold
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "© ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} Apex Digital Hub. All rights reserved.",
                fontSize = 10.sp,
                color = contentColor.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
