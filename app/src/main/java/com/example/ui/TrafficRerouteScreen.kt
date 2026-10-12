package com.example.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Terrain
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.LiquidMainGlassPanel
import com.example.components.LiquidPrimaryButton
import com.example.components.LiquidSecondaryButton
import com.example.components.LiquidSuccessChip
import com.example.engine.HydrologicalAiEngine
import com.example.model.TrafficRouteOption
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
import com.example.ui.theme.RainbowBorderGradient
import com.example.viewmodel.FloodUiState

@Composable
fun TrafficRerouteScreen(
    uiState: FloodUiState,
    onSelectRoute: (TrafficRouteOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val result = uiState.trafficRerouteResult
    val context = LocalContext.current
    var isApiCopied by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Panel: Headline & Corridor Selector
        LiquidMainGlassPanel(
            title = "Traffic Reroute Engine"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Real-time municipal rerouting fusing street-by-street inundation with terrain elevation to detour around floodwater.",
                    style = TextStyle(fontSize = 13.sp, color = LiquidTextMuted, lineHeight = 18.sp)
                )

                // Route options picker
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select Transit Corridor:",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LiquidTextDark)
                    )

                    HydrologicalAiEngine.arterialRoutes.forEach { route ->
                        val isSelected = route.routeId == uiState.selectedRouteOption.routeId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) LiquidPrimaryPurpleStart.copy(alpha = 0.2f)
                                    else LiquidGlassWhite
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) LiquidAccentPurple else LiquidBorderWhiteSubtle,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { onSelectRoute(route) }
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${route.origin} → ${route.destination}",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = LiquidTextDark
                                        )
                                    )
                                    Text(
                                        text = "Normal: ${route.directDistanceKm}km (${route.directEtaMinutes}m) • Detour: ${route.detourDistanceKm}km",
                                        style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted)
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(LiquidAccentPurple),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Navigation,
                                            contentDescription = "Active",
                                            tint = LiquidTextLight,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Reroute Assessment Banner
        if (result != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(LiquidGlassTokens.CardRadius),
                        ambientColor = if (result.isDirectFlooded) Color(0x33FF3B30) else Color(0x3334C759)
                    )
                    .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
                    .background(
                        if (result.isDirectFlooded)
                            Brush.linearGradient(listOf(Color(0xFFFFF0F3), Color(0xFFFFE0E6)))
                        else
                            Brush.linearGradient(listOf(Color(0xFFF0FFF4), Color(0xFFDCFCE7)))
                    )
                    .border(
                        1.5.dp,
                        if (result.isDirectFlooded) Color(0xFFFFB3BA) else Color(0xFF86EFAC),
                        RoundedCornerShape(LiquidGlassTokens.CardRadius)
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (result.isDirectFlooded) LiquidDangerRed else LiquidSuccessGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (result.isDirectFlooded) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = LiquidTextLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = if (result.isDirectFlooded) "REROUTE MANDATORY" else "DIRECT CORRIDOR CLEAR",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (result.isDirectFlooded) LiquidDangerRed else LiquidSuccessGreen,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }

                        Text(
                            text = "+${uiState.selectedHour}h Forecast",
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark)
                        )
                    }

                    if (result.isDirectFlooded) {
                        Text(
                            text = "Direct corridor blocked by floodwaters on:",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = LiquidTextDark)
                        )

                        result.floodedStreets.forEach { street ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(LiquidDangerRed)
                                )
                                Text(
                                    text = street,
                                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LiquidDangerRed)
                                )
                            }
                        }

                        // Detour recommendation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(LiquidSolidSoftWhite)
                                .border(1.dp, Color(0x80FFFFFF), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "RECOMMENDED SAFE DETOUR",
                                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LiquidAccentPurple)
                                )
                                Text(
                                    text = result.safeAlternativeName,
                                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark)
                                )
                                Text(
                                    text = "Detour ETA: ${result.option.detourEtaMinutes} mins (+${result.detourTimeDeltaMinutes}m) • Avoids all submerged streets via elevated ridges.",
                                    style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LiquidPrimaryButton(
                                    text = "Navigate with Google Maps",
                                    icon = Icons.Default.Navigation,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        val gmapsUrl = "https://www.google.com/maps/dir/?api=1&origin=" +
                                                android.net.Uri.encode(result.option.origin + ", Chennai") +
                                                "&destination=" + android.net.Uri.encode(result.option.destination + ", Chennai") +
                                                "&travelmode=driving"
                                        com.example.service.GoogleMapsService.openGoogleMapsNavigation(context, gmapsUrl)
                                    }
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "All segments along this direct corridor remain below critical 15cm threshold at current rainfall horizon. Traffic flowing normally.",
                            style = TextStyle(fontSize = 13.sp, color = LiquidTextDark)
                        )
                    }
                }
            }

            // Route Visualization Canvas (Interactive Detour vs Flooded Corridor)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
                    .background(Color(0xFF1E1B4B))
                    .border(1.5.dp, Color(0x66A78BFA), RoundedCornerShape(LiquidGlassTokens.CardRadius))
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height

                    // Background grid
                    val gridColor = Color(0x1AFFFFFF)
                    for (x in 0..w.toInt() step 60) {
                        drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), h), 1f)
                    }
                    for (y in 0..h.toInt() step 60) {
                        drawLine(gridColor, Offset(0f, y.toFloat()), Offset(w, y.toFloat()), 1f)
                    }

                    val startPt = Offset(50f, h * 0.75f)
                    val endPt = Offset(w - 50f, h * 0.35f)

                    // Direct route (Red/dashed flooded line)
                    val directPath = Path().apply {
                        moveTo(startPt.x, startPt.y)
                        quadraticTo(w * 0.45f, h * 0.85f, endPt.x, endPt.y)
                    }
                    drawPath(
                        path = directPath,
                        color = if (result.isDirectFlooded) Color(0xFFFF4D4D) else Color(0xFF60A5FA),
                        style = Stroke(width = 6f, cap = StrokeCap.Round)
                    )

                    // Elevated Detour Route (Cyan/Purple Glowing Line)
                    if (result.isDirectFlooded) {
                        val detourPath = Path().apply {
                            moveTo(startPt.x, startPt.y)
                            cubicTo(
                                w * 0.25f, h * 0.15f,
                                w * 0.75f, h * 0.15f,
                                endPt.x, endPt.y
                            )
                        }
                        drawPath(
                            path = detourPath,
                            brush = Brush.horizontalGradient(listOf(LiquidSecondaryTealStart, LiquidPrimaryPurpleStart)),
                            style = Stroke(width = 7f, cap = StrokeCap.Round)
                        )
                    }

                    // Start & End Node Circles
                    drawCircle(color = Color(0xFF34C759), radius = 10f, center = startPt)
                    drawCircle(color = Color.White, radius = 5f, center = startPt)

                    drawCircle(color = Color(0xFFA78BFA), radius = 10f, center = endPt)
                    drawCircle(color = Color.White, radius = 5f, center = endPt)

                    // Flooded point marker
                    if (result.isDirectFlooded) {
                        val floodHazardPt = Offset(w * 0.5f, h * 0.8f)
                        drawCircle(color = Color(0xFFFF3B30), radius = 12f, center = floodHazardPt)
                        drawCircle(color = Color.White, radius = 6f, center = floodHazardPt)
                    }
                }

                // Overlay labels
                Column(
                    modifier = Modifier.align(Alignment.TopStart),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "● Origin: ${result.option.origin}",
                        style = TextStyle(fontSize = 11.sp, color = Color(0xFF34C759), fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "● Detour: Elevated Ridge (+8.5m MSL)",
                        style = TextStyle(fontSize = 11.sp, color = LiquidSecondaryTealStart, fontWeight = FontWeight.Bold)
                    )
                    if (result.isDirectFlooded) {
                        Text(
                            text = "✖ Direct: Submerged Hotspots",
                            style = TextStyle(fontSize = 11.sp, color = Color(0xFFFF4D4D), fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Real-Time Smart City API Payload Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(LiquidGlassTokens.CardRadius), ambientColor = Color(0x1F000000))
                    .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
                    .background(LiquidSolidSoftWhite)
                    .border(1.dp, LiquidBorderWhite, RoundedCornerShape(LiquidGlassTokens.CardRadius))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                imageVector = Icons.Default.Code,
                                contentDescription = "API",
                                tint = LiquidAccentPurple,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Traffic API Response (JSON)",
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isApiCopied) LiquidSuccessGreen else LiquidGlassWhite)
                                .clickable { isApiCopied = true }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isApiCopied) "Copied!" else "Copy JSON",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isApiCopied) LiquidTextLight else LiquidAccentPurple
                                )
                            )
                        }
                    }

                    // JSON Code Block
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1F29))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = result.apiResponseJson,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF8EE0D6),
                                lineHeight = 16.sp
                            )
                        )
                    }

                    Text(
                        text = "This API payload feeds directly into municipal traffic control centers and GPS navigation systems to divert incoming cars before they reach inundated roads.",
                        style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted)
                    )
                }
            }
        }
    }
}
