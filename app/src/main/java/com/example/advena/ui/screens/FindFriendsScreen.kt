package com.example.advena.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.advena.domain.User
import com.example.advena.ui.components.AppIconButton
import com.example.advena.ui.components.icons.PersonAdd
import com.example.advena.ui.components.icons.PersonCheck
import com.example.advena.viewmodel.FindFriendsViewModel

@Composable
fun FindFriendsScreen(
    viewModel: FindFriendsViewModel,
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val followingList by viewModel.followingList.collectAsState()
    val followingIds = followingList.map { it.id }.toSet()

    FindFriendsContent(
        title = when (viewModel.listType) {
            "event" -> "Attendees"
            "following" -> "Following"
            "followers" -> "Followers"
            else -> "Users"
        },
        searchQuery = viewModel.searchQuery,
        users = viewModel.filteredUsers.collectAsState(initial = emptyList()).value,
        currentUserId = viewModel.currentId,
        followingIds = followingIds,
        onSearchQueryChange = { viewModel.searchQuery = it },
        onAddFriend = { userId -> viewModel.followUser(userId) },
        onRemoveFriend = { userId -> viewModel.unfollowUser(userId) },
        onBackClick = onBackClick,
        onUserClick = onUserClick,
        viewModel = viewModel,
        modifier = modifier
    )
}

@Composable
fun FindFriendsContent(
    title: String,
    searchQuery: String,
    users: List<User>,
    currentUserId: String,
    followingIds: Set<String>,
    onSearchQueryChange: (String) -> Unit,
    onAddFriend: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    viewModel: FindFriendsViewModel,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        // Top bar with title and close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconButton(
                onClick = onBackClick,
                icon = Icons.Default.Close,
                contentDescription = "Close",
                iconTint = colors.onBackground
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.onBackground
            )

            // Empty spacer to balance the layout
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Search bar
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search by name...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = colors.onSurfaceVariant
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
                disabledContainerColor = colors.surface,
                focusedIndicatorColor = colors.primary,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { keyboardController?.hide() }
            )
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colors.primary,
            thickness = 2.dp
        )

        // User list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(users) { user ->
                if (user.id != currentUserId) {
                    UserListItem(
                        user = user,
                        follows = followingIds.contains(user.id),
                        onFollow = { onAddFriend(user.id) },
                        onUnfollow = { onRemoveFriend(user.id) },
                        onUserClick = { onUserClick(user.id) },
                        viewModel = viewModel
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = colors.primary.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
fun UserListItem(
    user: User,
    follows: Boolean,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    onUserClick: () -> Unit,
    viewModel: FindFriendsViewModel,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Profile picture placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
                    .clickable(onClick = onUserClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.first().uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = user.id,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Text(
                    text = user.name,
                    fontSize = 14.sp,
                    color = colors.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        // Add/Remove friend button
        if (user.id != viewModel.loggedInUserId) {
            FilledIconButton(
                onClick = if (follows) onUnfollow else onFollow,
                colors = if (follows) {
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = colors.primary
                    )
                } else {
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                },
                modifier = Modifier.border(
                    width = 2.dp,
                    color = colors.primary,
                    shape = CircleShape
                )
            ) {
                Icon(
                    imageVector = if (follows) PersonCheck else PersonAdd,
                    contentDescription = if (follows) "Unfollow" else "Follow",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}