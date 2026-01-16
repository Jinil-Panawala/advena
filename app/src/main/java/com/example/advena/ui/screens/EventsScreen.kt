package com.example.advena.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.advena.ui.components.DetailedEventModal
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.advena.ui.components.AppIconButton
import com.example.advena.ui.components.PreferencesModal
import com.example.advena.viewmodel.EventsViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    viewModel: EventsViewModel,
    onBackClick: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onViewAttendees: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {},
    ) {
    val colors = MaterialTheme.colorScheme
    val state by remember { derivedStateOf { viewModel.uiState } }

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    AppIconButton(
                        onClick = onBackClick,
                        icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        iconModifier = Modifier.size(24.dp)
                    )
                },
                title = { Text("Your Events", style = MaterialTheme.typography.headlineSmall) },
                actions = {
                    AppIconButton(
                        onClick = { onNavigateToCreate() },
                        icon = Icons.Default.Add,
                        contentDescription = "Create",
                        iconModifier = Modifier.size(24.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = state.search,
                    onValueChange = { viewModel.onSearchChange(it)},
                    label = { Text("Search", style = MaterialTheme.typography.bodyLarge) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant,
                        disabledContainerColor = colors.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedLabelColor = colors.primary,
                        unfocusedLabelColor = colors.onBackground
                    ),
                    leadingIcon = {Icon(Icons.Default.Search, contentDescription = "search",)}
                )

                Spacer(modifier = Modifier.width(16.dp))

                AppIconButton(
                    onClick = { viewModel.openFilter() },
                    icon = Icons.Default.Menu,
                    contentDescription = "Filter",
                    iconTint = colors.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))


            val filtered = state.events.filter { event ->
                val containsName = event.name.contains(state.search, true)
                val containsTags = event.tags.split(",").any { tag -> tag.contains(state.search, true) }

                // Date range filtering: use filterStartDate and filterEndDate (both in ISO yyyy-MM-dd)
                val matchesDate = run {
                    try {
                        val eventLocalDate = LocalDate.parse(event.date)
                        val start = state.filterStartDate?.let { LocalDate.parse(it) }
                        val end = state.filterEndDate?.let { LocalDate.parse(it) }

                        when {
                            start != null && end != null -> (eventLocalDate >= start) && (eventLocalDate <= end)
                            start != null -> eventLocalDate >= start
                            end != null -> eventLocalDate <= end
                            else -> true
                        }
                    } catch (_: Exception) {
                        false
                    }
                }

                val matchesGroupSize = state.filterGroupSize?.let { requestedGroupSize ->
                    event.maxAttendees <= requestedGroupSize
                } ?: true

                val matchesCost = state.filterCost?.let { maxCost ->
                    event.estimatedCost <= maxCost.toDouble()
                } ?: true

                (containsName || containsTags) && matchesDate && matchesGroupSize && matchesCost
            }


            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(filtered){ event ->
                        val attendeeCount by viewModel.getEventAttendeeCountFlow(event.id).collectAsState(initial = 0)
                        val isAttending by viewModel.getIsUserAttendingFlow(event.id).collectAsState(initial = false)
                        DetailedEventModal (
                            event = event,
                            hostName = event.hostId, // can fetch user name if needed?
                            onRSVP = { ev -> viewModel.handleRSVP(ev) },
                            attendeeCount = attendeeCount,
                            isAttending = isAttending,
                            onLeave = { ev -> viewModel.handleRSVP(ev) },
                            onDelete = { ev -> viewModel.deleteEvent(ev) },
                            isMine = viewModel.isEventOwnedByLoggedInUser(event),
                            onEditEvent = {event ->
                                viewModel.onEditEvent(event)
                                onNavigateToEdit()
                            },
                            viewAttendees = { onViewAttendees(event.id) },
                            viewHostProfile = { onUserClick(event.hostId) },
                            viewModel = viewModel
                        )
                }
            }

            if (state.isFilterOpen) {
                Dialog(onDismissRequest = { viewModel.closeFilter() }, properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        PreferencesModal(
                            onDismiss = { viewModel.closeFilter() },
                            onSave = { startDate, endDate, groupSize, cost ->
                                viewModel.updateFilters(startDate, endDate, groupSize, cost)
                                viewModel.closeFilter()
                            },
                            currentStartDate = state.filterStartDate,
                            currentEndDate = state.filterEndDate,
                            currentGroupSize = state.filterGroupSize,
                            currentCost = state.filterCost
                        )
                    }
                }
            }

        }
    }}
