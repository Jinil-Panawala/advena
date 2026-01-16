package com.example.advena.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.advena.domain.Event
import com.example.advena.domain.EventFilter
import com.example.advena.domain.Model
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.advena.utilities.MathUtils



data class HomeUiState(
    val userLocation: LatLng? = null,
    val visibleEvents: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val locationPermissionGranted: Boolean = false,
    val filterStartDate: String? = LocalDate.now().toString(),
    val filterEndDate: String? = LocalDate.now().toString(),
    val filterGroupSize: Int? = null,
    val filterCost: Int? = null,
    val search: String = ""
)

class HomeViewModel(model: Model) : BaseViewModel(model) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onLocationPermissionGranted() {
        _uiState.value = _uiState.value.copy(locationPermissionGranted = true)
    }

    fun updateUserLocation(location: LatLng) {
        _uiState.value = _uiState.value.copy(userLocation = location)
    }

    fun onSearchChange(newValue: String, currentMapBounds: LatLngBounds?) {
        _uiState.value = _uiState.value.copy(search = newValue)
        // Reload events with the new search filter
        currentMapBounds?.let { bounds ->
            loadEventsInBounds(bounds)
        }
    }

    fun updateFilters(startDate: String?, endDate: String?, groupSize: Int?, cost: Int?, currentMapBounds: LatLngBounds? = null) {
        try {
            val inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

            val formattedStart = startDate?.let {
                try {
                    val parsedDate = LocalDate.parse(it, inputFormatter)
                    parsedDate.toString()
                } catch (_: Exception) {
                    null
                }
            }

            val formattedEnd = endDate?.let {
                try {
                    val parsedDate = LocalDate.parse(it, inputFormatter)
                    parsedDate.toString()
                } catch (_: Exception) {
                    null
                }
            }

            _uiState.value = _uiState.value.copy(
                filterStartDate = formattedStart,
                filterEndDate = formattedEnd,
                filterGroupSize = groupSize,
                filterCost = cost
            )

            // Re-filter visible events based on new filters using current map bounds
            currentMapBounds?.let { bounds ->
                loadEventsInBounds(bounds)
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Error updating filters: ${e.message}"
            )
        }
    }

    fun loadEventsInBounds(bounds: LatLngBounds) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val filters = buildFilters(bounds)

                model.getFilteredEvents(loggedInUserId, filters).collect { events ->
                    // Apply search filter to the events
                    val filteredEvents = applySearchFilter(events, _uiState.value.search)

                    _uiState.value = _uiState.value.copy(
                        visibleEvents = filteredEvents,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error filtering events: ${e.message}"
                )
            }
        }
    }

    private fun buildFilters(bounds: LatLngBounds): List<EventFilter> {
        val filters = mutableListOf<EventFilter>()

        // Add location filter based on map bounds
        // Calculate center and radius from bounds
        val center = bounds.center
        val radius = calculateRadiusFromBounds(bounds)
        filters.add(EventFilter.ByLocation(
            longitude = center.longitude,
            latitude = center.latitude,
            radiusKm = radius
        ))

        // Add date range filter if specified (support single-sided ranges)
        if (_uiState.value.filterStartDate != null || _uiState.value.filterEndDate != null) {
            val start = _uiState.value.filterStartDate ?: _uiState.value.filterEndDate!!
            val end = _uiState.value.filterEndDate ?: _uiState.value.filterStartDate!!
            filters.add(EventFilter.ByDateRange(
                startDate = start,
                endDate = end
            ))
        }

        // Add group size filter if specified
        _uiState.value.filterGroupSize?.let { groupSize ->
            filters.add(EventFilter.ByMaxAttendees(maxAttendees = groupSize))
        }

        // Add cost filter if specified
        _uiState.value.filterCost?.let { cost ->
            filters.add(EventFilter.ByCost(maxCost = cost.toDouble()))
        }

        return filters
    }

    /**
     * Calculate approximate radius in kilometers from map bounds.
     * Uses the diagonal distance from center to corner as radius.
     */
    private fun calculateRadiusFromBounds(bounds: LatLngBounds): Double {
        val center = bounds.center
        val northeast = bounds.northeast

        return MathUtils.haversineDistanceKm(
            center.latitude,
            center.longitude,
            northeast.latitude,
            northeast.longitude
        )
    }

    fun setError(message: String?) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }

    fun handleRSVP(event: Event) {
        handleRSVPWithCallback(
            event = event,
            onStart = { _uiState.value = _uiState.value.copy(isLoading = true) },
            onComplete = { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null) },
            onError = { errorMsg ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "RSVP failed: $errorMsg"
                )
            }
        )
    }

    fun deleteEvent(event: Event) {
        deleteEventWithCallback(
            event,
            onStart = { _uiState.value = _uiState.value.copy(isLoading = true) },
            onComplete = {
                _uiState.value = _uiState.value.copy(
                    visibleEvents = _uiState.value.visibleEvents.filter { it.id != event.id },
                    isLoading = false,
                    errorMessage = null
                )
            },

            onError = { errorMsg ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Delete failed: $errorMsg"
                )
            }
        )
    }
}
