package com.example.advena.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.advena.domain.Event
import com.example.advena.domain.User
import com.example.advena.ui.components.AppButton
import com.example.advena.ui.components.AppIconButton
import com.example.advena.ui.components.CompactEventModal
import com.example.advena.ui.components.icons.ChevronBackward
import com.example.advena.ui.components.icons.EventSeat
import com.example.advena.ui.components.icons.PersonPin
import com.example.advena.ui.theme.AshGrey
import com.example.advena.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onFriendsClick: (listType: String, userId: String) -> Unit = {_, _, ->},
    onLogout: () -> Unit = {},
    onViewAttendees: (eventId: String) -> Unit = {},
    onUserClick: (userId: String) -> Unit = {},
) {
    val uiState = viewModel.uiState

    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        uiState.user?.let {
            ProfileScreenContent(
                user = it,
                userFollowers = viewModel.getFollowers(it.id).collectAsState(initial = emptyList()).value.size,
                userFollowing = viewModel.getFollowing(it.id).collectAsState(initial = emptyList()).value.size,
                isOwnProfile = uiState.isOwnProfile,
                isFollowing = uiState.isFollowing,
                hostedEvents = uiState.hostedEvents,
                attendedEvents = uiState.attendedEvents,
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onSearchClick = onSearchClick,
                onFriendsClick = onFriendsClick,
                onRSVP = { event -> viewModel.handleRSVP(event) },
                onDelete = { event -> viewModel.deleteEvent(event) },
                getEventAttendeeCountFlow = { eid -> viewModel.getEventAttendeeCountFlow(eid) },
                getIsUserAttendingFlow = { eid -> viewModel.getIsUserAttendingFlow(eid) },
                isEventOwnedByLoggedInUser = { ev -> viewModel.isEventOwnedByLoggedInUser(ev) },
                onFollowToggle = { viewModel.toggleFollow() },
                onLogout = onLogout,
                onViewAttendees = onViewAttendees,
                onUserClick = onUserClick,
                modifier = modifier,
                viewModel = viewModel
            )
        } ?: Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    user: User,
    userFollowers: Int,
    userFollowing: Int,
    isOwnProfile: Boolean,
    isFollowing: Boolean = false,
    hostedEvents: List<Event>,
    attendedEvents: List<Event>,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onFriendsClick: (listType: String, userId: String) -> Unit = { _, _, -> },
    onRSVP: (Event) -> Unit = {},
    onDelete: (Event) -> Unit = {},
    getEventAttendeeCountFlow: (String) -> Flow<Int>,
    getIsUserAttendingFlow: (String) -> Flow<Boolean>,
    isEventOwnedByLoggedInUser: (Event) -> Boolean,
    onFollowToggle: () -> Unit = {},
    onLogout: () -> Unit = {},
    onViewAttendees: (String) -> Unit,
    onUserClick: (String) -> Unit,
) {
    val uiState = viewModel.uiState
    var showingHosted by remember { mutableStateOf(true) }
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp)
    ) {
        // Username + Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isOwnProfile) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ChevronBackward,
                            contentDescription = "Back",
                            tint = colors.onBackground
                        )
                    }
                }

                Text(
                    text = user.id,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.onBackground
                )
            }

            Row {
                AppIconButton(
                    onClick = onSearchClick,
                    icon = Icons.Default.Search,
                    contentDescription = "Search",
                    iconTint = colors.onBackground
                )

                if (isOwnProfile) {
                    AppIconButton(
                        onClick = onEditClick,
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        iconTint = colors.onBackground
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Profile Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.primary.copy(alpha = 0.9f), shape = RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile picture placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AshGrey),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.first().uppercase(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Stats section
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileStat(number = userFollowers, label = "Followers", modifier = Modifier.clickable{onFriendsClick("followers", user.id)})
                    ProfileStat(number = userFollowing, label = "Following", modifier = Modifier.clickable{onFriendsClick("following", user.id)})
                    ProfileStat(number = hostedEvents.size, label = "Hosted")
                    ProfileStat(number = attendedEvents.size, label = "Attended")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Name + Bio
        Text(
            text = user.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onBackground
        )

        Text(
            text = user.bio?.ifBlank { "No bio yet" } ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onBackground.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isOwnProfile) {
            AppButton(
                text = "Logout",
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth())
        } else {
            AppButton(
                text = if (isFollowing) "Following" else "Follow",
                onClick = onFollowToggle,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppIconButton(
                onClick = { showingHosted = true },
                icon = PersonPin,
                contentDescription = "Hosted Events",
                iconTint = if (showingHosted) colors.onBackground else colors.onBackground.copy(alpha = 0.4f)
            )
            AppIconButton(
                onClick = { showingHosted = false },
                icon = EventSeat,
                contentDescription = "Attended Events",
                iconTint = if (!showingHosted) colors.onBackground else colors.onBackground.copy(alpha = 0.4f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Events Grid
        val eventsToShow = if (showingHosted) hostedEvents else attendedEvents

        if (uiState.updatingEvents) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {
            if (eventsToShow.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showingHosted) "No hosted events yet" else "No attended events yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(eventsToShow) { event ->
                        val attendeeCount by getEventAttendeeCountFlow(event.id).collectAsState(initial = 0)
                        val isAttending by getIsUserAttendingFlow(event.id).collectAsState(initial = false)
                        CompactEventModal(
                            event = event,
                            hostName = event.hostId,
                            onRSVP = onRSVP,
                            attendeeCount = attendeeCount,
                            isAttending = isAttending,
                            onLeave = { ev -> onRSVP(ev) },
                            onDelete = { ev -> onDelete(ev) },
                            isMine = isEventOwnedByLoggedInUser(event),
                            viewAttendees = { onViewAttendees(event.id) },
                            viewHostProfile = { onUserClick(event.hostId) },
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
    }
        }


@Composable
fun ProfileStat(number: Int, label: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = number.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}