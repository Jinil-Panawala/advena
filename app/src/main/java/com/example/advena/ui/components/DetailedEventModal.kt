package com.example.advena.ui.components

import PinDrop
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.advena.domain.Event
import com.example.advena.ui.components.icons.Clock
import com.example.advena.ui.components.icons.Dollar
import com.example.advena.ui.theme.LightBlue
import com.example.advena.viewmodel.BaseViewModel

@Composable
fun DetailedEventModal(
    event: Event,
    hostName: String,
    onRSVP: (Event) -> Unit,
    attendeeCount: Int,
    isAttending: Boolean,
    onLeave: (Event) -> Unit = {},
    onDelete: (Event) -> Unit = {},
    isMine : Boolean,
    onEditEvent : (Event) -> Unit = {},
    viewAttendees: () -> Unit = {},
    viewHostProfile: () -> Unit = {},
    viewModel: BaseViewModel,
) {
    EventModal(
        event = event,
        hostName = hostName,
        onRSVP = onRSVP,
        attendeeCount = attendeeCount,
        isAttending = isAttending,
        isMine = isMine,
        onLeave = onLeave,
        onDelete = onDelete,
        viewAttendees = viewAttendees,
        viewHostProfile = viewHostProfile,
        viewModel = viewModel,
    ) { openDialog ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(LightBlue)
                .clickable { openDialog() }
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Event Title
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Display if user's event
                    if (isMine) {
                        IconButton(onClick = { onEditEvent(event) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Event Details Address + Time
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(PinDrop, contentDescription = "Address")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (event.address.length > 10)
                                event.address.take(10) + "..."
                            else
                                event.address,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Clock, contentDescription = "Address")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${event.startTime} - ${event.endTime}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
                        )
                    }
                }

                // Occupancy and Cost
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Occupancy",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Occupancy: ${attendeeCount}/${event.maxAttendees}", // attendeeCount
                            color = Color.Black,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Dollar,
                            contentDescription = "Dollar",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cost: $${"%.2f".format(event.estimatedCost)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}
