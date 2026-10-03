package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// 1. Core Midnight & Golden Yellow Palette (User Requested #0B1323 + Brand Gold)
// ============================================================================
val ZuccoAppBackground = Color(0xFF0B1323)    // Official app background (#0B1323)
val MidnightDark = Color(0xFF0B1323)          // Deep midnight background
val MidnightElevated = Color(0xFF101C33)      // Elevated surfaces, top/bottom bars (#101C33)
val MidnightCard = Color(0xFF14223D)          // Cards, containers, dialogs (#14223D)
val MidnightCardBorder = Color(0xFF22365A)    // Elegant card border on midnight
val MidnightInputBg = Color(0xFF0E182B)       // Text input fields background

// Brand Golden Yellows (Synchronized with #0B1323)
val ZuccoGold = Color(0xFFF6BC36)             // The exact brand golden yellow from logo & app icon
val ZuccoGoldLight = Color(0xFFFFD566)        // Lighter tint of brand gold for highlights
val ZuccoGoldDark = Color(0xFFEAA61E)         // Rich deep gold for high-contrast borders & text
val ZuccoGoldContainer = Color(0xFF2B2310)    // Dark golden glow container for badges/chips
val ZuccoGoldGlow = Color(0x33F6BC36)         // Subtle yellow glow for active elements

// High Contrast Text & Dividers on #0B1323
val PureWhite = Color(0xFFF8FAFC)             // Primary headline & high-legibility text
val OffWhite = Color(0xFFE2E8F0)              // Secondary readable text
val LightSlate = Color(0xFF94A3B8)            // Muted labels, timestamps & placeholders
val BorderGray = Color(0xFF1E2D4A)            // Subtle dividers

// Accent Green for verification badges & ratings
val LeafGreen = Color(0xFF22C55E)             // Vibrant verified green on dark
val LeafGreenLight = Color(0xFF4ADE80)
val LeafGreenContainer = Color(0xFF143825)

// Backward-compatible semantic mappings so all existing screens match automatically
val NavyBlue = MidnightElevated               // Mapped to MidnightElevated (#101C33)
val NavySurface = MidnightCard                // Mapped to MidnightCard (#14223D)
val DarkBlack = PureWhite                     // Text previously marked as DarkBlack renders in crisp PureWhite on #0B1323
val MediumBlack = OffWhite
val EspressoBlack = Color(0xFF0B1323)

val DarkBackground = ZuccoAppBackground
val DarkCardBg = MidnightCard
val DarkCardBorder = MidnightCardBorder
val DarkInputBg = MidnightInputBg
val DarkBadgeBg = Color(0xFF1C2840)
val WarmBadgeBg = ZuccoGoldContainer
val WarmBadgeText = ZuccoGold

// Golden Accents
val SkyBlue = ZuccoGold                       // Golden accent
val SkyBlueLight = ZuccoGoldLight
val SkyBlueContainer = ZuccoGoldContainer

// Milk Aliases mapped gracefully to Midnight Card & Background
val MilkWhite = MidnightCard
val SoftMilk = ZuccoAppBackground
val MilkBackground = ZuccoAppBackground
val MilkSurface = MidnightCard
val MilkCard = MidnightCard
val MilkBorder = MidnightCardBorder
val SoftGray = Color(0xFF1A2744)
