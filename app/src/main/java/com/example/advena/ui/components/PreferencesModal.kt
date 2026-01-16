package com.example.advena.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.advena.ui.theme.Black
import java.time.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesModal(
    onDismiss: () -> Unit,
    onSave: (startDate: String, endDate: String, groupSize: Int?, cost: Int?) -> Unit,
    currentStartDate: String? = null,
    currentEndDate: String? = null,
    currentGroupSize: Int? = null,
    currentCost: Int? = null
) {
    val displayFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    var selectedStartDate by remember {
        mutableStateOf(
            if (currentStartDate != null) {
                // currentDate expected in ISO yyyy-MM-dd
                try {
                    LocalDate.parse(currentStartDate)
                } catch (_: Exception) {
                    LocalDate.now()
                }
            } else {
                LocalDate.now()
            }
        )
    }

    var selectedEndDate by remember {
        mutableStateOf(
            if (currentEndDate != null) {
                try {
                    LocalDate.parse(currentEndDate)
                } catch (_: Exception) {
                    LocalDate.now()
                }
            } else {
                LocalDate.now()
            }
        )
    }

    var maxGroupSizeText by remember { mutableStateOf(currentGroupSize?.toString() ?: "") }
    var maxCostText by remember { mutableStateOf(currentCost?.toString() ?: "") }

    // Controls for showing the date picker and which field is being edited
    var showDatePicker by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf("start") } // "start" or "end"
    var datePickerKey by remember { mutableStateOf(0) } // used to recreate the DatePickerState with new initial date

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = {}),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Preferences",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    AppIconButton(
                        onClick = onDismiss,
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        buttonModifier = Modifier
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        iconModifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Date Range Section
                Text(
                    text = "Date",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Start Date field
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            editingField = "start"
                            datePickerKey++
                            showDatePicker = true
                        },
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Start Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = selectedStartDate.format(displayFormatter),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Select start date",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // End Date field
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            editingField = "end"
                            datePickerKey++
                            showDatePicker = true
                        },
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "End Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = selectedEndDate.format(displayFormatter),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Select end date",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "MM/DD/YYYY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Max Group Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Max Group Size",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = maxGroupSizeText,
                        onValueChange = { value ->
                            // Allow empty string or valid positive integers
                            if (value.isEmpty() || value.toIntOrNull()?.let { it > 0 } == true) {
                                maxGroupSizeText = value
                            }
                        },
                        modifier = Modifier.width(100.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        placeholder = {
                            Text("Any", style = MaterialTheme.typography.bodyMedium)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp)) // Reduced from 24dp to 16dp

                // Max Cost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Max Cost",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = maxCostText,
                            onValueChange = { value ->
                                // Allow empty string or valid non-negative integers
                                if (value.isEmpty() || value.toIntOrNull()?.let { it >= 0 } == true) {
                                    maxCostText = value
                                }
                            },
                            modifier = Modifier.width(100.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            placeholder = {
                                Text("Any", style = MaterialTheme.typography.bodyMedium)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp)) // Reduced from 32dp to 24dp

                // Save Button
                AppButton(
                    text = "Save",
                    onClick = {
                        val groupSize = maxGroupSizeText.toIntOrNull()
                        val cost = maxCostText.toIntOrNull()
                        onSave(
                            selectedStartDate.format(displayFormatter),
                            selectedEndDate.format(displayFormatter),
                            groupSize,
                            cost
                        )
                        onDismiss()
                    },
                    shape = RoundedCornerShape(28.dp),
                    textStyle = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }

        if (showDatePicker) {
            // Create a DatePickerState keyed by datePickerKey so that when we open the dialog
            // for a different field we initialize the picker with that field's current date.
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        // The actual update of selected date happens in the LaunchedEffect below
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                // Use a local DatePickerState that's recreated when datePickerKey changes
                val initialDate = if (editingField == "start") selectedStartDate else selectedEndDate
                val initialMillis = initialDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                val datePickerState = remember(datePickerKey) {
                    DatePickerState(
                        initialSelectedDateMillis = initialMillis,
                        yearRange = IntRange(2020, 2030),
                        initialDisplayMode = DisplayMode.Picker,
                        locale = java.util.Locale.getDefault()
                    )
                }

                // When the user confirms, use the selected millis to update the correct field
                LaunchedEffect(datePickerState.selectedDateMillis) {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        if (editingField == "start") {
                            selectedStartDate = newDate
                        } else {
                            selectedEndDate = newDate
                        }
                    }
                }

                DatePicker(
                    state = datePickerState,
                    showModeToggle = false
                )
            }
        }
    }
}
