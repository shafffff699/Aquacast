package com.example.model

data class GoogleMapLocation(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val address: String = "",
    val placeId: String = ""
)

enum class TrafficCongestion(val label: String, val speedKmh: Int, val hexColor: Long) {
    FREE_FLOW("Free Flow", 50, 0xFF34C759),
    SLUGGISH("Moderate Traffic", 25, 0xFFFF9500),
    HEAVY_CONGESTION("Heavy Congestion", 10, 0xFFFF3B30),
    WATERLOGGED_STANDSTILL("Waterlogged Standstill", 0, 0xFF990000)
}

data class GoogleMapsRouteSegment(
    val start: GoogleMapLocation,
    val end: GoogleMapLocation,
    val roadName: String,
    val polylinePoints: List<GoogleMapLocation>,
    val distanceMeters: Int,
    val durationSeconds: Int,
    val elevationMeters: Double, // Google Maps Elevation API
    val currentTraffic: TrafficCongestion,
    val floodInundationCm: Double = 0.0
)

data class GoogleDirectionsResult(
    val routeId: String,
    val origin: GoogleMapLocation,
    val destination: GoogleMapLocation,
    val routeTitle: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val isFlooded: Boolean,
    val floodedStreetNames: List<String>,
    val maxInundationCm: Double,
    val segments: List<GoogleMapsRouteSegment>,
    val detourWaypoints: List<GoogleMapLocation>,
    val googleMapsUrl: String,
    val elevationProfileMeters: List<Double>,
    val navigationInstructions: List<String>,
    val rawGoogleDirectionsJson: String
)
