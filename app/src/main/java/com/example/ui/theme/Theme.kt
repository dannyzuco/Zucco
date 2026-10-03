package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Premium Midnight Blue (#0B1323) & Golden Yellow (#F6BC36) Theme
private val ZuccoColorScheme =
    darkColorScheme(
        primary = ZuccoGold,
        onPrimary = EspressoBlack,
        primaryContainer = ZuccoGoldContainer,
        onPrimaryContainer = ZuccoGoldLight,
        secondary = ZuccoGoldLight,
        onSecondary = EspressoBlack,
        secondaryContainer = MidnightElevated,
        onSecondaryContainer = ZuccoGold,
        tertiary = LeafGreen,
        onTertiary = EspressoBlack,
        tertiaryContainer = LeafGreenContainer,
        onTertiaryContainer = LeafGreenLight,
        background = ZuccoAppBackground, // Exact #0B1323 requested by user
        onBackground = PureWhite,
        surface = MidnightCard,
        onSurface = PureWhite,
        surfaceVariant = MidnightElevated,
        onSurfaceVariant = LightSlate,
        outline = MidnightCardBorder
    )

private val DarkColorScheme = ZuccoColorScheme
private val LightColorScheme = ZuccoColorScheme

@Composable
fun ZuccoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve brand palette #0B1323 + #F6BC36
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = ZuccoColorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) = ZuccoTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
