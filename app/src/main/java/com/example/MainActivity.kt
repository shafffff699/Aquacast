package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FloodAiChatSheet
import com.example.ui.LiquidGlassShowcaseScreen
import com.example.ui.RadarTerrainScreen
import com.example.ui.StreetFloodForecastScreen
import com.example.ui.StreetFloodMapScreen
import com.example.ui.TrafficRerouteScreen
import com.example.ui.theme.LiquidAccentPurple
import com.example.ui.theme.LiquidBorderWhite
import com.example.ui.theme.LiquidGlassTheme
import com.example.ui.theme.LiquidGlassTokens
import com.example.ui.theme.LiquidGlassWhite
import com.example.ui.theme.LiquidPrimaryPurpleStart
import com.example.ui.theme.LiquidSolidSoftWhite
import com.example.ui.theme.LiquidTextDark
import com.example.ui.theme.LiquidTextLight
import com.example.ui.theme.LiquidTextMuted
import com.example.ui.theme.PrimaryPurpleGradient
import com.example.ui.theme.RainbowBorderGradient
import com.example.ui.theme.ScreenBackgroundGradient
import com.example.viewmodel.FloodViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiquidGlassTheme {
                MainAppRoot()
            }
        }
    }
}

@Composable
fun MainAppRoot(viewModel: FloodViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var isChatBotOpen by remember { mutableStateOf(false) }

    val navItems = listOf(
        NavigationItem("Forecast", Icons.Default.WaterDamage, 0),
        NavigationItem("Google Maps", Icons.Default.Map, 1),
        NavigationItem("Reroute", Icons.Default.DirectionsCar, 2),
        NavigationItem("Radar", Icons.Default.Radar, 3),
        NavigationItem("Glass UI", Icons.AutoMirrored.Filled.ViewQuilt, 4)
    )

    // Liquid Glass Background with soft gradient from warm cream-pink to cool blue-grey
    // and subtle diagonal shadow line across the screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackgroundGradient)
            .drawBehind {
                // Light diagonal shadow across the screen so glass looks like it sits on a surface
                drawLine(
                    color = Color(0x14000000),
                    start = Offset(0f, size.height * 0.25f),
                    end = Offset(size.width, size.height * 0.75f),
                    strokeWidth = 40f
                )
                drawLine(
                    color = Color(0x0A000000),
                    start = Offset(0f, size.height * 0.22f),
                    end = Offset(size.width, size.height * 0.78f),
                    strokeWidth = 80f
                )
            }
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // App Top Bar: Logo, Title, AquaAI Trigger & Live Horizon Badge
            TopBarHeader(
                onLogoClick = { viewModel.setActiveTab(0) },
                onOpenChatBot = { isChatBotOpen = true }
            )

            // Alert Banner (if active)
            AnimatedVisibility(
                visible = uiState.simulatedAlertBanner != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (uiState.simulatedAlertBanner != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEDE9FE))
                            .border(1.dp, Color(0xFFC4B5FD), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.simulatedAlertBanner!!,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = LiquidTextDark
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = LiquidTextDark,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { viewModel.dismissBanner() }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (uiState.activeTab) {
                    0 -> StreetFloodForecastScreen(
                        uiState = uiState,
                        onHourChange = { viewModel.setSelectedHour(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onSelectStreet = { viewModel.selectStreet(it) },
                        onLocalityFilter = { viewModel.setLocalityFilter(it) },
                        onIntensityChange = { viewModel.setRainfallIntensity(it) },
                        onToggleTidalLock = { viewModel.toggleTidalLock(it) },
                        onTriggerEmergency = { viewModel.triggerCityWideAlert() },
                        onNavigateToReroute = { viewModel.setActiveTab(2) }
                    )
                    1 -> StreetFloodMapScreen(
                        uiState = uiState,
                        onHourChange = { viewModel.setSelectedHour(it) },
                        onSelectStreet = { viewModel.selectStreet(it) },
                        onNavigateToReroute = { viewModel.setActiveTab(2) }
                    )
                    2 -> TrafficRerouteScreen(
                        uiState = uiState,
                        onSelectRoute = { viewModel.selectRouteOption(it) }
                    )
                    3 -> RadarTerrainScreen(
                        uiState = uiState,
                        onTidalLockToggle = { viewModel.toggleTidalLock(it) },
                        onSiltationChange = { viewModel.setSiltationFactor(it) },
                        onTriggerEmergency = { viewModel.triggerCityWideAlert() },
                        onResetShowers = { viewModel.resetToNormalShowers() }
                    )
                    4 -> LiquidGlassShowcaseScreen(
                        onNavigateToFloodApp = { viewModel.setActiveTab(0) }
                    )
                }

                // Floating AquaAI Glass Bubble Button (Accessible on all screens)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 4.dp)
                        .shadow(12.dp, CircleShape, ambientColor = Color(0x336C5CE7))
                        .clip(CircleShape)
                        .background(PrimaryPurpleGradient)
                        .border(1.5.dp, RainbowBorderGradient, CircleShape)
                        .clickable { isChatBotOpen = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("floating_aqua_ai_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Open AI Flood Assistant",
                            tint = LiquidTextLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "AquaAI",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = LiquidTextLight
                            )
                        )
                    }
                }
            }

            // Liquid Glass Floating Bottom Navigation Bar
            LiquidGlassBottomNavBar(
                items = navItems,
                selectedIndex = uiState.activeTab,
                onItemSelected = { viewModel.setActiveTab(it) }
            )
        }

        // AquaAI Chat Bot Sheet (Animated slide-up modal)
        AnimatedVisibility(
            visible = isChatBotOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .clickable { isChatBotOpen = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(modifier = Modifier.clickable(enabled = false) {}) {
                    FloodAiChatSheet(
                        uiState = uiState,
                        onDismiss = { isChatBotOpen = false }
                    )
                }
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val index: Int
)

@Composable
fun TopBarHeader(
    onLogoClick: () -> Unit,
    onOpenChatBot: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.clickable(onClick = onLogoClick)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(6.dp, RoundedCornerShape(12.dp), ambientColor = Color(0x1F000000))
                    .clip(RoundedCornerShape(12.dp))
                    .background(LiquidSolidSoftWhite)
                    .border(1.dp, Color(0xCCFFFFFF), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.WaterDrop,
                    contentDescription = "AquaCast Logo",
                    tint = LiquidAccentPurple,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = "AquaCast",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LiquidTextDark
                    )
                )
                Text(
                    text = "Street-by-Street Flood AI",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = LiquidTextMuted
                    )
                )
            }
        }

        // Right side: AquaAI Trigger Pill + Chennai Live badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // AquaAI Button
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LiquidSolidSoftWhite)
                    .border(1.dp, Color(0x99FFFFFF), CircleShape)
                    .clickable(onClick = onOpenChatBot)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Bot",
                        tint = LiquidAccentPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "AI Bot",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LiquidAccentPurple
                        )
                    )
                }
            }

            // Live badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LiquidSolidSoftWhite)
                    .border(1.dp, Color(0x99FFFFFF), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34C759))
                    )
                    Text(
                        text = "Live",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LiquidTextDark
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LiquidGlassBottomNavBar(
    items: List<NavigationItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                ambientColor = Color(0x1F000000),
                spotColor = Color(0x1F000000)
            )
            .clip(CircleShape)
            .background(LiquidGlassWhite)
            .border(LiquidGlassTokens.BorderWidth, LiquidBorderWhite, CircleShape)
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .testTag("liquid_bottom_nav_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.index == selectedIndex
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .shadow(6.dp, CircleShape, ambientColor = Color(0x26000000))
                                    .background(LiquidSolidSoftWhite)
                                    .border(1.dp, Color(0xCCFFFFFF), CircleShape)
                            } else {
                                Modifier.background(Color.Transparent)
                            }
                        )
                        .clickable { onItemSelected(item.index) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) LiquidAccentPurple else LiquidTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        if (isSelected) {
                            Text(
                                text = item.title,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LiquidTextDark
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
