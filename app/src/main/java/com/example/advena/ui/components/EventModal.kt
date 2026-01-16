package com.example.advena.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.advena.domain.Event
import com.example.advena.domain.EventType
import com.example.advena.ui.components.icons.Dollar
import com.example.advena.ui.theme.AshGrey
import com.example.advena.ui.theme.Grey
import com.example.advena.ui.theme.LightPurple
import com.example.advena.ui.theme.Purple
import com.example.advena.ui.theme.White
import com.example.advena.viewmodel.BaseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventModal(
    event: Event,
    hostName: String,
    onRSVP: (Event) -> Unit,
    onDismiss: () -> Unit = {},
    attendeeCount: Int,
    isAttending: Boolean,
    isMine: Boolean,
    onLeave: (Event) -> Unit = {},
    onDelete: (Event) -> Unit = {},
    viewAttendees: () -> Unit = {},
    viewHostProfile: () -> Unit = {},
    viewModel: BaseViewModel,
    trigger: @Composable (openDialog: () -> Unit) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }
    var showEventDeleteConfirmation by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(event.id) {
        isVisible = viewModel.isEventVisibleToLoggedInUser(event)
    }

    trigger { openDialog = true }

    if (showEventDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showEventDeleteConfirmation = false },
            title = {
                Text(
                    text = "Delete Event?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text("Are you sure you want to delete this event? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEventDeleteConfirmation = false
                        onDelete(event)
                        openDialog = false
                    }
                ) {
                    Text("Yes", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                    showEventDeleteConfirmation = false
                    }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.secondary)
                }
            }
        )
    }

    if (openDialog) {
        Dialog(
            onDismissRequest = {
                openDialog = false
                onDismiss()
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .scale(0.9f)
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    // Floating Close Button (top-right corner)
                    AppIconButton(
                        onClick = {
                            openDialog = false
                            onDismiss()
                        },
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        iconTint = MaterialTheme.colorScheme.onSurface,
                        buttonModifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(28.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 36.dp) // Push content below the close button
                    ) {
                        // Event title
                        Text(
                            text = event.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Date + time + address
                        val formattedDate = try {
                            LocalDate.parse(event.date).format(DateTimeFormatter.ofPattern("EEE, MMM d"))
                        } catch (_: Exception) {
                            event.date
                        }
                        Text(
                            text = "$formattedDate • ${event.startTime} • ${event.address}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Host Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(AshGrey)
                                    .clickable(onClick = viewHostProfile),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = hostName.first().uppercase(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = hostName,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tags
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            event.tags.split(",").forEach { tag ->
                                Surface(
                                    color = LightPurple,
                                    shape = RoundedCornerShape(20.dp),
                                    tonalElevation = 1.dp
                                ) {
                                    Text(
                                        text = tag,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        color = Purple,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Occupancy and Cost
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable(onClick = {
                                    openDialog = false
                                    onDismiss()
                                    viewAttendees()
                                })
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Occupancy",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Occupancy: ${attendeeCount}/${event.maxAttendees}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Dollar,
                                    contentDescription = "Dollar",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Cost: $${"%.2f".format(event.estimatedCost)}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(end = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Determine if the event is full
                        val isFull = attendeeCount >= event.maxAttendees

                        // Determine if the event date is in the past (expired).
                        val isExpired = try {
                            val parsedDate = LocalDate.parse(event.date)
                            parsedDate.isBefore(LocalDate.now())
                        } catch (_: Exception) {
                            false
                        }

                        val buttonEnabled = when {
                            isExpired -> false
                            isMine -> true
                            isAttending -> true
                            else -> isVisible && !isFull
                        }
                        AppButton(
                            text = when {
                                isExpired -> "EVENT EXPIRED"
                                isMine -> "DELETE EVENT"
                                isAttending -> "LEAVE EVENT"
                                isFull -> "EVENT IS FULL"
                                !isVisible -> "${event.type}S EVENT ONLY"
                                else -> "RSVP"
                            },
                            onClick = {
                                if (isAttending) onLeave(event)
                                else if (isMine) showEventDeleteConfirmation = true
                                else onRSVP(event)
                            },
                            enabled = buttonEnabled,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = White,
                            enabledAlpha = 1f,
                            disabledContainerColor = Grey,
                            disabledContentColor = White.copy(alpha = 0.85f),
                            useAlphaForDisabled = false,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

}
