package com.example.advena.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.advena.domain.EventType
import com.example.advena.viewmodel.HomeViewModel
import com.example.advena.ui.components.AppButton
import com.example.advena.ui.components.AppIconButton
import com.example.advena.ui.components.PreferencesModal
import com.example.advena.ui.components.EventModal
import com.example.advena.ui.theme.Black
import com.example.advena.ui.theme.White
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
private suspend fun fetchLastLocationIfPermitted(context: Context): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    return try {
        fusedLocationClient.lastLocation.await()
    } catch (_: Exception) {
        null
    }
}


@OptIn(com.google.accompanist.permissions.ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onViewAttendees: (String) -> Unit,
    onUserClick: (String) -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme
    // Location permission
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Default location (San Francisco)
    val defaultLocation = LatLng(37.7749, -122.4194)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            uiState.userLocation ?: defaultLocation,
            12f
        )
    }

    var showPreferencesModal by remember { mutableStateOf(false) }

    // Request location permission on first composition
    LaunchedEffect(Unit) {
        if (locationPermissionState.status !is PermissionStatus.Granted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Evaluate whether we have the permission
    val hasLocationPermission by remember {
        derivedStateOf { locationPermissionState.status is PermissionStatus.Granted }
    }

    // Get user location when permission is granted
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            viewModel.onLocationPermissionGranted()
            try {
                val location = fetchLastLocationIfPermitted(context)
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    viewModel.updateUserLocation(userLatLng)
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(userLatLng, 14f),
                        durationMs = 1000
                    )
                } ?: throw Exception("Could not get your location. Please make sure location services are enabled.")
            } catch (e: Exception) {
                viewModel.setError("Could not get your location: ${e.message}")
                viewModel.updateUserLocation(defaultLocation)
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f),
                    durationMs = 1000
                )
            }
            // Load events after setting initial position
            cameraPositionState.projection?.visibleRegion?.latLngBounds?.let { bounds ->
                viewModel.loadEventsInBounds(bounds)
            }
        }
    }

    // Load events when camera moves
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            cameraPositionState.projection?.visibleRegion?.latLngBounds?.let { bounds ->
                viewModel.loadEventsInBounds(bounds)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasLocationPermission) {
            // Map View - only show when permission is granted
            GoogleMapView(
                cameraPositionState = cameraPositionState,
                userLocation = uiState.userLocation,
                viewModel = viewModel,
                onViewAttendees = onViewAttendees,
                onUserClick = onUserClick,
                modifier = Modifier.fillMaxSize()
            )


            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color.White,
                        shadowElevation = 0.dp,
                    ) {
                        TextField(
                            value = uiState.search,
                            onValueChange = { query ->
                                val currentBounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                                viewModel.onSearchChange(query, currentBounds)
                            },
                            placeholder = { Text("Search events...") },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }

                    // Filter Button
                    Surface(
                        modifier = Modifier.size(56.dp),
                        color = colors.surface,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 4.dp
                    ) {
                        AppIconButton(
                            onClick = { showPreferencesModal = true },
                            icon = Icons.Default.Menu,
                            contentDescription = "Filter",
                            iconTint = colors.primary,
                            buttonModifier = Modifier.fillMaxSize(),
                            iconModifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Filter pills
                FilterPills(
                    filterStartDate = uiState.filterStartDate,
                    filterEndDate = uiState.filterEndDate,
                    filterGroupSize = uiState.filterGroupSize,
                    filterCost = uiState.filterCost,
                    modifier = Modifier.fillMaxWidth()
                )

                // Show PreferencesModal when button is clicked
                if (showPreferencesModal) {
                    Dialog(
                        onDismissRequest = { showPreferencesModal = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            val handlePrefsSave: (String, String, Int?, Int?) -> Unit = { startDate, endDate, groupSize, cost ->
                                val currentBounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                                viewModel.updateFilters(startDate, endDate, groupSize, cost, currentBounds)
                                showPreferencesModal = false
                            }

                            PreferencesModal(
                                onDismiss = { showPreferencesModal = false },
                                onSave = handlePrefsSave,
                                currentStartDate = uiState.filterStartDate,
                                currentEndDate = uiState.filterEndDate,
                                currentGroupSize = uiState.filterGroupSize,
                                currentCost = uiState.filterCost
                            )
                        }
                    }
                }
            }

        } else {
            // Show location permission denied screen when permission is not granted
            LocationPermissionDenied(
                onRequestPermission = { locationPermissionState.launchPermissionRequest() }
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Suppress("UNUSED_VARIABLE")
@Composable
fun GoogleMapView(
    cameraPositionState: CameraPositionState,
    userLocation: LatLng?,
    viewModel: HomeViewModel,
    onViewAttendees: (String) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var defaultMarkerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var friendMarkerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var followerMarkerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var selectedEvent by remember { mutableStateOf<com.example.advena.domain.Event?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        defaultMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
        friendMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        followerMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = userLocation != null,
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = false
        )
    ) {
        uiState.visibleEvents.forEach { event ->
            Marker(
                state = MarkerState(position = LatLng(event.latitude, event.longitude)),
                title = event.name,
                icon = when (event.type) {
                    EventType.FRIEND -> friendMarkerIcon ?: BitmapDescriptorFactory.defaultMarker()
                    EventType.FOLLOWER -> followerMarkerIcon ?: BitmapDescriptorFactory.defaultMarker()
                    EventType.PUBLIC -> defaultMarkerIcon ?: BitmapDescriptorFactory.defaultMarker()
                },
                onClick = {
                    selectedEvent = event
                    true
                }
            )
        }
    }

    // Show EventModal when an event is selected
    selectedEvent?.let { event ->
        val attendeeCount by viewModel.getEventAttendeeCountFlow(event.id).collectAsState(initial = 0)
        val isAttending by viewModel.getIsUserAttendingFlow(event.id).collectAsState(initial = false)

        EventModal(
            event = event,
            hostName = event.hostId,
            onRSVP = { rsvpEvent ->
                viewModel.handleRSVP(rsvpEvent)
                selectedEvent = null
            },
            onDismiss = {
                selectedEvent = null
            },
            attendeeCount = attendeeCount,
            isAttending = isAttending,
            isMine = viewModel.isEventOwnedByLoggedInUser(event),
            onLeave = { leaveEvent ->
                viewModel.handleRSVP(leaveEvent)
                selectedEvent = null
            },
            onDelete = { deleteEvent ->
                viewModel.deleteEvent(deleteEvent)
                selectedEvent = null
            },
            viewAttendees = { onViewAttendees(event.id) },
            viewHostProfile = { onUserClick(event.hostId)},
            viewModel = viewModel
        ) { openDialog ->
            // Automatically open the modal when event is selected
            LaunchedEffect(event.id) {
                openDialog()
            }
        }
    }
}

@OptIn(com.google.accompanist.permissions.ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionDenied(
    onRequestPermission: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Location Permission Required",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This app needs access to your location to show nearby events on the map.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))


        val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

        when {
            locationPermissionState.status.shouldShowRationale -> {
                Text(
                    text = "We need your location to provide personalized event recommendations. Please grant us permission.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(
                    text = "Retry",
                    onClick = { locationPermissionState.launchPermissionRequest() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
            locationPermissionState.status is PermissionStatus.Denied -> {
                // Permission denied permanently, show settings button
                Text(
                    text = "Location permission was denied. You can enable it in app settings.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Open app settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Open Settings")
                }
            }
            else -> {
                AppButton(
                    text = "Grant Permission",
                    onClick = onRequestPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }
    }
}

@Composable
fun FilterPills(
    filterStartDate: String?,
    filterEndDate: String?,
    filterGroupSize: Int?,
    filterCost: Int?,
    modifier: Modifier = Modifier
) {
    // Prepare a human-friendly date range string if either date is present
    val displayDate: String? = remember(filterStartDate, filterEndDate) {
        if (filterStartDate == null && filterEndDate == null) return@remember null
        val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd yyyy")
        val start = filterStartDate?.let {
            try {
                java.time.LocalDate.parse(it).format(formatter)
            } catch (_: Exception) { null }
        }
        val end = filterEndDate?.let {
            try {
                java.time.LocalDate.parse(it).format(formatter)
            } catch (_: Exception) { null }
        }
        when {
            start != null && end != null -> "$start - $end"
            start != null -> start
            end != null -> end
            else -> null
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            label = "Date",
            value = displayDate
        )

        FilterChip(
            label = "Size",
            value = filterGroupSize?.toString()
        )

        FilterChip(
            label = "Cost",
            value = filterCost?.toString(),
            prefix = "$"
        )
    }
}

@Composable
fun FilterChip(
    label: String,
    value: String?,
    prefix: String = ""
) {
    val displayValue = value?.let { if (prefix.isNotEmpty()) "$prefix$it" else it } ?: "Any"

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = White,
        modifier = Modifier.height(32.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: $displayValue",
                style = MaterialTheme.typography.bodySmall,
                color = Black
            )
        }
    }
}
