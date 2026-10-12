package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.engine.HydrologicalAiEngine
import com.example.model.FloodPrediction
import com.example.model.HazardLevel
import com.example.model.RadarStatus
import com.example.model.RainfallIntensity
import com.example.model.StreetNode
import com.example.model.TrafficRerouteResult
import com.example.model.TrafficRouteOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FloodUiState(
    val selectedHour: Int = 1, // 0h, 1h, 2h, 3h
    val radarStatus: RadarStatus = RadarStatus(),
    val predictions: List<FloodPrediction> = emptyList(),
    val filteredPredictions: List<FloodPrediction> = emptyList(),
    val selectedStreet: FloodPrediction? = null,
    val selectedRouteOption: TrafficRouteOption = HydrologicalAiEngine.arterialRoutes.first(),
    val trafficRerouteResult: TrafficRerouteResult? = null,
    val searchQuery: String = "",
    val activeTab: Int = 0, // 0: Street Flood Forecast, 1: Live Map, 2: Traffic Reroute, 3: Radar & Drains, 4: Liquid Glass UI Kit
    val tidalLockOverride: Boolean = true,
    val siltationFactorPercent: Int = 65,
    val selectedLocalityFilter: String = "All Localities",
    val isReroutingSimulated: Boolean = false,
    val simulatedAlertBanner: String? = null
)

class FloodViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FloodUiState())
    val uiState: StateFlow<FloodUiState> = _uiState.asStateFlow()

    init {
        recomputePredictions()
    }

    fun setSelectedHour(hour: Int) {
        _uiState.update { it.copy(selectedHour = hour.coerceIn(0, 3)) }
        recomputeTrafficReroute()
    }

    fun setRainfallIntensity(intensity: RainfallIntensity) {
        _uiState.update {
            it.copy(
                radarStatus = it.radarStatus.copy(currentIntensity = intensity),
                simulatedAlertBanner = "Radar Intensity updated to ${intensity.title} (${intensity.rateMmHr} mm/hr)"
            )
        }
        recomputePredictions()
    }

    fun toggleTidalLock(active: Boolean) {
        _uiState.update {
            it.copy(
                tidalLockOverride = active,
                simulatedAlertBanner = if (active) "Tidal Lock ON: Bay of Bengal high tide blocks drain outfalls!" else "Tidal Lock OFF: Drains discharging at full gravity."
            )
        }
        recomputePredictions()
    }

    fun setSiltationFactor(silt: Int) {
        _uiState.update { it.copy(siltationFactorPercent = silt.coerceIn(0, 100)) }
        recomputePredictions()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) {
                applyLocalityFilter(state.predictions, state.selectedLocalityFilter)
            } else {
                state.predictions.filter {
                    it.street.name.contains(query, ignoreCase = true) ||
                            it.street.locality.contains(query, ignoreCase = true)
                }
            }
            state.copy(searchQuery = query, filteredPredictions = filtered)
        }
    }

    fun setLocalityFilter(locality: String) {
        _uiState.update { state ->
            val filtered = applyLocalityFilter(state.predictions, locality)
            state.copy(selectedLocalityFilter = locality, filteredPredictions = filtered)
        }
    }

    fun selectStreet(prediction: FloodPrediction?) {
        _uiState.update { it.copy(selectedStreet = prediction) }
    }

    fun selectRouteOption(option: TrafficRouteOption) {
        _uiState.update { it.copy(selectedRouteOption = option) }
        recomputeTrafficReroute()
    }

    fun setActiveTab(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(simulatedAlertBanner = null) }
    }

    fun triggerCityWideAlert() {
        setRainfallIntensity(RainfallIntensity.EXTREME_CYCLONE)
        toggleTidalLock(true)
        setSiltationFactor(80)
        _uiState.update {
            it.copy(
                simulatedAlertBanner = "⚠️ RED ALERT: Cyclone cloudburst triggered! 120 mm/hr rainfall + Tidal Lock active."
            )
        }
    }

    fun resetToNormalShowers() {
        setRainfallIntensity(RainfallIntensity.MODERATE)
        toggleTidalLock(false)
        setSiltationFactor(40)
        _uiState.update {
            it.copy(
                simulatedAlertBanner = "Cleared. City stormwater drains operating at normal capacity."
            )
        }
    }

    private fun recomputePredictions() {
        val currentState = _uiState.value
        val predictions = HydrologicalAiEngine.computeFloodPredictions(
            radarStatus = currentState.radarStatus,
            userSiltationOverride = currentState.siltationFactorPercent,
            userTidalLockOverride = currentState.tidalLockOverride
        )
        val filtered = if (currentState.searchQuery.isBlank()) {
            applyLocalityFilter(predictions, currentState.selectedLocalityFilter)
        } else {
            predictions.filter {
                it.street.name.contains(currentState.searchQuery, ignoreCase = true) ||
                        it.street.locality.contains(currentState.searchQuery, ignoreCase = true)
            }
        }

        _uiState.update {
            it.copy(
                predictions = predictions,
                filteredPredictions = filtered,
                selectedStreet = if (it.selectedStreet != null) {
                    predictions.find { p -> p.street.id == it.selectedStreet.street.id }
                } else null
            )
        }
        recomputeTrafficReroute()
    }

    private fun recomputeTrafficReroute() {
        val currentState = _uiState.value
        val rerouteResult = HydrologicalAiEngine.computeTrafficReroute(
            routeOption = currentState.selectedRouteOption,
            predictions = currentState.predictions,
            selectedHour = currentState.selectedHour
        )
        _uiState.update { it.copy(trafficRerouteResult = rerouteResult) }
    }

    private fun applyLocalityFilter(list: List<FloodPrediction>, locality: String): List<FloodPrediction> {
        return if (locality == "All Localities") {
            list
        } else {
            list.filter { it.street.locality.equals(locality, ignoreCase = true) }
        }
    }
}
