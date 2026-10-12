package com.example.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.FilterVintage
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassCardGradient
import com.example.ui.theme.IridescentCardGradient
import com.example.ui.theme.LiquidAccentPurple
import com.example.ui.theme.LiquidBorderWhite
import com.example.ui.theme.LiquidBorderWhiteSubtle
import com.example.ui.theme.LiquidCardCyan
import com.example.ui.theme.LiquidCardLavender
import com.example.ui.theme.LiquidCardPink
import com.example.ui.theme.LiquidGlassTokens
import com.example.ui.theme.LiquidGlassWhite
import com.example.ui.theme.LiquidGlassWhiteHover
import com.example.ui.theme.LiquidGlassWhiteSubtle
import com.example.ui.theme.LiquidPrimaryPurpleStart
import com.example.ui.theme.LiquidSecondaryTealStart
import com.example.ui.theme.LiquidSolidSoftWhite
import com.example.ui.theme.LiquidSuccessGreen
import com.example.ui.theme.LiquidTextDark
import com.example.ui.theme.LiquidTextLight
import com.example.ui.theme.LiquidTextMuted
import com.example.ui.theme.LiquidTextSubtle
import com.example.ui.theme.PrimaryPurpleGradient
import com.example.ui.theme.RainbowBorderGradient
import com.example.ui.theme.SecondaryTealGradient

// -----------------------------------------------------------------------------
// MAIN FROSTED GLASS CONTAINER PANEL
// Big frosted glass container, 28px rounded corners, 20-30% opacity white,
// 1.5px border, soft shadow, title and top-right window icons.
// -----------------------------------------------------------------------------
@Composable
fun LiquidMainGlassPanel(
    title: String = "Liquid Glass UI Kit",
    modifier: Modifier = Modifier,
    onMinusClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(LiquidGlassTokens.PanelRadius),
                ambientColor = Color(0x1A000000),
                spotColor = Color(0x1A000000)
            )
            .clip(RoundedCornerShape(LiquidGlassTokens.PanelRadius))
            .background(LiquidGlassWhite)
            .border(
                width = LiquidGlassTokens.BorderWidth,
                color = LiquidBorderWhite,
                shape = RoundedCornerShape(LiquidGlassTokens.PanelRadius)
            )
            .drawBehind {
                // Top inner glossy light edge
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x80FFFFFF), Color(0x00FFFFFF)),
                        startY = 0f,
                        endY = 40f
                    ),
                    size = Size(size.width, 40f)
                )
            }
            .padding(LiquidGlassTokens.PanelPadding)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Title & Top-Right window icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = LiquidTextDark
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = "Panel Circle Icon",
                        tint = LiquidTextSubtle,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Panel Minimize",
                        tint = LiquidTextSubtle,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onMinusClick)
                    )
                }
            }
            content()
        }
    }
}

// -----------------------------------------------------------------------------
// 1. PRIMARY BUTTON
// Full pill shape, purple gradient, white text, glossy white shine on top edge,
// soft purple shadow below. Height: 52dp.
// -----------------------------------------------------------------------------
@Composable
fun LiquidPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(LiquidGlassTokens.ButtonHeight)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = LiquidPrimaryPurpleStart.copy(alpha = 0.5f),
                spotColor = LiquidPrimaryPurpleStart.copy(alpha = 0.5f)
            )
            .clip(CircleShape)
            .background(PrimaryPurpleGradient)
            .border(LiquidGlassTokens.BorderWidthSubtle, Color(0x80FFFFFF), CircleShape)
            .drawBehind {
                // Glossy white shine across upper half
                drawOval(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x99FFFFFF), Color(0x00FFFFFF)),
                        startY = 0f,
                        endY = size.height * 0.55f
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height * 0.55f)
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp)
            .testTag("liquid_primary_button"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LiquidTextLight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = LiquidTextLight
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 2. SECONDARY BUTTON
// Full pill shape, teal gradient, white text, same glossy shine,
// soft teal shadow. Height: 52dp.
// -----------------------------------------------------------------------------
@Composable
fun LiquidSecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(LiquidGlassTokens.ButtonHeight)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = LiquidSecondaryTealStart.copy(alpha = 0.5f),
                spotColor = LiquidSecondaryTealStart.copy(alpha = 0.5f)
            )
            .clip(CircleShape)
            .background(SecondaryTealGradient)
            .border(LiquidGlassTokens.BorderWidthSubtle, Color(0x80FFFFFF), CircleShape)
            .drawBehind {
                // Glossy white shine across upper half
                drawOval(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x99FFFFFF), Color(0x00FFFFFF)),
                        startY = 0f,
                        endY = size.height * 0.55f
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height * 0.55f)
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp)
            .testTag("liquid_secondary_button"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LiquidTextLight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = LiquidTextLight
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 3. ROUND ICON BUTTON
// Solid soft-white circle with "+" icon (or custom icon), soft drop shadow.
// -----------------------------------------------------------------------------
@Composable
fun LiquidRoundIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
    buttonSize: Dp = 48.dp,
    tint: Color = LiquidTextDark,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(buttonSize)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color(0x1F000000),
                spotColor = Color(0x1F000000)
            )
            .clip(CircleShape)
            .background(LiquidSolidSoftWhite)
            .border(LiquidGlassTokens.BorderWidthSubtle, Color(0xCCFFFFFF), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag("liquid_round_icon_button"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Round Action Icon",
            tint = tint,
            modifier = Modifier.size(buttonSize * 0.45f)
        )
    }
}

// -----------------------------------------------------------------------------
// 4. CLEAR GLASS CIRCLE BUTTON
// Fully see-through circle, like a glass bubble, small icon, light border & inner shine.
// -----------------------------------------------------------------------------
@Composable
fun LiquidClearGlassCircleButton(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.WaterDrop,
    buttonSize: Dp = 48.dp,
    tint: Color = LiquidTextDark,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(buttonSize)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = Color(0x12000000),
                spotColor = Color(0x12000000)
            )
            .clip(CircleShape)
            .background(LiquidGlassWhite)
            .border(LiquidGlassTokens.BorderWidthSubtle, LiquidBorderWhite, CircleShape)
            .drawBehind {
                // Inner bubble shine highlight
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x99FFFFFF), Color(0x00FFFFFF)),
                        center = Offset(this.size.width * 0.35f, this.size.height * 0.35f),
                        radius = this.size.width * 0.45f
                    )
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag("liquid_clear_glass_circle_button"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Glass Bubble Icon",
            tint = tint,
            modifier = Modifier.size(buttonSize * 0.45f)
        )
    }
}

// -----------------------------------------------------------------------------
// 5. GLASS CARD (Top-Right in Reference)
// Blue-teal-lavender transparent gradient, 24px radius, iridescent edge glow.
// Inside it: app logo in white rounded square, ring-style icon button with rainbow border,
// white pill search field ("Search projects..."), purple circle "+" button at right.
// -----------------------------------------------------------------------------
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    appName: String = "AquaCast",
    searchQuery: String = "",
    onQueryChange: (String) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(LiquidGlassTokens.CardRadius),
                ambientColor = Color(0x1A6C5CE7),
                spotColor = Color(0x1A6C5CE7)
            )
            .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
            .background(GlassCardGradient)
            .border(
                width = LiquidGlassTokens.BorderWidth,
                brush = RainbowBorderGradient,
                shape = RoundedCornerShape(LiquidGlassTokens.CardRadius)
            )
            .drawBehind {
                // Soft iridescent ambient top glaze
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x40FFFFFF), Color(0x05FFFFFF)),
                        startY = 0f,
                        endY = size.height * 0.6f
                    )
                )
            }
            .padding(20.dp)
            .testTag("liquid_glass_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Top Row: App Logo in white rounded square + ring icon button with rainbow border
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // White rounded square app logo
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp), ambientColor = Color(0x14000000))
                            .clip(RoundedCornerShape(12.dp))
                            .background(LiquidSolidSoftWhite)
                            .border(1.dp, Color(0x80FFFFFF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WaterDrop,
                            contentDescription = "App Logo",
                            tint = LiquidAccentPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = appName,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LiquidTextDark
                            )
                        )
                        Text(
                            text = "Connected Stormwater Network",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = LiquidTextMuted
                            )
                        )
                    }
                }

                // Ring-style icon button with rainbow border
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(LiquidGlassWhite)
                        .border(1.5.dp, RainbowBorderGradient, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterVintage,
                        contentDescription = "Rainbow Ring Icon",
                        tint = LiquidAccentPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Bottom Row: White pill search field + purple circle "+" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // White pill search field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(CircleShape)
                        .background(LiquidSolidSoftWhite)
                        .border(1.dp, Color(0x99FFFFFF), CircleShape)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = LiquidTextDark
                        ),
                        cursorBrush = SolidColor(LiquidAccentPurple),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search projects...",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = LiquidTextMuted
                                    )
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Purple circle "+" button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(6.dp, CircleShape, ambientColor = LiquidPrimaryPurpleStart.copy(0.4f))
                        .clip(CircleShape)
                        .background(PrimaryPurpleGradient)
                        .clickable(onClick = onAddClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Project",
                        tint = LiquidTextLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 6. SEARCH BAR WITH CHECKBOX
// Glass pill, purple filled circle with white tick on left, "Search projects..." text,
// rainbow-border circle arrow button on right.
// -----------------------------------------------------------------------------
@Composable
fun LiquidSearchBarWithCheckbox(
    query: String,
    onQueryChange: (String) -> Unit,
    isChecked: Boolean = true,
    onToggleCheck: () -> Unit = {},
    placeholder: String = "Search projects...",
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color(0x14000000),
                spotColor = Color(0x14000000)
            )
            .clip(CircleShape)
            .background(LiquidGlassWhiteHover)
            .border(LiquidGlassTokens.BorderWidthSubtle, LiquidBorderWhite, CircleShape)
            .drawBehind {
                // Top inner glossy light edge
                drawOval(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x60FFFFFF), Color(0x00FFFFFF)),
                        startY = 0f,
                        endY = size.height * 0.5f
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height * 0.5f)
                )
            }
            .padding(horizontal = 8.dp)
            .testTag("liquid_search_bar_with_checkbox"),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Purple filled circle with white tick on the left
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isChecked) PrimaryPurpleGradient else SolidColor(Color(0x308B6CF0))
                    )
                    .clickable(onClick = onToggleCheck),
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected Tick",
                        tint = LiquidTextLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text field
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = LiquidTextDark,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(LiquidAccentPurple),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontSize = 15.sp,
                                color = LiquidTextMuted,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.weight(1f)
            )

            // Rainbow-border circle arrow button on the right
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LiquidSolidSoftWhite)
                    .border(1.5.dp, RainbowBorderGradient, CircleShape)
                    .clickable(onClick = onActionClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Search Action",
                    tint = LiquidAccentPurple,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 7. INPUT FIELD
// Soft-white pill, "Create new workspace" placeholder in grey, search icon on right, soft shadow.
// -----------------------------------------------------------------------------
@Composable
fun LiquidInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Create new workspace",
    trailingIcon: ImageVector = Icons.Default.Search,
    onTrailingIconClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(LiquidGlassTokens.InputHeight)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = Color(0x17000000),
                spotColor = Color(0x17000000)
            )
            .clip(CircleShape)
            .background(LiquidSolidSoftWhite)
            .border(LiquidGlassTokens.BorderWidthSubtle, Color(0xB3FFFFFF), CircleShape)
            .padding(horizontal = 20.dp)
            .testTag("liquid_input_field"),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = LiquidTextDark,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(LiquidAccentPurple),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = LiquidTextMuted,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = trailingIcon,
                contentDescription = "Input Action",
                tint = LiquidTextSubtle,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onTrailingIconClick)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 8. CHIP
// Small glass pill with text and a small icon (example: "Nitestudio").
// -----------------------------------------------------------------------------
@Composable
fun LiquidChip(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.WaterDrop,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(LiquidGlassTokens.ChipHeight)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = Color(0x10000000)
            )
            .clip(CircleShape)
            .background(LiquidGlassWhite)
            .border(LiquidGlassTokens.BorderWidthSubtle, LiquidBorderWhite, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp)
            .testTag("liquid_chip"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LiquidAccentPurple,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = LiquidTextDark
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 9. TOGGLE SWITCH
// Glass pill track with a soft-white round knob holding a "+" icon.
// -----------------------------------------------------------------------------
@Composable
fun LiquidToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackWidth = 60.dp
    val trackHeight = 34.dp
    val knobSize = 28.dp

    // Animate knob offset from left (3dp) to right (29dp)
    val targetOffset = if (checked) 29f else 3f
    val knobOffset by animateFloatAsState(targetValue = targetOffset, label = "knobOffset")

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = Color(0x14000000)
            )
            .clip(CircleShape)
            .background(
                if (checked) Color(0x668B6CF0) else LiquidGlassWhite
            )
            .border(LiquidGlassTokens.BorderWidthSubtle, LiquidBorderWhite, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
            .testTag("liquid_toggle_switch"),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset.dp)
                .size(knobSize)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x24000000)
                )
                .clip(CircleShape)
                .background(LiquidSolidSoftWhite)
                .border(1.dp, Color(0xCCFFFFFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Toggle Knob Plus",
                tint = if (checked) LiquidAccentPurple else LiquidTextSubtle,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 10. SUCCESS CHIP
// Glass pill with text and a green tick on the right (example: "whitespace").
// -----------------------------------------------------------------------------
@Composable
fun LiquidSuccessChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(LiquidGlassTokens.ChipHeight)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = Color(0x10000000)
            )
            .clip(CircleShape)
            .background(LiquidGlassWhite)
            .border(LiquidGlassTokens.BorderWidthSubtle, LiquidBorderWhite, CircleShape)
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 10.dp)
            .testTag("liquid_success_chip"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = LiquidTextDark
                )
            )
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(LiquidSuccessGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success tick",
                    tint = LiquidTextLight,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 11. TABS / SEGMENTED CONTROL
// Glass pill background. The active tab is a solid soft-white pill.
// Inactive tab is see-through with a short purple underline.
// -----------------------------------------------------------------------------
@Composable
fun LiquidTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = Color(0x12000000)
            )
            .clip(CircleShape)
            .background(LiquidGlassWhite)
            .border(LiquidGlassTokens.BorderWidthSubtle, LiquidBorderWhite, CircleShape)
            .padding(4.dp)
            .testTag("liquid_tabs"),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .clip(CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .shadow(4.dp, CircleShape, ambientColor = Color(0x1F000000))
                                    .background(LiquidSolidSoftWhite)
                                    .border(1.dp, Color(0xCCFFFFFF), CircleShape)
                            } else {
                                Modifier.background(Color.Transparent)
                            }
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(index) }
                        )
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = title,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) LiquidTextDark else LiquidTextMuted
                            )
                        )
                        // Inactive tab purple underline indicator
                        if (!isSelected) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(2.dp)
                                    .clip(CircleShape)
                                    .background(LiquidPrimaryPurpleStart)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 12. FEATURE CARD (Bottom-Right in Reference)
// Big gradient card (pink -> lavender -> blue -> cyan), 24px radius,
// title "Modern Glass UI Design" in bold dark text, subtitle "Explore more features" in grey,
// white pill button "Upgrade plan" with up-arrow icon, small pill indicator at top-right,
// soft colored light glows around bottom edges.
// -----------------------------------------------------------------------------
@Composable
fun LiquidFeatureCard(
    title: String = "Modern Glass UI Design",
    subtitle: String = "Explore more features",
    buttonText: String = "Upgrade plan",
    indicatorText: String = "PRO",
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(LiquidGlassTokens.CardRadius),
                ambientColor = Color(0x2AC9A8F0),
                spotColor = Color(0x2A8EE0E8)
            )
            .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
            .background(IridescentCardGradient)
            .border(
                width = LiquidGlassTokens.BorderWidth,
                color = Color(0x99FFFFFF),
                shape = RoundedCornerShape(LiquidGlassTokens.CardRadius)
            )
            .drawBehind {
                // Soft colored ambient light glows around the bottom edges
                drawCircle(
                    color = LiquidCardCyan.copy(alpha = 0.45f),
                    radius = size.width * 0.4f,
                    center = Offset(size.width * 0.85f, size.height * 0.95f)
                )
                drawCircle(
                    color = LiquidCardPink.copy(alpha = 0.4f),
                    radius = size.width * 0.35f,
                    center = Offset(size.width * 0.15f, size.height * 0.95f)
                )
                drawCircle(
                    color = LiquidCardLavender.copy(alpha = 0.45f),
                    radius = size.width * 0.35f,
                    center = Offset(size.width * 0.5f, size.height * 0.95f)
                )
                // Glossy white top sheen
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x66FFFFFF), Color(0x00FFFFFF)),
                        startY = 0f,
                        endY = size.height * 0.45f
                    )
                )
            }
            .padding(24.dp)
            .testTag("liquid_feature_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row: Subtitle + Small pill indicator top-right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = LiquidTextMuted
                    )
                )
                // Small pill indicator
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .clip(CircleShape)
                        .background(LiquidGlassWhiteHover)
                        .border(1.dp, Color(0x80FFFFFF), CircleShape)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = indicatorText,
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LiquidTextDark
                        )
                    )
                }
            }

            // Title
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LiquidTextDark,
                    lineHeight = 26.sp
                )
            )

            // White pill action button with up-arrow icon
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .shadow(6.dp, CircleShape, ambientColor = Color(0x1F000000))
                    .clip(CircleShape)
                    .background(LiquidSolidSoftWhite)
                    .border(1.dp, Color(0xCCFFFFFF), CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onButtonClick
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LiquidTextDark
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.NorthEast,
                        contentDescription = "Arrow Up Right",
                        tint = LiquidAccentPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
