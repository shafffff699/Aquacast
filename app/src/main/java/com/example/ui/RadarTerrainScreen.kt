package com.example.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Water
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.LiquidMainGlassPanel
import com.example.components.LiquidPrimaryButton
import com.example.components.LiquidSecondaryButton
import com.example.components.LiquidToggleSwitch
import com.example.engine.HydrologicalAiEngine
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarTerrainScreen(
    uiState: FloodUiState,
    onTidalLockToggle: (Boolean) -> Unit,
    onSiltationChange: (Int) -> Unit,
    onTriggerEmergency: () -> Unit,
    onResetShowers: () -> Unit,
    modifier: Modifier = Modifier
) {
    val radarTransition = rememberInfiniteTransition(label = "radarSweep")
    val sweepAngle by radarTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAngle"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Panel: IMD Radar Live Sweep
        LiquidMainGlassPanel(
            title = "IMD Doppler Weather Radar"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = uiState.radarStatus.stationName,
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LiquidTextDark)
                        )
                        Text(
                            text = "${uiState.radarStatus.band} • Reflectivity: ${uiState.radarStatus.currentIntensity.radarReflectivityDbz} dBZ",
                            style = TextStyle(fontSize = 11.sp, color = LiquidTextMuted)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LiquidDangerRed.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE SWEEP",
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LiquidDangerRed)
                        )
                    }
                }

                // Radar Scope Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.5.dp, Color(0x3338BDF8), RoundedCornerShape(20.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth(0.9f).height(220.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val maxRadius = size.height * 0.45f

                        // Concentric Range Rings (25km, 50km, 75km, 100km)
                        val rings = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                        rings.forEach { fraction ->
                            drawCircle(
                                color = Color(0x3338BDF8),
                                radius = maxRadius * fraction,
                                center = center,
                                style = Stroke(width = 1.5f)
                            )
                        }

                        // Crosshair lines
                        drawLine(
                            color = Color(0x3338BDF8),
                            start = Offset(center.x - maxRadius, center.y),
                            end = Offset(center.x + maxRadius, center.y),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = Color(0x3338BDF8),
                            start = Offset(center.x, center.y - maxRadius),
                            end = Offset(center.x, center.y + maxRadius),
                            strokeWidth = 1f
                        )

                        // Storm Cell Heatmap Blobs (Simulated Monsoon Cloud Clustered over Chennai)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xB3EF4444), Color(0x80F59E0B), Color(0x0010B981)),
                                center = Offset(center.x + 35f, center.y - 25f),
                                radius = maxRadius * 0.45f
                            ),
                            radius = maxRadius * 0.45f,
                            center = Offset(center.x + 35f, center.y - 25f)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x99DC2626), Color(0x66F97316), Color(0x0006B6D4)),
                                center = Offset(center.x - 20f, center.y + 40f),
                                radius = maxRadius * 0.35f
                            ),
                            radius = maxRadius * 0.35f,
                            center = Offset(center.x - 20f, center.y + 40f)
                        )

                        // Rotating Radar Beam Line
                        val rad = Math.toRadians(sweepAngle.toDouble())
                        val beamEnd = Offset(
                            (center.x + maxRadius * cos(rad)).toFloat(),
                            (center.y + maxRadius * sin(rad)).toFloat()
                        )
                        drawLine(
                            color = Color(0xFF38BDF8),
                            start = center,
                            end = beamEnd,
                            strokeWidth = 2.5f
                        )

                        // Radar Station Center Point (Meenambakkam)
                        drawCircle(color = Color(0xFF38BDF8), radius = 6f, center = center)
                        drawCircle(color = Color.White, radius = 3f, center = center)
                    }

                    // Scope metadata tags
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Intensity: ${uiState.radarStatus.currentIntensity.rateMmHr} mm/h",
                            style = TextStyle(fontSize = 11.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Storm Motion: ${uiState.radarStatus.stormSpeedKmh} km/h @ ${uiState.radarStatus.stormHeadingDegrees}°",
                            style = TextStyle(fontSize = 10.sp, color = Color(0xFF94A3B8))
                        )
                    }
                }
            }
        }

        // ISRO Cartosat DEM Terrain & Stormwater Drain Pipe Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(LiquidGlassTokens.CardRadius), ambientColor = Color(0x1F000000))
                .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
                .background(LiquidSolidSoftWhite)
                .border(1.dp, LiquidBorderWhite, RoundedCornerShape(LiquidGlassTokens.CardRadius))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Terrain,
                            contentDescription = null,
                            tint = LiquidAccentPurple,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "ISRO Terrain & Drain Network",
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark)
                        )
                    }
                    Text(
                        text = "Cartosat-1 DEM",
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = LiquidSecondaryTealStart)
                    )
                }

                Text(
                    text = "Chennai elevation profile drops from Kathipara bedrock (12.8m) to low marshland basins (1.8m). Drains connect into Adyar River, Cooum, Otteri Nullah and Buckingham Canal.",
                    style = TextStyle(fontSize = 13.sp, color = LiquidTextMuted, lineHeight = 18.sp)
                )

                // Terrain Elevation Profile Cross-Section Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val w = size.width
                        val h = size.height

                        // Terrain elevation curve
                        val terrainPoints = listOf(
                            Offset(0f, h * 0.15f), // Kathipara (12.8m)
                            Offset(w * 0.2f, h * 0.35f), // Anna Nagar (9.8m)
                            Offset(w * 0.45f, h * 0.55f), // T. Nagar (4.1m)
                            Offset(w * 0.7f, h * 0.85f), // Velachery (2.2m)
                            Offset(w * 0.85f, h * 0.92f), // Madipakkam (1.8m)
                            Offset(w, h * 0.95f) // Bay of Bengal (0m MSL)
                        )

                        val terrainPath = Path().apply {
                            moveTo(0f, h)
                            lineTo(terrainPoints[0].x, terrainPoints[0].y)
                            for (i in 1 until terrainPoints.size) {
                                val prev = terrainPoints[i - 1]
                                val cur = terrainPoints[i]
                                cubicTo(
                                    prev.x + (cur.x - prev.x) / 2, prev.y,
                                    prev.x + (cur.x - prev.x) / 2, cur.y,
                                    cur.x, cur.y
                                )
                            }
                            lineTo(w, h)
                            close()
                        }

                        // Draw terrain fill
                        drawPath(
                            path = terrainPath,
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF94A3B8), Color(0xFF475569))
                            )
                        )

                        // Water pooling in low basins (Velachery & Madipakkam)
                        val waterLevelY = h * 0.78f
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0x8038BDF8), Color(0xCC0284C7))),
                            topLeft = Offset(w * 0.65f, waterLevelY),
                            size = Size(w * 0.3f, h - waterLevelY)
                        )

                        // Drain outfall pipe line underneath
                        drawLine(
                            color = Color(0xFF0284C7),
                            start = Offset(w * 0.45f, h * 0.8f),
                            end = Offset(w, h * 0.88f),
                            strokeWidth = 5f
                        )
                    }

                    // Annotations
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Guindy: 12.8m", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark))
                        Text(text = "T. Nagar: 4.1m", style = TextStyle(fontSize = 10.sp, color = LiquidTextDark))
                        Text(text = "Velachery Bowl: 2.2m", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LiquidDangerRed))
                    }
                }

                // Diagnostic Controls: Siltation & Tidal Lock
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Stormwater Drain Siltation Blockage: ${uiState.siltationFactorPercent}%",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LiquidTextDark)
                    )

                    Slider(
                        value = uiState.siltationFactorPercent.toFloat(),
                        onValueChange = { onSiltationChange(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = LiquidAccentPurple,
                            activeTrackColor = LiquidPrimaryPurpleStart,
                            inactiveTrackColor = Color(0x33000000)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiquidPrimaryButton(
                            text = "Trigger Red Alert",
                            modifier = Modifier.weight(1f),
                            onClick = onTriggerEmergency
                        )
                        LiquidSecondaryButton(
                            text = "Reset Drains",
                            modifier = Modifier.weight(1f),
                            onClick = onResetShowers
                        )
                    }
                }
            }
        }
    }
}
