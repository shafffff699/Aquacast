package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Water
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.LiquidClearGlassCircleButton
import com.example.components.LiquidMainGlassPanel
import com.example.components.LiquidPrimaryButton
import com.example.components.LiquidTabs
import com.example.model.FloodPrediction
import com.example.model.HazardLevel
import com.example.ui.theme.LiquidAccentPurple
import com.example.ui.theme.LiquidBorderWhite
import com.example.ui.theme.LiquidDangerRed
import com.example.ui.theme.LiquidGlassTokens
import com.example.ui.theme.LiquidGlassWhite
import com.example.ui.theme.LiquidPrimaryPurpleStart
import com.example.ui.theme.LiquidSecondaryTealStart
import com.example.ui.theme.LiquidSolidSoftWhite
import com.example.ui.theme.LiquidSuccessGreen
import com.example.ui.theme.LiquidTextDark
import com.example.ui.theme.LiquidTextLight
import com.example.ui.theme.LiquidTextMuted
import com.example.ui.theme.LiquidWarningAmber
import com.example.viewmodel.FloodUiState

@Composable
fun StreetFloodMapScreen(
    uiState: FloodUiState,
    onHourChange: (Int) -> Unit,
    onSelectStreet: (FloodPrediction?) -> Unit,
    onNavigateToReroute: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mapMode by remember { androidx.compose.runtime.mutableIntStateOf(0) } // 0: Google Maps Data, 1: Chennai GIS Map
    var showRadarOverlay by remember { mutableStateOf(true) }
    var selectedHotspot by remember { mutableStateOf(uiState.predictions.firstOrNull()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector: Google Maps Platform vs Chennai GIS Map
        LiquidTabs(
            tabs = listOf("Google Maps Platform", "Chennai GIS Map"),
            selectedIndex = mapMode,
            onTabSelected = { mapMode = it }
        )

        if (mapMode == 0) {
            GoogleMapsDataView(uiState = uiState)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
        // Main Panel: Headline & Map Controls
        LiquidMainGlassPanel(
            title = "Live Chennai Hotspot Map"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Real-time street inundation GIS map with waterways & elevation layers.",
                        style = TextStyle(fontSize = 13.sp, color = LiquidTextMuted),
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (showRadarOverlay) LiquidPrimaryPurpleStart else LiquidGlassWhite)
                            .clickable { showRadarOverlay = !showRadarOverlay }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (showRadarOverlay) "Radar On" else "Radar Off",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showRadarOverlay) LiquidTextLight else LiquidTextDark
                            )
                        )
                    }
                }

                // Time Horizon Tabs
                LiquidTabs(
                    tabs = listOf("0h Now", "+1h Rise", "+2h Peak", "+3h Risk"),
                    selectedIndex = uiState.selectedHour,
                    onTabSelected = onHourChange
                )
            }
        }

        // Live Interactive Canvas Map
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .shadow(16.dp, RoundedCornerShape(LiquidGlassTokens.CardRadius), ambientColor = Color(0x221E1B4B))
                .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
                .background(Color(0xFF0F172A))
                .border(1.5.dp, Color(0x4038BDF8), RoundedCornerShape(LiquidGlassTokens.CardRadius))
        ) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(uiState.predictions) {
                        detectTapGestures { tapOffset ->
                            val w = size.width.toFloat()
                            val h = size.height.toFloat()
                            // Check which hotspot is closest to tap
                            val closest = uiState.predictions.minByOrNull { pred ->
                                val pt = calculateHotspotOffset(pred, w, h)
                                (pt.x - tapOffset.x) * (pt.x - tapOffset.x) + (pt.y - tapOffset.y) * (pt.y - tapOffset.y)
                            }
                            if (closest != null) {
                                selectedHotspot = closest
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // 1. Bay of Bengal Coastline on right side
                val coastPath = Path().apply {
                    moveTo(w * 0.88f, 0f)
                    cubicTo(w * 0.86f, h * 0.3f, w * 0.90f, h * 0.7f, w * 0.85f, h)
                    lineTo(w, h)
                    lineTo(w, 0f)
                    close()
                }
                drawPath(
                    path = coastPath,
                    brush = Brush.horizontalGradient(listOf(Color(0x330284C7), Color(0x660369A1)))
                )

                // 2. Natural River & Canal Lines (Adyar River & Cooum River)
                // Cooum River (North-Central eastwards)
                val cooumPath = Path().apply {
                    moveTo(0f, h * 0.28f)
                    cubicTo(w * 0.3f, h * 0.26f, w * 0.6f, h * 0.35f, w * 0.88f, h * 0.32f)
                }
                drawPath(cooumPath, Color(0x4D38BDF8), style = Stroke(width = 4f, cap = StrokeCap.Round))

                // Adyar River (Central-South eastwards)
                val adyarPath = Path().apply {
                    moveTo(w * 0.1f, h * 0.62f)
                    cubicTo(w * 0.4f, h * 0.58f, w * 0.65f, h * 0.68f, w * 0.87f, h * 0.65f)
                }
                drawPath(adyarPath, Color(0x4D38BDF8), style = Stroke(width = 6f, cap = StrokeCap.Round))

                // Buckingham Canal (Parallel to coast)
                val canalPath = Path().apply {
                    moveTo(w * 0.82f, 0f)
                    lineTo(w * 0.80f, h)
                }
                drawPath(canalPath, Color(0x3360A5FA), style = Stroke(width = 3f, cap = StrokeCap.Round))

                // 3. Radar Reflectivity Heat Overlay (if enabled)
                if (showRadarOverlay) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0x66EF4444), Color(0x33F59E0B), Color(0x0010B981)),
                            center = Offset(w * 0.5f, h * 0.5f),
                            radius = w * 0.45f
                        ),
                        radius = w * 0.45f,
                        center = Offset(w * 0.5f, h * 0.5f)
                    )
                }

                // 4. Plot Street Hotspots
                uiState.predictions.forEach { pred ->
                    val pos = calculateHotspotOffset(pred, w, h)
                    val depth = pred.depthAtHour(uiState.selectedHour)
                    val nodeColor = when {
                        depth < 8.0 -> Color(0xFF34C759)
                        depth < 18.0 -> Color(0xFF5CC8C0)
                        depth < 35.0 -> Color(0xFFF59E0B)
                        depth < 60.0 -> Color(0xFFFF6B6B)
                        else -> Color(0xFFEF4444)
                    }

                    val isSelected = selectedHotspot?.street?.id == pred.street.id

                    // Water ripple circle for flooded streets
                    if (depth > 20.0) {
                        drawCircle(
                            color = nodeColor.copy(alpha = 0.35f),
                            radius = if (isSelected) 22f else 16f,
                            center = pos
                        )
                    }

                    // Main node circle
                    drawCircle(
                        color = nodeColor,
                        radius = if (isSelected) 12f else 8f,
                        center = pos
                    )
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 6f else 3.5f,
                        center = pos
                    )
                }
            }

            // Map Overlays
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CHENNAI METRO GIS (ISRO DEM)",
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                )
                Text(
                    text = "Bay of Bengal →",
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                )
            }

            // Map Legend
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xCC0F172A))
                    .border(1.dp, Color(0x33FFFFFF), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendDot(Color(0xFF34C759), "Safe")
                LegendDot(Color(0xFFF59E0B), "15-30cm")
                LegendDot(Color(0xFFEF4444), ">30cm Flooded")
            }
        }

        // Selected Hotspot Info Card below map
        if (selectedHotspot != null) {
            val spot = selectedHotspot!!
            val depth = spot.depthAtHour(uiState.selectedHour)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(LiquidGlassTokens.CardRadius), ambientColor = Color(0x1F000000))
                    .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
                    .background(LiquidSolidSoftWhite)
                    .border(1.dp, LiquidBorderWhite, RoundedCornerShape(LiquidGlassTokens.CardRadius))
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = spot.street.name,
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark)
                            )
                            Text(
                                text = "${spot.street.locality} • Rank #${spot.floodRankOrder} to flood",
                                style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted)
                            )
                        }

                        // Depth Pill
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (depth > 20) LiquidDangerRed.copy(0.15f) else LiquidSuccessGreen.copy(0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${depth} cm",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (depth > 20) LiquidDangerRed else LiquidSuccessGreen
                                )
                            )
                        }
                    }

                    // Key details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ISRO Elevation: ${spot.street.elevationMeters}m MSL",
                            style = TextStyle(fontSize = 12.sp, color = LiquidTextDark, fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "Flood Arrival: ${spot.timeToFloodMinutes} min",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (spot.timeToFloodMinutes < 30) LiquidDangerRed else LiquidTextDark
                            )
                        )
                    }

                    Text(
                        text = "Stormwater Drain Outfall: ${spot.street.drainOutfall} (${spot.street.drainSiltationPercent}% silted). ${spot.advisory}",
                        style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted, lineHeight = 16.sp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiquidPrimaryButton(
                            text = "Reroute Around Here",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToReroute
                        )
                        LiquidClearGlassCircleButton(
                            icon = Icons.Default.Info,
                            onClick = { onSelectStreet(spot) }
                        )
                    }
                }
            }
        }
            } // closes inner Column (inside else)
        } // closes else
    } // closes outer Column
} // closes StreetFloodMapScreen

@Composable
fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, style = TextStyle(fontSize = 10.sp, color = Color.White))
    }
}

// Convert latitude and longitude of Chennai hotspots into 2D canvas coordinates
fun calculateHotspotOffset(pred: FloodPrediction, w: Float, h: Float): Offset {
    // Chennai bounding box:
    // Min Lat: 12.94 (South / Madipakkam), Max Lat: 13.12 (North / Perambur)
    // Min Lon: 80.17 (West / Koyambedu), Max Lon: 80.28 (East / Beach)
    val minLat = 12.94
    val maxLat = 13.12
    val minLon = 80.17
    val maxLon = 80.28

    val xFraction = ((pred.street.lon - minLon) / (maxLon - minLon)).coerceIn(0.1, 0.9)
    // Invert Y because Latitude increases northwards
    val yFraction = (1.0 - ((pred.street.lat - minLat) / (maxLat - minLat))).coerceIn(0.1, 0.9)

    return Offset((xFraction * w).toFloat(), (yFraction * h).toFloat())
}
