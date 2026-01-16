package com.example.advena.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.advena.domain.Event
import com.example.advena.ui.theme.LightBlue
import com.example.advena.viewmodel.BaseViewModel

@Composable
fun CompactEventModal(
    event: Event,
    hostName: String,
    onRSVP: (Event) -> Unit,
    attendeeCount: Int,
    isAttending: Boolean,
    onLeave: (Event) -> Unit = {},
    onDelete: (Event) -> Unit = {},
    isMine: Boolean,
    viewAttendees: () -> Unit = {},
    viewHostProfile: () -> Unit = {},
    viewModel: BaseViewModel,
) {
    // Compact Card
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
                .width(180.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LightBlue)
                .clickable { openDialog() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    }
}
