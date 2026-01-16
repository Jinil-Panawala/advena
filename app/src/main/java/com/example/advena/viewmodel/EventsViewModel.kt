package com.example.advena.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.advena.domain.Event
import com.example.advena.domain.EventFilter
import com.example.advena.domain.Model
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel for the Events screen.
 * Manages UI state and user interactions for event listing, filtering, and viewing details.
 */
class EventsViewModel(model: Model) : BaseViewModel(model) {


    // UI State for the Events Screen
    var uiState by mutableStateOf(EventsUiState())
        private set

    private var allEvents: List<Event> = emptyList()

    // ===== UI Actions =====

    /**
     * Loads the user's RSVP'd events and events they're hosting.
     */
    fun loadEvents() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                val filters = buildFilters()
                model.getFilteredEvents(loggedInUserId, filters).collect { events ->
                    // store the canonical list
                    allEvents = events
                    // apply current search on the canonical list
                    val filteredEvents = applySearchFilter(allEvents)
                    uiState = uiState.copy(
                        events = filteredEvents,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Search changed
     */
    fun onSearchChange(newValue: String) {
        uiState = uiState.copy(search = newValue)
        // Re-apply search filter to the canonical events list (not the already-filtered list)
        val filteredEvents = applySearchFilter(allEvents)
        uiState = uiState.copy(events = filteredEvents)
    }

    /**
     * Open filter popup
     */
    fun openFilter() {
        uiState = uiState.copy(isFilterOpen = true)
    }

    /**
     * Close filter popup
     */
    fun closeFilter() {
        uiState = uiState.copy(isFilterOpen = false)
    }

    /**
     * Edit event clicked
     */
    fun onEditEvent(event: Event?) {
        uiState = uiState.copy(selectedEvent = event)
    }

    /**
     * Update filters and reload events
     */
    fun updateFilters(startDate: String?, endDate: String?, groupSize: Int?, cost: Int?) {
        val inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        val formattedStart = startDate?.let {
            try {
                val parsed = LocalDate.parse(it, inputFormatter)
                parsed.toString()
            } catch (_: Exception) {
                null
            }
        }

        val formattedEnd = endDate?.let {
            try {
                val parsed = LocalDate.parse(it, inputFormatter)
                parsed.toString()
            } catch (_: Exception) {
                null
            }
        }

        uiState = uiState.copy(
            filterStartDate = formattedStart,
            filterEndDate = formattedEnd,
            filterGroupSize = groupSize,
            filterCost = cost
        )

        // Reload events with new filters
        loadEvents()
    }

    // ===== Private Helper Functions =====

    /**
     * Build EventFilter list from current UI state
     */
    private fun buildFilters(): List<EventFilter> {
        val filters = mutableListOf<EventFilter>()

        // Always include user's hosted and attended events
        filters.add(EventFilter.ByHostedBy(loggedInUserId))
        filters.add(EventFilter.Attending(loggedInUserId))

        // Add date range filter if specified (support single-sided ranges)
        if (uiState.filterStartDate != null || uiState.filterEndDate != null) {
            val start = uiState.filterStartDate ?: uiState.filterEndDate!!
            val end = uiState.filterEndDate ?: uiState.filterStartDate!!
            filters.add(
                EventFilter.ByDateRange(
                    startDate = start,
                    endDate = end
                )
            )
        }

        // Add group size filter if specified
        uiState.filterGroupSize?.let { groupSize ->
            filters.add(EventFilter.ByMaxAttendees(maxAttendees = groupSize))
        }

        // Add cost filter if specified
        uiState.filterCost?.let { cost ->
            filters.add(EventFilter.ByCost(maxCost = cost.toDouble()))
        }

        return filters
    }

    /**
     * Apply search filter to events based on name and description
     */
    private fun applySearchFilter(events: List<Event>): List<Event> {
        return applySearchFilter(events, uiState.search)
    }


    fun handleRSVP(event: Event) {
        handleRSVPWithCallback(event, onComplete = { loadEvents() })
    }

    fun deleteEvent(event: Event) {
        deleteEventWithCallback(event, onComplete = { loadEvents() })
    }
}

/**
 * UI state for the Events Screen
 */
data class EventsUiState(
    val events: List<Event> = emptyList(),
    val search: String = "",
    val selectedEvent: Event? = null,
    val isFilterOpen: Boolean = false,
    val isLoading: Boolean = false,
    val filterStartDate: String? = LocalDate.now().toString(),
    val filterEndDate: String? = LocalDate.now().plusMonths(4).toString(),
    val filterGroupSize: Int? = null,
    val filterCost: Int? = null
)
