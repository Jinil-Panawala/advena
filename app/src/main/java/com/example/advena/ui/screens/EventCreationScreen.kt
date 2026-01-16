package com.example.advena.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.advena.viewmodel.EventCreationViewModel
import com.example.advena.ui.components.AppButton
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.example.advena.domain.EventType

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun EventCreationScreen(
    navController: NavController,
    viewModel: EventCreationViewModel,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
) {
    // Bind directly to viewModel state
    val name by remember { derivedStateOf { viewModel.name } }
    val location by remember { derivedStateOf { viewModel.location } }
    val description by remember { derivedStateOf { viewModel.description } }
    val occupancyLimit by remember { derivedStateOf { viewModel.occupancyLimit } }
    val expectedCost by remember { derivedStateOf { viewModel.expectedCost } }
    val date by remember { derivedStateOf { viewModel.date } }
    val startTime by remember { derivedStateOf { viewModel.startTime } }
    val endTime by remember { derivedStateOf { viewModel.endTime } }
    val tagsText by remember { derivedStateOf { viewModel.tags } }
    val type by remember { derivedStateOf { viewModel.type } }

    val showDatePicker by remember { derivedStateOf { viewModel.showDatePicker } }
    val showStartTimePicker by remember { derivedStateOf { viewModel.showStartTimePicker } }
    val showEndTimePicker by remember { derivedStateOf { viewModel.showEndTimePicker } }
    val showInvalidFieldsDialog by remember { derivedStateOf { viewModel.showIncompleteFieldsDialog } }

    val isEdit by remember { derivedStateOf { viewModel.selectedEvent != null } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", Modifier.size(24.dp))
                    }
                },
                title = {
                    Text(if (isEdit) "Edit Event" else "Create Event",
                        style = MaterialTheme.typography.headlineSmall)
                },
            )
        }
    ) { paddingValues ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // reduced top spacing so form sits closer to the top app bar
                item { Spacer(Modifier.height(12.dp)) }

                item { EventInputField("Event Name", name, onChange = { viewModel.updateName(it) }) }
                item { EventInputField("Location", location, onChange = { viewModel.updateLocation(it) }) }
                item { EventInputField("Max Attendees", occupancyLimit, onChange = { viewModel.updateOccupancyLimit(it) }) }
                item { EventInputField("Estimated Cost", expectedCost, onChange = { viewModel.updateExpectedCost(it) }) }
                item { EventInputField("Date", date, readOnly = true, onClick = { viewModel.showDatePicker() }) { } }
                item { EventInputField("Start Time", startTime, readOnly = true, onClick = { viewModel.showStartTimePicker() }) { } }
                item { EventInputField("End Time", endTime, readOnly = true, onClick = { viewModel.showEndTimePicker() }) { } }
                item { EventInputField("Description", description, singleLine = false, onChange = { viewModel.updateDescription(it) }) }

                item {
                    EventInputField(
                        label = "Tags (comma-separated)",
                        value = tagsText,
                        singleLine = false,
                        onChange = { viewModel.updateTags(it) }
                    )
                }

                if(!isEdit) {
                    item {
                        EventTypeSegmentedButtons(
                            selected = type ?: EventType.PUBLIC,
                            onSelect = { viewModel.updateType(it) }
                        )
                    }
                }

                item {
                    AppButton(
                        if (isEdit) "Edit Event" else "Create Event",
                        onClick = { viewModel.saveEvent { onAddClick() } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                    )
                }
            }

            if (showDatePicker) {
                DatePickerModal(
                    label = "Date",
                    onDateSelected = { millis ->
                        viewModel.hideDatePicker()
                        viewModel.setDateFromMillis(millis)
                    },
                    onDismiss = { viewModel.hideDatePicker() }
                )
            }

            // Start time picker
            if (showStartTimePicker) {
                TimePickerModal(
                    label = "Start Time",
                    initialHour = try { startTime.split(":")[0].toInt() } catch (_: Exception) { 9 },
                    initialMinute = try { startTime.split(":")[1].toInt() } catch (_: Exception) { 0 },
                    onTimeSelected = { h: Int, m: Int ->
                        viewModel.hideStartTimePicker()
                        viewModel.setStartTime(h, m)
                    },
                    onDismiss = { viewModel.hideStartTimePicker() }
                )
            }

            // End time picker
            if (showEndTimePicker) {
                TimePickerModal(
                    label = "End Time",
                    initialHour = try { endTime.split(":")[0].toInt() } catch (_: Exception) { 17 },
                    initialMinute = try { endTime.split(":")[1].toInt() } catch (_: Exception) { 0 },
                    onTimeSelected = { h: Int, m: Int ->
                        viewModel.hideEndTimePicker()
                        viewModel.setEndTime(h, m)
                    },
                    onDismiss = { viewModel.hideEndTimePicker() }
                )
            }

            if (showInvalidFieldsDialog) {
                InvalidFieldsModal(
                    onDismiss = { viewModel.hideIncompleteFieldsDialog() }
                )
            }
        }
    }
}

@Composable
fun EventTypeSegmentedButtons(
    selected : EventType,
    onSelect: (EventType) -> Unit
){
    Text("Event Visibility", style = MaterialTheme.typography.bodyLarge)
    Spacer(Modifier.height(4.dp))
    SingleChoiceSegmentedButtonRow {
        EventType.entries.forEach { type ->
            SegmentedButton(
                selected = selected == type,
                onClick = { onSelect(type) },
                label = {when (type) {
                    EventType.PUBLIC -> Text("Public")
                    EventType.FOLLOWER -> Text("Followers")
                    EventType.FRIEND -> Text("Friends")
                }},
                shape = SegmentedButtonDefaults.itemShape(
                    index = EventType.entries.indexOf(type),
                    count = EventType.entries.size
                ),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    activeContentColor = MaterialTheme.colorScheme.primary,
                    inactiveContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        }
    }
}
@Composable
fun EventInputField(
    label: String,
    value: String,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    onChange: (String) -> Unit
) {
    Text(label, style = MaterialTheme.typography.bodyLarge)
    Spacer(Modifier.height(4.dp))
    val baseModifier = Modifier
        .fillMaxWidth()
        .height(if (singleLine) 55.dp else 200.dp)
        .padding(horizontal = 16.dp)

    val textField = @Composable {
        TextField(
            value = value,
            onValueChange = onChange,
            singleLine = singleLine,
            readOnly = readOnly,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                        .fillMaxWidth()
                        .height(if (singleLine) 55.dp else 200.dp),
            trailingIcon = { if (onClick != null) Text("Select", style = MaterialTheme.typography.bodySmall) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
            ),
        )
    }

    Box(modifier = baseModifier) {
        textField()
        if (onClick != null) {
            Box(modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
            )
        }
    }
    Spacer(Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    label: String = "Date",
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
){
    val date = rememberDatePickerState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val dialogWidth = if ((screenWidth * 0.98f) < 820.dp) (screenWidth * 0.98f) else 820.dp
    val dialogHeight = (screenHeight * 0.94f).coerceAtLeast(420.dp)

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .width(dialogWidth)
                        .height(dialogHeight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(8.dp))

                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                        ) {
                            DatePicker(
                                state = date,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                colors = DatePickerDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    headlineContentColor = MaterialTheme.colorScheme.onPrimary,
                                    navigationContentColor = MaterialTheme.colorScheme.onPrimary,
                                    weekdayContentColor = MaterialTheme.colorScheme.onSurface,
                                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                                    dayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.secondary,
                                    dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onSecondary,
                                )

                            )
                        }

                        Spacer(Modifier.height(12.dp))
                        AppButton(
                            text = "OK",
                            onClick = { onDateSelected(date.selectedDateMillis) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    label: String = "Time",
    initialHour: Int = 9,
    initialMinute: Int = 0,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timeState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            TimeInput(
                                state = timeState,
                                modifier = Modifier.wrapContentSize(),
                                colors = TimePickerDefaults.colors(
                                    clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                                    clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                    clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    selectorColor = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    periodSelectorBorderColor = MaterialTheme.colorScheme.outline,
                                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                    periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                        }

                        Spacer(Modifier.height(12.dp))
                        AppButton(
                            text = "OK",
                            onClick = { onTimeSelected(timeState.hour, timeState.minute) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InvalidFieldsModal(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .width(300.dp)
                .height(180.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Invalid Fields", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Please ensure all fields are filled out correctly before proceeding.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                AppButton(
                    text = "OK",
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                )
            }
        }
    }
}
