package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.LiquidMainGlassPanel
import com.example.components.LiquidPrimaryButton
import com.example.components.LiquidSecondaryButton
import com.example.components.LiquidTabs
import com.example.components.LiquidToggleSwitch
import com.example.model.GoogleDirectionsResult
import com.example.model.GoogleMapLocation
import com.example.service.GoogleMapsService
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
import com.example.ui.theme.PrimaryPurpleGradient
import com.example.ui.theme.RainbowBorderGradient
import com.example.viewmodel.FloodUiState

enum class GoogleMapType(val label: String) {
    ROADMAP("Roadmap"),
    TERRAIN("Terrain"),
    DARK("Satellite/Night")
}

@Composable
fun GoogleMapsDataView(
    uiState: FloodUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedMapType by remember { mutableStateOf(GoogleMapType.ROADMAP) }
    var showTrafficLayer by remember { mutableStateOf(true) }
    var selectedRouteIndex by remember { mutableIntStateOf(0) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var isApiCopied by remember { mutableStateOf(false) }

    val routeCorridors = listOf(
        Pair(GoogleMapsService.CHENNAI_AIRPORT, GoogleMapsService.OMR_TIDEL_PARK),
        Pair(GoogleMapsService.KOYAMBEDU_CMBT, GoogleMapsService.CHENNAI_CENTRAL),
        Pair(GoogleMapsService.TNAGAR_USMAN_RD, GoogleMapsService.ADYAR_GANDHI_MANDAPAM)
    )

    val currentPair = routeCorridors[selectedRouteIndex]
    val directionsResult: GoogleDirectionsResult = remember(selectedRouteIndex, uiState.selectedHour, uiState.predictions) {
        GoogleMapsService.getGoogleDirections(
            origin = currentPair.first,
            destination = currentPair.second,
            predictions = uiState.predictions,
            forecastHour = uiState.selectedHour
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Panel: Headline + Google Maps Platform Badge + Corridor Picker
        LiquidMainGlassPanel(
            title = "Google Maps Live Data"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GOOGLE MAPS PLATFORM INTEGRATION",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4285F4),
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Directions, Traffic & Elevation APIs",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LiquidTextDark
                            )
                        )
                    }

                    // Google Maps Branding Pill
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFE8F0FE))
                            .border(1.dp, Color(0xFFD2E3FC), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = Color(0xFF1A73E8),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Maps Data OK",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A73E8)
                                )
                            )
                        }
                    }
                }

                // Corridor Tabs
                LiquidTabs(
                    tabs = listOf("Airport → OMR", "CMBT → Central", "T.Nagar → Adyar"),
                    selectedIndex = selectedRouteIndex,
                    onTabSelected = { selectedRouteIndex = it }
                )

                // Map Style & Traffic Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Map Type selector chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        GoogleMapType.values().forEach { type ->
                            val isSelected = selectedMapType == type
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) LiquidPrimaryPurpleStart else LiquidGlassWhite)
                                    .clickable { selectedMapType = type }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = type.label,
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) LiquidTextLight else LiquidTextDark
                                    )
                                )
                            }
                        }
                    }

                    // Traffic Layer Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LiquidGlassWhite)
                            .clickable { showTrafficLayer = !showTrafficLayer }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Traffic,
                            contentDescription = "Traffic",
                            tint = if (showTrafficLayer) LiquidSuccessGreen else LiquidTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (showTrafficLayer) "Traffic ON" else "Traffic OFF",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (showTrafficLayer) LiquidSuccessGreen else LiquidTextMuted
                            )
                        )
                    }
                }
            }
        }

        // Interactive Google Maps Styled Canvas View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .shadow(16.dp, RoundedCornerShape(LiquidGlassTokens.CardRadius), ambientColor = Color(0x221E1B4B))
                .clip(RoundedCornerShape(LiquidGlassTokens.CardRadius))
                .border(1.5.dp, Color(0x664285F4), RoundedCornerShape(LiquidGlassTokens.CardRadius))
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height

                // Background based on selected Map Type
                when (selectedMapType) {
                    GoogleMapType.ROADMAP -> {
                        // Google Maps classic light roadmap background (#F4F3F0)
                        drawRect(color = Color(0xFFF4F3F0))
                    }
                    GoogleMapType.TERRAIN -> {
                        // Google Maps contour relief shading (#EBE8DF)
                        drawRect(color = Color(0xFFEBE8DF))
                    }
                    GoogleMapType.DARK -> {
                        // Google Maps dark night theme (#242F3E)
                        drawRect(color = Color(0xFF242F3E))
                    }
                }

                // 1. Water features (Bay of Bengal coastline & Adyar / Cooum rivers)
                val waterColor = when (selectedMapType) {
                    GoogleMapType.DARK -> Color(0xFF17263C)
                    GoogleMapType.TERRAIN -> Color(0xFFA5BFDD)
                    else -> Color(0xFFAAD3DF) // Google Maps water blue
                }

                val oceanPath = Path().apply {
                    moveTo(w * 0.88f, 0f)
                    cubicTo(w * 0.86f, h * 0.35f, w * 0.90f, h * 0.7f, w * 0.86f, h)
                    lineTo(w, h)
                    lineTo(w, 0f)
                    close()
                }
                drawPath(oceanPath, waterColor)

                // Rivers
                val adyarRiver = Path().apply {
                    moveTo(0f, h * 0.62f)
                    cubicTo(w * 0.35f, h * 0.58f, w * 0.65f, h * 0.66f, w * 0.87f, h * 0.64f)
                }
                drawPath(adyarRiver, waterColor, style = Stroke(width = 8f * zoomLevel, cap = StrokeCap.Round))

                // 2. Google Maps Road Grid
                val highwayColor = when (selectedMapType) {
                    GoogleMapType.DARK -> Color(0xFF746855)
                    else -> Color(0xFFFBD894) // Google Maps arterial yellow highway
                }
                val localRoadColor = when (selectedMapType) {
                    GoogleMapType.DARK -> Color(0xFF38414E)
                    else -> Color(0xFFFFFFFF) // Google Maps white local street
                }

                // Local streets
                for (step in 0..6) {
                    val y = h * (step / 6f)
                    drawLine(localRoadColor, Offset(0f, y), Offset(w * 0.85f, y), strokeWidth = 3f * zoomLevel)
                }
                for (step in 0..5) {
                    val x = w * (step / 5f)
                    drawLine(localRoadColor, Offset(x, 0f), Offset(x, h), strokeWidth = 3f * zoomLevel)
                }

                // Major Arterials (GST Road / NH 32 & OMR)
                val gstRoadPath = Path().apply {
                    moveTo(w * 0.12f, h * 0.85f)
                    cubicTo(w * 0.25f, h * 0.65f, w * 0.35f, h * 0.45f, w * 0.5f, h * 0.35f)
                }
                drawPath(gstRoadPath, highwayColor, style = Stroke(width = 7f * zoomLevel, cap = StrokeCap.Round))

                val omrPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.35f)
                    cubicTo(w * 0.65f, h * 0.55f, w * 0.75f, h * 0.75f, w * 0.82f, h * 0.95f)
                }
                drawPath(omrPath, highwayColor, style = Stroke(width = 7f * zoomLevel, cap = StrokeCap.Round))

                // 3. Traffic Flow overlay (if enabled)
                if (showTrafficLayer) {
                    // Green free flow segment
                    drawLine(
                        color = Color(0xFF0F9D58),
                        start = Offset(w * 0.12f, h * 0.85f),
                        end = Offset(w * 0.3f, h * 0.6f),
                        strokeWidth = 4f * zoomLevel,
                        cap = StrokeCap.Round
                    )
                    // Standstill waterlogged red segment
                    if (directionsResult.isFlooded) {
                        drawLine(
                            color = Color(0xFFD93025),
                            start = Offset(w * 0.3f, h * 0.6f),
                            end = Offset(w * 0.65f, h * 0.65f),
                            strokeWidth = 6f * zoomLevel,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // 4. Routes: Direct (Red dashed if flooded) vs Safe Elevated Detour (Google Blue #4285F4)
                val originPt = Offset(w * 0.15f, h * 0.78f)
                val destPt = Offset(w * 0.75f, h * 0.68f)

                if (directionsResult.isFlooded) {
                    // Direct flooded path
                    val directPath = Path().apply {
                        moveTo(originPt.x, originPt.y)
                        quadraticTo(w * 0.45f, h * 0.8f, destPt.x, destPt.y)
                    }
                    drawPath(
                        path = directPath,
                        color = Color(0xFFEA4335), // Google Red
                        style = Stroke(width = 5f * zoomLevel, cap = StrokeCap.Round)
                    )

                    // Safe Detour Path (Google Blue #4285F4 with green glow)
                    val detourPath = Path().apply {
                        moveTo(originPt.x, originPt.y)
                        cubicTo(w * 0.3f, h * 0.25f, w * 0.65f, h * 0.25f, destPt.x, destPt.y)
                    }
                    drawPath(
                        path = detourPath,
                        color = Color(0xFF4285F4),
                        style = Stroke(width = 7f * zoomLevel, cap = StrokeCap.Round)
                    )
                } else {
                    // Clear Direct route
                    val directPath = Path().apply {
                        moveTo(originPt.x, originPt.y)
                        quadraticTo(w * 0.45f, h * 0.65f, destPt.x, destPt.y)
                    }
                    drawPath(
                        path = directPath,
                        color = Color(0xFF4285F4),
                        style = Stroke(width = 7f * zoomLevel, cap = StrokeCap.Round)
                    )
                }

                // 5. Origin & Destination Google Pins
                // Origin Green Pin
                drawCircle(color = Color(0xFF0F9D58), radius = 10f * zoomLevel, center = originPt)
                drawCircle(color = Color.White, radius = 5f * zoomLevel, center = originPt)

                // Destination Red Pin
                drawCircle(color = Color(0xFFEA4335), radius = 10f * zoomLevel, center = destPt)
                drawCircle(color = Color.White, radius = 5f * zoomLevel, center = destPt)

                // Flood obstruction marker if blocked
                if (directionsResult.isFlooded) {
                    val hazardPt = Offset(w * 0.45f, h * 0.78f)
                    drawCircle(color = Color(0xFFD93025), radius = 12f * zoomLevel, center = hazardPt)
                    drawCircle(color = Color.White, radius = 6f * zoomLevel, center = hazardPt)
                }
            }

            // Google Maps Floating Action Controls (Zoom in, Zoom out, My Location)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Zoom In
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { zoomLevel = (zoomLevel + 0.25f).coerceAtMost(2.5f) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = Color(0xFF5F6368), modifier = Modifier.size(18.dp))
                }

                // Zoom Out
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { zoomLevel = (zoomLevel - 0.25f).coerceAtLeast(0.75f) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color(0xFF5F6368), modifier = Modifier.size(18.dp))
                }

                // Re-center / My Location
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { zoomLevel = 1.0f },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Center", tint = Color(0xFF4285F4), modifier = Modifier.size(18.dp))
                }
            }

            // Google Maps Top Overlay Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCCFFFFFF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Google Maps • ${directionsResult.distanceKm} km (${directionsResult.durationMinutes} min)",
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF202124))
                    )
                }

                if (directionsResult.isFlooded) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFEBEE))
                            .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Flood Detour Active",
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD93025))
                        )
                    }
                }
            }
        }

        // Action Row: "Open in Google Maps" Button + "Copy Directions API JSON"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiquidPrimaryButton(
                text = "Open in Google Maps",
                icon = Icons.Default.Navigation,
                modifier = Modifier.weight(1f),
                onClick = {
                    GoogleMapsService.openGoogleMapsNavigation(context, directionsResult.googleMapsUrl)
                }
            )

            LiquidSecondaryButton(
                text = if (isApiCopied) "Copied!" else "Copy API JSON",
                icon = Icons.Default.Code,
                modifier = Modifier.weight(1f),
                onClick = {
                    clipboardManager.setText(AnnotatedString(directionsResult.rawGoogleDirectionsJson))
                    isApiCopied = true
                }
            )
        }

        // Turn-by-Turn Google Navigation Steps Card
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
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color(0xFF4285F4),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Google Maps Navigation Steps",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark)
                        )
                    }

                    Text(
                        text = "${directionsResult.navigationInstructions.size} steps",
                        style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted)
                    )
                }

                directionsResult.navigationInstructions.forEachIndexed { idx, step ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (step.contains("AVOID")) Color(0xFFFFCDD2) else Color(0xFFE8F0FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (step.contains("AVOID")) Color(0xFFD93025) else Color(0xFF1A73E8)
                                )
                            )
                        }

                        Text(
                            text = step,
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = if (step.contains("AVOID")) FontWeight.Bold else FontWeight.Normal,
                                color = if (step.contains("AVOID")) Color(0xFFD93025) else LiquidTextDark,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Google Maps Elevation API Profile Card
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
                            imageVector = Icons.Outlined.Terrain,
                            contentDescription = null,
                            tint = LiquidSecondaryTealStart,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Google Maps Elevation API Profile",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark)
                        )
                    }

                    Text(
                        text = "+8.5m Ridge Buffer",
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LiquidSuccessGreen)
                    )
                }

                Text(
                    text = "Elevation data along the detour ensures clearance above Chennai flood plains. High elevation corridors (Guindy 12.8m, Anna Nagar 9.8m) stay bone dry.",
                    style = TextStyle(fontSize = 12.sp, color = LiquidTextMuted, lineHeight = 16.sp)
                )

                // Elevation Bar Chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEDE9FE).copy(0.4f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    directionsResult.elevationProfileMeters.forEachIndexed { i, elevation ->
                        val barFraction = (elevation / 15.0).coerceIn(0.1, 1.0).toFloat()
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${elevation.toInt()}m",
                                style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LiquidTextDark)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .fillMaxWidth(barFraction)
                                    .height((60 * barFraction).dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (elevation < 3.0) Color(0xFFFF6B6B) else LiquidPrimaryPurpleStart
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
