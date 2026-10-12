package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.LiquidAccentPurple
import com.example.ui.theme.LiquidDangerRed
import com.example.ui.theme.LiquidSecondaryTealStart
import com.example.ui.theme.LiquidSuccessGreen
import com.example.ui.theme.LiquidWarningAmber

enum class HazardLevel(
    val label: String,
    val color: Color,
    val maxSafeSpeedKmH: Int,
    val description: String
) {
    SAFE("Safe", LiquidSuccessGreen, 50, "Dry tarmac. All vehicles permitted."),
    LOW("Low Risk", LiquidSecondaryTealStart, 40, "Mild surface puddling (<15cm). Safe with care."),
    MODERATE("Waterlogged", LiquidWarningAmber, 20, "15-30cm. Two-wheelers & low hatchbacks risk stall."),
    SEVERE("Flooded", Color(0xFFFF6B6B), 0, "30-60cm. Roads closed to non-emergency vehicles."),
    CRITICAL("Submerged", LiquidDangerRed, 0, ">60cm. Total inundation. Evacuation/Boats only.")
}

enum class RainfallIntensity(
    val title: String,
    val rateMmHr: Double,
    val radarReflectivityDbz: Int,
    val description: String
) {
    SCATTERED("Scattered Showers", 18.0, 32, "Localized light rain, drains coping."),
    MODERATE("Monsoon Showers", 42.0, 44, "Continuous rain, drains at 70% capacity."),
    HEAVY("Heavy Downpour", 75.0, 52, "Severe storm cloud burst, low-lying street overflow."),
    EXTREME_CYCLONE("Cyclone Surge", 120.0, 60, "Extreme IMD Red Alert, tidal lock + flash inundation.")
}

data class StreetNode(
    val id: String,
    val name: String,
    val locality: String,
    val elevationMeters: Double, // ISRO Cartosat DEM elevation
    val baseDrainCapacityM3s: Double, // Stormwater drain discharge capacity m3/s
    val drainSiltationPercent: Int, // Siltation level
    val drainOutfall: String, // Outfall basin / river
    val isUnderpass: Boolean = false,
    val catchmentAreaSqKm: Double = 1.2,
    val lat: Double,
    val lon: Double,
    val normalTravelMinutes: Int = 8
)

data class FloodPrediction(
    val street: StreetNode,
    val depthAt0hCm: Double,
    val depthAt1hCm: Double,
    val depthAt2hCm: Double,
    val depthAt3hCm: Double,
    val timeToFloodMinutes: Int, // Minutes until street hits >15cm (waterlogged)
    val hazardLevel: HazardLevel,
    val floodRankOrder: Int, // 1 = Floods first!
    val projectedPeakCm: Double,
    val advisory: String
) {
    fun depthAtHour(hour: Int): Double {
        return when (hour) {
            0 -> depthAt0hCm
            1 -> depthAt1hCm
            2 -> depthAt2hCm
            3 -> depthAt3hCm
            else -> depthAt3hCm
        }
    }
}

data class RadarStatus(
    val stationName: String = "IMD Chennai Doppler Radar (Meenambakkam)",
    val band: String = "S-Band Doppler (2.8 GHz)",
    val currentIntensity: RainfallIntensity = RainfallIntensity.HEAVY,
    val stormHeadingDegrees: Int = 78,
    val stormSpeedKmh: Int = 34,
    val accumulatedRainfallMm: Double = 64.5,
    val tidalLockActive: Boolean = true, // High tide impeding gravity outfall to Bay of Bengal
    val tideHeightMeters: Double = 1.35,
    val lastSweepTimestamp: String = "10 mins ago"
)

data class TrafficRouteOption(
    val routeId: String,
    val origin: String,
    val destination: String,
    val directStreetNames: List<String>,
    val detourStreetNames: List<String>,
    val directDistanceKm: Double,
    val directEtaMinutes: Int,
    val detourDistanceKm: Double,
    val detourEtaMinutes: Int,
    val elevatedRidgePath: String
)

data class TrafficRerouteResult(
    val option: TrafficRouteOption,
    val isDirectFlooded: Boolean,
    val floodedStreets: List<String>,
    val maxInundationCm: Double,
    val safeAlternativeName: String,
    val detourTimeDeltaMinutes: Int,
    val detourClearanceMeters: Double,
    val rerouteRecommended: Boolean,
    val apiResponseJson: String
)
