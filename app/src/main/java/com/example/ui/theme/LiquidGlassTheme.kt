package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object LiquidGlassTokens {
    val PanelRadius = 28.dp
    val CardRadius = 24.dp
    val PillRadius = 999.dp
    
    val BorderWidth = 1.5.dp
    val BorderWidthSubtle = 1.dp
    
    val ButtonHeight = 52.dp
    val InputHeight = 48.dp
    val ChipHeight = 36.dp
    val PanelPadding = 24.dp
    val ItemGap = 16.dp
}

val LiquidGlassTypography = androidx.compose.material3.Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        color = LiquidTextDark
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = LiquidTextDark
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = LiquidTextDark
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        color = LiquidTextDark
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = LiquidTextMuted
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        color = LiquidTextLight
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        color = LiquidTextMuted
    )
)

val LiquidGlassColorScheme = lightColorScheme(
    primary = LiquidPrimaryPurpleStart,
    onPrimary = LiquidTextLight,
    secondary = LiquidSecondaryTealStart,
    onSecondary = LiquidTextLight,
    tertiary = LiquidAccentPurple,
    background = LiquidBgWarmPink,
    surface = LiquidSolidSoftWhite,
    onSurface = LiquidTextDark,
    surfaceVariant = LiquidGlassWhite,
    outline = LiquidBorderWhite
)

@Composable
fun LiquidGlassTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LiquidGlassColorScheme,
        typography = LiquidGlassTypography,
        shapes = androidx.compose.material3.Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(LiquidGlassTokens.CardRadius),
            large = RoundedCornerShape(LiquidGlassTokens.PanelRadius),
            extraLarge = RoundedCornerShape(LiquidGlassTokens.PillRadius)
        ),
        content = content
    )
}
