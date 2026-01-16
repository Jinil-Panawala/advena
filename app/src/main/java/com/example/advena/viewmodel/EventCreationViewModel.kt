package com.example.advena.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.advena.domain.Event
import com.example.advena.domain.EventType
import com.example.advena.domain.GeocodingRepositoryInterface
import com.example.advena.domain.Model
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale


class EventCreationViewModel(
    model: Model,
    private val geocodingRepository: GeocodingRepositoryInterface
    ) : BaseViewModel(model) {

    var name by mutableStateOf("")
    var location by mutableStateOf("")
    var description by mutableStateOf("")
    var occupancyLimit by mutableStateOf("")
    var expectedCost by mutableStateOf("")
    var date by mutableStateOf("") // ISO yyyy-MM-dd
    var startTime by mutableStateOf("") // HH:mm
    var endTime by mutableStateOf("") // HH:mm
    var tags by mutableStateOf("")
    var type: EventType? by mutableStateOf(EventType.PUBLIC)

    var selectedEvent by mutableStateOf<Event?>(null)

    var showDatePicker by mutableStateOf(false)
    var showStartTimePicker by mutableStateOf(false)
    var showEndTimePicker by mutableStateOf(false)
    var showIncompleteFieldsDialog by mutableStateOf(false)

    var isSaving by mutableStateOf(false)

    fun loadEvent(event: Event?) {
        selectedEvent = event
        if (event != null) {
            name = event.name
            location = event.address
            description = event.description
            occupancyLimit = event.maxAttendees.toString()
            expectedCost = event.estimatedCost.toString()
            date = event.date
            startTime = event.startTime
            endTime = event.endTime
            tags = event.tags
            type = event.type
        } else {
            name = ""
            location = ""
            description = ""
            occupancyLimit = ""
            expectedCost = ""
            date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            startTime = "09:00"
            endTime = "17:00"
            tags = ""
            type = EventType.PUBLIC
        }
    }

    // Update helpers
    fun updateName(new: String) { name = new }
    fun updateLocation(new: String) { location = new }
    fun updateDescription(new: String) { description = new }
    fun updateOccupancyLimit(new: String) { occupancyLimit = new }
    fun updateExpectedCost(new: String) { expectedCost = new }
    fun updateTags(new: String) { tags = new }
    fun updateType(new: EventType) { type = new }

    fun showDatePicker() { showDatePicker = true }
    fun hideDatePicker() { showDatePicker = false }
    fun showStartTimePicker() { showStartTimePicker = true }
    fun hideStartTimePicker() { showStartTimePicker = false }
    fun showEndTimePicker() { showEndTimePicker = true }
    fun hideEndTimePicker() { showEndTimePicker = false }
    fun hideIncompleteFieldsDialog() { showIncompleteFieldsDialog = false }

    fun setDateFromMillis(millis: Long?) {
        millis?.let {
            val instant = Instant.ofEpochMilli(it)
            val picked = instant.atZone(ZoneId.of("UTC")).toLocalDate()
            date = picked.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }

    fun setStartTime(hour: Int, minute: Int) {
        startTime = String.format(Locale.US, "%02d:%02d", hour, minute)
    }

    fun setEndTime(hour: Int, minute: Int) {
        endTime = String.format(Locale.US, "%02d:%02d", hour, minute)
    }

    fun saveEvent(onComplete: (() -> Unit)? = null) {

        if (name.isBlank() ||
            location.isBlank() ||
            occupancyLimit.toIntOrNull() == null ||
            expectedCost.toDoubleOrNull() == null ||
            date.isBlank() ||
            startTime.isBlank() ||
            endTime.isBlank()
        ) {
           showIncompleteFieldsDialog = true
            return
        }

        viewModelScope.launch {
            isSaving = true

            try {
                val maxAtt = occupancyLimit.toIntOrNull() ?: 0
                val cost = expectedCost.toDoubleOrNull() ?: 0.0
                val geoLocation = geocodingRepository.geocode(location)

                if (selectedEvent != null) {
                    val e = selectedEvent!!.copy(
                        name = name,
                        description = description,
                        address = geoLocation!!.address,
                        latitude = geoLocation.latitude,
                        longitude = geoLocation.longitude,
                        date = date,
                        startTime = startTime,
                        endTime = endTime,
                        tags = tags,
                        estimatedCost = cost,
                        maxAttendees = maxAtt,
                        type = type ?: EventType.PUBLIC
                    )
                    model.updateEvent(e)
                } else {
                    val id = "evt_${System.currentTimeMillis()}"
                    model.createEvent(
                        id = id,
                        name = name,
                        description = description,
                        hostId = loggedInUserId,
                        address = geoLocation!!.address,
                        latitude = geoLocation.latitude,
                        longitude = geoLocation.longitude,
                        date = date,
                        startTime = startTime,
                        endTime = endTime,
                        tags = tags,
                        estimatedCost = cost,
                        maxAttendees = maxAtt,
                        type = type ?: EventType.PUBLIC
                    )
                }

            } catch (e: Exception) {
                Log.e("typeFail", "$e")
            }

            isSaving = false
            onComplete?.invoke()
        }
    }
}
