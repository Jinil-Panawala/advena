package com.example.advena.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.advena.ui.components.AppIconButton
import com.example.advena.viewmodel.EditProfileViewModel

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    EditProfileContent(
        name = viewModel.name,
        bio = viewModel.bio,
        profileInitial = viewModel.profileInitial,
        isSaving = viewModel.isSaving,
        onNameChange = { viewModel.updateName(it) },
        onBioChange = { viewModel.updateBio(it) },
        onSaveClick = {
            viewModel.saveProfile()
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun EditProfileContent(
    name: String,
    bio: String,
    profileInitial: String,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
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
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.onBackground
            )

            TextButton(
                onClick = onSaveClick,
                enabled = !isSaving && name.isNotBlank()
            ) {
                Text(
                    text = if (isSaving) "Saving..." else "Save",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (!isSaving && name.isNotBlank()) colors.primary else colors.onBackground.copy(alpha = 0.4f)
                )
            }
        }

        HorizontalDivider(
            color = colors.primary.copy(alpha = 0.5f),
            thickness = 1.dp
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile picture section
            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profileInitial,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Picture",
                        tint = colors.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.onBackground.copy(alpha = 0.3f),
                    focusedLabelColor = colors.primary,
                    unfocusedLabelColor = colors.onBackground.copy(alpha = 0.6f)
                ),
                singleLine = true,
                isError = name.isBlank()
            )

            if (name.isBlank()) {
                Text(
                    text = "Name cannot be empty",
                    color = colors.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bio field
            OutlinedTextField(
                value = bio,
                onValueChange = onBioChange,
                label = { Text("Bio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.onBackground.copy(alpha = 0.3f),
                    focusedLabelColor = colors.primary,
                    unfocusedLabelColor = colors.onBackground.copy(alpha = 0.6f)
                ),
                maxLines = 6,
                placeholder = { Text("Tell others about yourself...") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Character count
            Text(
                text = "${bio.length} / 200",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}