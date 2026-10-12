package com.example.engine

import com.example.model.FloodPrediction
import com.example.model.HazardLevel
import com.example.model.RadarStatus
import com.example.model.RainfallIntensity
import com.example.model.StreetNode
import com.example.model.TrafficRerouteResult
import com.example.model.TrafficRouteOption
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object HydrologicalAiEngine {

    // Predefined Chennai Street Network with ISRO Cartosat DEM Elevations & Stormwater Drain Basins
    val chennaiStreetNetwork = listOf(
        StreetNode(
            id = "chennai_madipakkam_lake",
            name = "Madipakkam Lake Road",
            locality = "Madipakkam",
            elevationMeters = 1.8, // Encroached lakebed depression
            baseDrainCapacityM3s = 3.2,
            drainSiltationPercent = 75,
            drainOutfall = "Pallikaranai Marshland",
            isUnderpass = false,
            catchmentAreaSqKm = 1.6,
            lat = 12.9644,
            lon = 80.1989,
            normalTravelMinutes = 12
        ),
        StreetNode(
            id = "chennai_perambur_subway",
            name = "Perambur Ganesapuram Subway",
            locality = "Perambur",
            elevationMeters = 1.5, // Depressed rail underpass
            baseDrainCapacityM3s = 2.1,
            drainSiltationPercent = 65,
            drainOutfall = "Otteri Nullah",
            isUnderpass = true,
            catchmentAreaSqKm = 0.9,
            lat = 13.1098,
            lon = 80.2452,
            normalTravelMinutes = 8
        ),
        StreetNode(
            id = "chennai_velachery_bypass",
            name = "Velachery Bypass & Inner Ring Rd",
            locality = "Velachery",
            elevationMeters = 2.2, // Natural marsh basin
            baseDrainCapacityM3s = 4.5,
            drainSiltationPercent = 60,
            drainOutfall = "Velachery Canal to Pallikaranai",
            isUnderpass = false,
            catchmentAreaSqKm = 2.2,
            lat = 12.9790,
            lon = 80.2185,
            normalTravelMinutes = 15
        ),
        StreetNode(
            id = "chennai_tnagar_usman",
            name = "South Usman Road & Ranganathan St",
            locality = "T. Nagar",
            elevationMeters = 4.1, // Commercial basin
            baseDrainCapacityM3s = 4.0,
            drainSiltationPercent = 70,
            drainOutfall = "Mambalam Canal",
            isUnderpass = false,
            catchmentAreaSqKm = 1.8,
            lat = 13.0398,
            lon = 80.2325,
            normalTravelMinutes = 14
        ),
        StreetNode(
            id = "chennai_omr_perungudi",
            name = "OMR Expressway (Perungudi Jn)",
            locality = "OMR IT Corridor",
            elevationMeters = 3.2, // Canal parallel
            baseDrainCapacityM3s = 5.2,
            drainSiltationPercent = 45,
            drainOutfall = "Buckingham Canal",
            isUnderpass = false,
            catchmentAreaSqKm = 2.5,
            lat = 12.9654,
            lon = 80.2435,
            normalTravelMinutes = 18
        ),
        StreetNode(
            id = "chennai_koyambedu_market",
            name = "Koyambedu 100ft Road",
            locality = "Koyambedu",
            elevationMeters = 6.4,
            baseDrainCapacityM3s = 4.8,
            drainSiltationPercent = 50,
            drainOutfall = "Cooum River Basin",
            isUnderpass = false,
            catchmentAreaSqKm = 2.0,
            lat = 13.0694,
            lon = 80.1948,
            normalTravelMinutes = 16
        ),
        StreetNode(
            id = "chennai_adyar_bridge",
            name = "Gandhi Mandapam Rd (Adyar River)",
            locality = "Adyar",
            elevationMeters = 5.6,
            baseDrainCapacityM3s = 6.0,
            drainSiltationPercent = 40,
            drainOutfall = "Adyar River Estuary",
            isUnderpass = false,
            catchmentAreaSqKm = 1.5,
            lat = 13.0076,
            lon = 80.2442,
            normalTravelMinutes = 10
        ),
        StreetNode(
            id = "chennai_mylapore_luz",
            name = "Luz Church Road & Royapettah",
            locality = "Mylapore",
            elevationMeters = 7.1, // Historic beach ridge
            baseDrainCapacityM3s = 4.0,
            drainSiltationPercent = 35,
            drainOutfall = "Buckingham Canal",
            isUnderpass = false,
            catchmentAreaSqKm = 1.1,
            lat = 13.0352,
            lon = 80.2641,
            normalTravelMinutes = 12
        ),
        StreetNode(
            id = "chennai_annanagar_2ndave",
            name = "Anna Nagar 2nd Avenue",
            locality = "Anna Nagar",
            elevationMeters = 9.8, // High plateau, well drained
            baseDrainCapacityM3s = 6.5,
            drainSiltationPercent = 25,
            drainOutfall = "Otteri Nullah Channel",
            isUnderpass = false,
            catchmentAreaSqKm = 1.4,
            lat = 13.0850,
            lon = 80.2101,
            normalTravelMinutes = 10
        ),
        StreetNode(
            id = "chennai_guindy_kathipara",
            name = "Kathipara Junction & GST Road",
            locality = "Guindy",
            elevationMeters = 12.8, // Solid high bedrock ridge
            baseDrainCapacityM3s = 7.0,
            drainSiltationPercent = 20,
            drainOutfall = "Adyar Upper Basin",
            isUnderpass = false,
            catchmentAreaSqKm = 1.7,
            lat = 13.0067,
            lon = 80.2050,
            normalTravelMinutes = 10
        )
    )

    // Predefined Chennai Arterial Traffic Routes
    val arterialRoutes = listOf(
        TrafficRouteOption(
            routeId = "route_airport_to_omr",
            origin = "Chennai Airport (Meenambakkam)",
            destination = "Tidel Park / OMR IT Corridor",
            directStreetNames = listOf("Kathipara Junction", "Madipakkam Lake Road", "Velachery Bypass & Inner Ring Rd", "OMR Expressway (Perungudi Jn)"),
            detourStreetNames = listOf("Kathipara Junction & GST Road", "Guindy Industrial Estate", "Gandhi Mandapam Rd (Adyar River)", "Sardar Patel Road", "OMR Tidel Corridor"),
            directDistanceKm = 14.2,
            directEtaMinutes = 32,
            detourDistanceKm = 17.5,
            detourEtaMinutes = 38,
            elevatedRidgePath = "Via Guindy & Sardar Patel Ridge (+8.5m elevation buffer)"
        ),
        TrafficRouteOption(
            routeId = "route_koyambedu_to_central",
            origin = "Koyambedu CMBT Terminal",
            destination = "Chennai Central Railway Station",
            directStreetNames = listOf("Koyambedu 100ft Road", "Perambur Ganesapuram Subway", "EVR Periyar Salai"),
            detourStreetNames = listOf("Anna Nagar 2nd Avenue", "Kilpauk Garden Road", "Poonamallee High Road Flyover"),
            directDistanceKm = 11.5,
            directEtaMinutes = 28,
            detourDistanceKm = 13.8,
            detourEtaMinutes = 33,
            elevatedRidgePath = "Via Anna Nagar High Plateau (bypassing Perambur Subway)"
        ),
        TrafficRouteOption(
            routeId = "route_tnagar_to_adyar",
            origin = "T. Nagar Shopping Hub",
            destination = "Adyar Malar Hospital",
            directStreetNames = listOf("South Usman Road & Ranganathan St", "Saidapet Bridge", "Velachery Bypass"),
            detourStreetNames = listOf("Luz Church Road & Royapettah", "Mylapore Beach Road", "Gandhi Mandapam Rd (Adyar River)"),
            directDistanceKm = 8.4,
            directEtaMinutes = 22,
            detourDistanceKm = 10.1,
            detourEtaMinutes = 25,
            elevatedRidgePath = "Via Mylapore Coastal Ridge (+4.2m elevation)"
        )
    )

    /**
     * Hydrological AI simulation fusing:
     * - Radar Precipitation Rate (mm/hr)
     * - ISRO DEM Elevation (depression vs ridge)
     * - Stormwater Pipe discharge capacity (Manning's flow)
     * - Siltation factor and Bay of Bengal tidal lock
     */
    fun computeFloodPredictions(
        radarStatus: RadarStatus,
        userSiltationOverride: Int? = null,
        userTidalLockOverride: Boolean? = null
    ): List<FloodPrediction> {
        val intensityMmHr = radarStatus.currentIntensity.rateMmHr
        val tidalLock = userTidalLockOverride ?: radarStatus.tidalLockActive
        val tidalEfficiencyFactor = if (tidalLock) 0.42 else 0.95 // High tide locks gravity outfalls

        val predictions = chennaiStreetNetwork.map { street ->
            val siltPercent = userSiltationOverride ?: street.drainSiltationPercent
            val effectiveDrainDischarge = street.baseDrainCapacityM3s *
                    (1.0 - (siltPercent / 100.0) * 0.7) *
                    tidalEfficiencyFactor

            // Net inflow rate = Rainfall on catchment + terrain gravity runoff from upstream
            // Elevation lower than 4m receives upstream gravitational run-off
            val terrainInflowFactor = when {
                street.isUnderpass -> 2.8 // Underpass receives massive funnel runoff
                street.elevationMeters < 2.5 -> 2.1 // Natural marsh bowl
                street.elevationMeters < 4.5 -> 1.4
                street.elevationMeters < 7.0 -> 0.9
                else -> 0.5 // High elevation discharges easily
            }

            // Hydrological accumulation rate in cm/hr
            val netRiseRateCmHr = max(0.0, (intensityMmHr * 0.1 * terrainInflowFactor) - (effectiveDrainDischarge * 0.45))

            // Depth calculation across 0 to 3 hours
            val depth0h = max(0.0, min(120.0, netRiseRateCmHr * 0.25)) // onset at 15 mins
            val depth1h = max(0.0, min(140.0, netRiseRateCmHr * 1.0))
            val depth2h = max(0.0, min(160.0, netRiseRateCmHr * 2.0))
            val depth3h = max(0.0, min(180.0, netRiseRateCmHr * 3.0))

            // Time to flood (minutes to exceed 15cm road hazard threshold)
            val timeToFloodMinutes = if (netRiseRateCmHr <= 0.05) {
                999 // Safe, does not flood
            } else {
                val mins = ((15.0 / netRiseRateCmHr) * 60).roundToInt()
                max(6, mins)
            }

            // Peak hazard level across the 3-hour forecast
            val peakDepth = depth3h
            val hazardLevel = when {
                peakDepth < 8.0 -> HazardLevel.SAFE
                peakDepth < 18.0 -> HazardLevel.LOW
                peakDepth < 35.0 -> HazardLevel.MODERATE
                peakDepth < 60.0 -> HazardLevel.SEVERE
                else -> HazardLevel.CRITICAL
            }

            val advisory = when (hazardLevel) {
                HazardLevel.SAFE -> "Free-flowing. Terrain elevation keeps road clear."
                HazardLevel.LOW -> "Minor street-edge puddling. Normal speeds allowed."
                HazardLevel.MODERATE -> "Waterlogging expected. Divert two-wheelers to elevated corridors."
                HazardLevel.SEVERE -> "Severe inundation. Road closed to light vehicles. City pumps active."
                HazardLevel.CRITICAL -> "Complete road submersion. Submerged culverts. Reroute mandatory."
            }

            FloodPrediction(
                street = street,
                depthAt0hCm = ((depth0h * 10).roundToInt()) / 10.0,
                depthAt1hCm = ((depth1h * 10).roundToInt()) / 10.0,
                depthAt2hCm = ((depth2h * 10).roundToInt()) / 10.0,
                depthAt3hCm = ((depth3h * 10).roundToInt()) / 10.0,
                timeToFloodMinutes = timeToFloodMinutes,
                hazardLevel = hazardLevel,
                floodRankOrder = 0, // Will be sorted below
                projectedPeakCm = ((peakDepth * 10).roundToInt()) / 10.0,
                advisory = advisory
            )
        }

        // Sort by which street floods first (smallest timeToFloodMinutes)
        val sorted = predictions.sortedWith(
            compareBy<FloodPrediction> { it.timeToFloodMinutes }
                .thenByDescending { it.depthAt3hCm }
        )

        return sorted.mapIndexed { index, item ->
            item.copy(floodRankOrder = index + 1)
        }
    }

    /**
     * Compute Traffic Reroute recommendation for a selected arterial route
     */
    fun computeTrafficReroute(
        routeOption: TrafficRouteOption,
        predictions: List<FloodPrediction>,
        selectedHour: Int = 1
    ): TrafficRerouteResult {
        val streetPredictionMap = predictions.associateBy { it.street.name }

        val floodedStreets = mutableListOf<String>()
        var maxInundation = 0.0

        for (streetName in routeOption.directStreetNames) {
            val pred = streetPredictionMap[streetName]
            if (pred != null) {
                val depth = pred.depthAtHour(selectedHour)
                if (depth > maxInundation) maxInundation = depth
                if (depth >= 15.0) { // Hazard threshold: 15cm
                    floodedStreets.add("${streetName} (${depth}cm water)")
                }
            }
        }

        val isFlooded = floodedStreets.isNotEmpty()
        val rerouteRecommended = isFlooded

        val timeDelta = routeOption.detourEtaMinutes - routeOption.directEtaMinutes
        val clearanceAdvantage = 6.4 // Average meters above sea level advantage

        val apiJson = """
        {
          "status": "SUCCESS",
          "route_id": "${routeOption.routeId}",
          "forecast_horizon_hours": $selectedHour,
          "alert": "${if (isFlooded) "REROUTE_DISPATCHED" else "CLEAR"}",
          "direct_path_passable": ${!isFlooded},
          "max_direct_inundation_cm": $maxInundation,
          "flooded_segments": [${floodedStreets.joinToString(", ") { "\"$it\"" }}],
          "recommended_detour": {
            "name": "${routeOption.elevatedRidgePath}",
            "distance_km": ${routeOption.detourDistanceKm},
            "estimated_eta_min": ${routeOption.detourEtaMinutes},
            "terrain_clearance_meters": $clearanceAdvantage
          },
          "generated_by": "AquaCast AI Hydrology Engine v2.4 (IMD + ISRO DEM)"
        }
        """.trimIndent()

        return TrafficRerouteResult(
            option = routeOption,
            isDirectFlooded = isFlooded,
            floodedStreets = floodedStreets,
            maxInundationCm = ((maxInundation * 10).roundToInt()) / 10.0,
            safeAlternativeName = routeOption.elevatedRidgePath,
            detourTimeDeltaMinutes = timeDelta,
            detourClearanceMeters = clearanceAdvantage,
            rerouteRecommended = rerouteRecommended,
            apiResponseJson = apiJson
        )
    }
}
