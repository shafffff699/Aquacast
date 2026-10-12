package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Liquid Glass Theme Color Tokens
val LiquidPrimaryPurpleStart = Color(0xFF8B6CF0)
val LiquidPrimaryPurpleEnd = Color(0xFFA78BFA)

val LiquidSecondaryTealStart = Color(0xFF5CC8C0)
val LiquidSecondaryTealEnd = Color(0xFF8EE0D6)

val LiquidAccentPurple = Color(0xFF6C5CE7)
val LiquidSuccessGreen = Color(0xFF34C759)
val LiquidWarningAmber = Color(0xFFFF9500)
val LiquidDangerRed = Color(0xFFFF3B30)

// Glass Whites & Neutrals
val LiquidGlassWhite = Color(0x40FFFFFF) // 25% opacity
val LiquidGlassWhiteHover = Color(0x54FFFFFF) // 33% opacity
val LiquidGlassWhiteSubtle = Color(0x26FFFFFF) // 15% opacity
val LiquidBorderWhite = Color(0x99FFFFFF) // 60% opacity
val LiquidBorderWhiteSubtle = Color(0x4DFFFFFF) // 30% opacity
val LiquidSolidSoftWhite = Color(0xFFF8F5F7)

val LiquidTextDark = Color(0xFF1F1F2E)
val LiquidTextLight = Color(0xFFFFFFFF)
val LiquidTextMuted = Color(0xFF75758A)
val LiquidTextSubtle = Color(0xFF9898AA)

// Gradient Card Palette (pink -> lavender -> sky blue -> cyan)
val LiquidCardPink = Color(0xFFF9A8B8)
val LiquidCardLavender = Color(0xFFC9A8F0)
val LiquidCardSkyBlue = Color(0xFF9CC8F5)
val LiquidCardCyan = Color(0xFF8EE0E8)

// Screen Background Gradient
val LiquidBgWarmPink = Color(0xFFFFF0F3)
val LiquidBgCoolBlueGrey = Color(0xFFE2E9F4)
val LiquidBgCoolBottom = Color(0xFFD6E0EE)

// Gradient Brushes
val PrimaryPurpleGradient = Brush.horizontalGradient(
    listOf(LiquidPrimaryPurpleStart, LiquidPrimaryPurpleEnd)
)

val SecondaryTealGradient = Brush.horizontalGradient(
    listOf(LiquidSecondaryTealStart, LiquidSecondaryTealEnd)
)

val IridescentCardGradient = Brush.linearGradient(
    listOf(LiquidCardPink, LiquidCardLavender, LiquidCardSkyBlue, LiquidCardCyan)
)

val RainbowBorderGradient = Brush.linearGradient(
    listOf(LiquidCardPink, LiquidCardLavender, LiquidCardSkyBlue, LiquidCardCyan)
)

val GlassCardGradient = Brush.linearGradient(
    listOf(Color(0x3B8EE0D6), Color(0x3BC9A8F0), Color(0x3B9CC8F5))
)

val ScreenBackgroundGradient = Brush.linearGradient(
    listOf(LiquidBgWarmPink, LiquidBgCoolBlueGrey, LiquidBgCoolBottom)
)
