package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.LiquidChip
import com.example.components.LiquidClearGlassCircleButton
import com.example.components.LiquidMainGlassPanel
import com.example.components.LiquidPrimaryButton
import com.example.components.LiquidRoundIconButton
import com.example.components.LiquidSearchBarWithCheckbox
import com.example.components.LiquidSecondaryButton
import com.example.components.LiquidSuccessChip
import com.example.components.LiquidTabs
import com.example.components.LiquidToggleSwitch
import com.example.model.FloodPrediction
import com.example.model.HazardLevel
import com.example.model.RainfallIntensity
import com.example.ui.theme.GlassCardGradient
import com.example.ui.theme.IridescentCardGradient
import com.example.ui.theme.LiquidAccentPurple
import com.example.ui.theme.LiquidBorderWhite
import com.example.ui.theme.LiquidBorderWhiteSubtle
import com.example.ui.theme.LiquidDangerRed
import com.example.ui.theme.LiquidGlassTokens
import com.example.ui.theme.LiquidGlassWhite
import com.example.ui.theme.LiquidGlassWhiteHover
import com.example.ui.theme.LiquidPrimaryPurpleStart
import com.example.ui.theme.LiquidSecondaryTealStart
import com.example.ui.theme.LiquidSolidSoftWhite
import com.example.ui.theme.LiquidSuccessGreen
import com.example.ui.theme.LiquidTextDark
import com.example.ui.theme.LiquidTextLight
import com.example.ui.theme.LiquidTextMuted
import com.example.ui.theme.LiquidWarningAmber
import com.example.ui.theme.RainbowBorderGradient
import com.example.viewmodel.FloodUiState

@Composable
fun StreetFloodForecastScreen(
    uiState: FloodUiState,
    onHourChange: (Int) -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectStreet: (FloodPrediction?) -> Unit,
    onLocalityFilter: (String) -> Unit,
    onIntensityChange: (RainfallIntensity) -> Unit,
    onToggleTidalLock: (Boolean) -> Unit,
    onTriggerEmergency: () -> Unit,
    onNavigateToReroute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val localities = listOf("All Localities", "Velachery", "Madipakkam", "T. Nagar", "Perambur", "Guindy", "OMR IT Corridor", "Anna Nagar")

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Panel: Headline + Radar Status + 0-3h Time Travel Slider
        item {
            LiquidMainGlassPanel(
                title = "AquaCast Chennai Flood AI"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Quick overview & Radar badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "STREET-BY-STREET PREDICTION",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LiquidAccentPurple,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = "Chennai Early Inundation Model",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LiquidTextDark
                                )
                            )
                        }

                        // IMD Doppler status pill
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(LiquidSolidSoftWhite)
                                .border(1.dp, Color(0x99FFFFFF), CircleShape)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(LiquidDangerRed)
                                )
                                Text(
                                    text = "IMD Radar: ${uiState.radarStatus.currentIntensity.rateMmHr.toInt()} mm/h",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LiquidTextDark
                                    )
                                )
                            }
                        }
                    }

                    // 0-3h Time Travel Segmented Control Tabs
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rainfall Inundation Horizon",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = LiquidTextDark)
                            )
                            Text(
                                text = "+${uiState.selectedHour} Hours Post-Rain",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LiquidAccentPurple)
                            )
                        }

                        LiquidTabs(
                            tabs = listOf("0h (Now)", "+1h (Rise)", "+2h (Peak)", "+3h (Crisis)"),
                            selectedIndex = uiState.selectedHour,
                            onTabSelected = onHourChange
                        )
                    }

                    // Rainfall Scenario Quick Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RainfallIntensity.values().forEach { intensity ->
                            val isSelected = uiState.radarStatus.currentIntensity == intensity
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) LiquidPrimaryPurpleStart.copy(alpha = 0.25f)
                                        else LiquidGlassWhite
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) LiquidPrimaryPurpleStart else LiquidBorderWhiteSubtle,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onIntensityChange(intensity) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = intensity.title.split(" ").first(),
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) LiquidAccentPurple else LiquidTextDark
                                    )
                                )
                            }
                        }
                    }

                    // Tidal Lock & City Drain Connectivity Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x26FFFFFF))
                            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bay of Bengal High Tide Lock",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LiquidTextDark)
                            )
                            Text(
                                text = if (uiState.tidalLockOverride) "Tidal lock active: Drains cannot discharge" else "Low tide: Gravity drains flowing freely",
                                style = TextStyle(fontSize = 11.sp, color = LiquidTextMuted)
                            )
                        }
                        LiquidToggleSwitch(
                            checked = uiState.tidalLockOverride,
                            onCheckedChange = onToggleTidalLock
                        )
                    }

                    // Search bar
                    LiquidSearchBarWithCheckbox(
                        query = uiState.searchQuery,
                        onQueryChange = onSearchChange,
                        isChecked = true,
                        placeholder = "Search street or locality (e.g. Velachery, Usman Rd)..."
                    )
                }
            }
        }

        // Locality Filter Horizontal Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(localities) { locality ->
                    val isSelected = uiState.selectedLocalityFilter == locality
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) LiquidSolidSoftWhite else LiquidGlassWhite
                            )
                            .border(
                                1.dp,
                                if (isSelected) LiquidAccentPurple else LiquidBorderWhite,
                                CircleShape
                            )
                            .clickable { onLocalityFilter(locality) }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = locality,
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) LiquidAccentPurple else LiquidTextDark
                            )
                        )
                    }
                }
            }
        }

        // Priority Banner: "Which street will flood first?"
        item {
            val firstStreet = uiState.predictions.firstOrNull()
            if (firstStreet != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(LiquidGlassTokens.CardRadius), ambientColor = Color(0x20FF3B30))
                        .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFF0F2), Color(0xFFFFE3E8), Color(0xFFFDE8E9))
                            )
                        )
                        .border(1.5.dp, Color(0xFFFFB3BA), RoundedCornerShape(LiquidGlassTokens.CardRadius))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(LiquidDangerRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Alert",
                                        tint = LiquidTextLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "PROJECTED TO FLOOD FIRST",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LiquidDangerRed,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(LiquidDangerRed.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ETA: ${firstStreet.timeToFloodMinutes} mins",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LiquidDangerRed
                                    )
                                )
                            }
                        }

                        Text(
                            text = "${firstStreet.street.name} (${firstStreet.street.locality})",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = LiquidTextDark
                            )
                        )

                        Text(
                            text = "ISRO DEM Elevation: ${firstStreet.street.elevationMeters}m (Low lakebed basin). Stormwater drain connects to ${firstStreet.street.drainOutfall}. Water rising at +${firstStreet.depthAtHour(uiState.selectedHour)} cm at +${uiState.selectedHour}h.",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = LiquidTextDark.copy(alpha = 0.8f),
                                lineHeight = 18.sp
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LiquidPrimaryButton(
                                text = "Reroute Traffic Around It",
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToReroute
                            )
                            LiquidClearGlassCircleButton(
                                icon = Icons.Default.Info,
                                onClick = { onSelectStreet(firstStreet) }
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Street-by-Street Inundation Ranking
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Street Vulnerability Ranking",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LiquidTextDark
                    )
                )
                Text(
                    text = "${uiState.filteredPredictions.size} Hotspots Monitored",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = LiquidTextMuted
                    )
                )
            }
        }

        // Street Prediction Cards List
        items(uiState.filteredPredictions) { pred ->
            StreetPredictionCard(
                prediction = pred,
                selectedHour = uiState.selectedHour,
                onClick = { onSelectStreet(pred) },
                onRerouteClick = onNavigateToReroute
            )
        }
    }

    // Street Detail Modal Sheet if selected
    if (uiState.selectedStreet != null) {
        StreetDetailModal(
            prediction = uiState.selectedStreet,
            onDismiss = { onSelectStreet(null) }
        )
    }
}

@Composable
fun StreetPredictionCard(
    prediction: FloodPrediction,
    selectedHour: Int,
    onClick: () -> Unit,
    onRerouteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDepth = prediction.depthAtHour(selectedHour)
    val hazard = prediction.hazardLevel

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(LiquidGlassTokens.CardRadius),
                ambientColor = Color(0x14000000)
            )
            .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
            .background(LiquidGlassWhiteHover)
            .border(
                width = LiquidGlassTokens.BorderWidthSubtle,
                color = LiquidBorderWhite,
                shape = RoundedCornerShape(LiquidGlassTokens.CardRadius)
            )
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header Row: Rank Badge + Locality + Hazard Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Order rank badge
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(LiquidSolidSoftWhite)
                            .border(1.dp, Color(0x99FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${prediction.floodRankOrder}",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LiquidTextDark
                            )
                        )
                    }

                    Column {
                        Text(
                            text = prediction.street.name,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LiquidTextDark
                            )
                        )
                        Text(
                            text = prediction.street.locality,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = LiquidTextMuted
                            )
                        )
                    }
                }

                // Hazard Level Pill
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(hazard.color.copy(alpha = 0.15f))
                        .border(1.dp, hazard.color.copy(alpha = 0.4f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = hazard.label,
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = hazard.color
                        )
                    )
                }
            }

            // Inundation Stats Row: Depth Gauge + Arrival ETA + ISRO Elevation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LiquidSolidSoftWhite.copy(alpha = 0.7f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Depth
                Column {
                    Text(
                        text = "Depth (+${selectedHour}h)",
                        style = TextStyle(fontSize = 11.sp, color = LiquidTextMuted)
                    )
                    Text(
                        text = "${currentDepth} cm",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentDepth > 20.0) LiquidDangerRed else LiquidAccentPurple
                        )
                    )
                }

                // Time to flood
                Column {
                    Text(
                        text = "Flood Arrival",
                        style = TextStyle(fontSize = 11.sp, color = LiquidTextMuted)
                    )
                    Text(
                        text = if (prediction.timeToFloodMinutes >= 999) "Safe / Dry" else "${prediction.timeToFloodMinutes} min",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (prediction.timeToFloodMinutes < 30) LiquidDangerRed else LiquidTextDark
                        )
                    )
                }

                // ISRO DEM Elevation
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ISRO Elevation",
                        style = TextStyle(fontSize = 11.sp, color = LiquidTextMuted)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Terrain,
                            contentDescription = null,
                            tint = LiquidSecondaryTealStart,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${prediction.street.elevationMeters}m",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LiquidTextDark
                            )
                        )
                    }
                }
            }

            // Water rise timeline mini bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0h: ${prediction.depthAt0hCm}cm",
                        style = TextStyle(fontSize = 10.sp, color = LiquidTextMuted)
                    )
                    Text(
                        text = "1h: ${prediction.depthAt1hCm}cm",
                        style = TextStyle(fontSize = 10.sp, color = LiquidTextMuted)
                    )
                    Text(
                        text = "2h: ${prediction.depthAt2hCm}cm",
                        style = TextStyle(fontSize = 10.sp, color = LiquidTextMuted)
                    )
                    Text(
                        text = "3h: ${prediction.depthAt3hCm}cm",
                        style = TextStyle(fontSize = 10.sp, color = LiquidTextMuted, fontWeight = FontWeight.Bold)
                    )
                }

                // Visual depth bar
                val ratio = (currentDepth / 100.0).coerceIn(0.02, 1.0).toFloat()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(Color(0x33000000))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(ratio)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(LiquidSecondaryTealStart, LiquidPrimaryPurpleStart, LiquidDangerRed)
                                )
                            )
                    )
                }
            }

            // Bottom Drain & Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Outfall: ${prediction.street.drainOutfall}",
                    style = TextStyle(fontSize = 11.sp, color = LiquidTextMuted),
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                if (currentDepth >= 15.0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LiquidPrimaryPurpleStart)
                            .clickable(onClick = onRerouteClick)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Reroute Traffic",
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LiquidTextLight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StreetDetailModal(
    prediction: FloodPrediction,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(LiquidSolidSoftWhite)
            .border(1.5.dp, LiquidBorderWhite, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Header Row: Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HYDROLOGICAL SIMULATION PROFILE",
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LiquidAccentPurple)
                    )
                    Text(
                        text = prediction.street.name,
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x1F000000))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LiquidTextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Inundation Rise Curve (Custom Canvas Chart)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Projected Water Rise Curve (0 to 3 Hours)",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LiquidTextDark)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEDE9FE).copy(alpha = 0.5f))
                        .border(1.dp, Color(0x408B6CF0), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val w = size.width
                        val h = size.height

                        // Horizontal guideline at 15cm (danger threshold)
                        val hazardY = h - (15.0f / 100.0f) * h
                        drawLine(
                            color = Color(0x66FF3B30),
                            start = Offset(0f, hazardY),
                            end = Offset(w, hazardY),
                            strokeWidth = 2f
                        )

                        // Water curve path
                        val points = listOf(
                            Offset(0f, h - (prediction.depthAt0hCm.toFloat() / 100f * h)),
                            Offset(w * 0.33f, h - (prediction.depthAt1hCm.toFloat() / 100f * h)),
                            Offset(w * 0.66f, h - (prediction.depthAt2hCm.toFloat() / 100f * h)),
                            Offset(w, h - (prediction.depthAt3hCm.toFloat() / 100f * h))
                        )

                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val cur = points[i]
                                cubicTo(
                                    prev.x + (cur.x - prev.x) / 2, prev.y,
                                    prev.x + (cur.x - prev.x) / 2, cur.y,
                                    cur.x, cur.y
                                )
                            }
                        }

                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(
                                listOf(LiquidSecondaryTealStart, LiquidPrimaryPurpleStart, LiquidDangerRed)
                            ),
                            style = Stroke(width = 6f, cap = StrokeCap.Round)
                        )

                        // Data points circles
                        points.forEach { pt ->
                            drawCircle(color = LiquidAccentPurple, radius = 6f, center = pt)
                            drawCircle(color = Color.White, radius = 3f, center = pt)
                        }
                    }
                }
            }

            // Key Technical Metrics Table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x20000000))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailMetricRow("ISRO Terrain Elevation", "${prediction.street.elevationMeters} meters MSL")
                DetailMetricRow("Stormwater Drain Outfall", prediction.street.drainOutfall)
                DetailMetricRow("Pipe Siltation Blockage", "${prediction.street.drainSiltationPercent}% silted")
                DetailMetricRow("Catchment Surface Area", "${prediction.street.catchmentAreaSqKm} sq. km")
                DetailMetricRow("Vehicle Wading Advisory", prediction.advisory)
            }

            LiquidPrimaryButton(
                text = "Close Profile",
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismiss
            )
        }
    }
}

@Composable
fun DetailMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted))
        Text(text = value, style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LiquidTextDark))
    }
}
