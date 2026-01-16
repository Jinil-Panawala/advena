package com.example.advena.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.advena.R
import com.example.advena.ui.components.AppButton
import com.example.advena.ui.components.AppIconButton
import com.example.advena.ui.theme.Grey
import com.example.advena.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var revealPassword by remember { mutableStateOf(false) }
    var showEmailLabel by remember { mutableStateOf(false) }
    var showPasswordLabel by remember { mutableStateOf(false) }
    val showPasswordIcon = if (revealPassword)
        painterResource(id = R.drawable.design_ic_visibility)
    else
        painterResource(id = R.drawable.design_ic_visibility_off)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    AppIconButton(
                        onClick = {
                            onBackClick()
                            viewModel.clearLoginError()
                        },
                        icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        buttonModifier = Modifier.background(
                                color = colors.surfaceVariant,
                                shape = CircleShape,
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
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
            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.surfaceDim
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please sign in to continue",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.surfaceDim,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Email Field
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearLoginError()
                },
                label = if (!showEmailLabel && email.isEmpty()) { { Text("Email") } } else null,
                placeholder = { Text("Enter your email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        showEmailLabel = focusState.isFocused
                    },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.surfaceVariant,
                    unfocusedContainerColor = colors.surfaceVariant,
                    disabledContainerColor = colors.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent, // Remove bottom line
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom line
                    disabledIndicatorColor = Color.Transparent,
                    focusedLabelColor = colors.primary,
                    unfocusedLabelColor = colors.onBackground
                ),
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.clearLoginError()
                },
                label = if (!showPasswordLabel && password.isEmpty()) { { Text("Password") } } else null,
                placeholder = { Text("Enter your password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        showPasswordLabel = focusState.isFocused
                    },
                singleLine = true,
                visualTransformation = if (revealPassword) VisualTransformation.None else PasswordVisualTransformation('*'),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { revealPassword = !revealPassword }) {
                        Icon(
                            painter = showPasswordIcon,
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.surfaceVariant,
                    unfocusedContainerColor = colors.surfaceVariant,
                    disabledContainerColor = colors.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent, // Remove bottom line
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom line
                    disabledIndicatorColor = Color.Transparent,
                    focusedLabelColor = colors.primary,
                    unfocusedLabelColor = colors.onBackground
                ),
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // wrap in box so upon rendering, doesn't push sign up button down
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                viewModel.loginState.errorMessage?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Login Button
            AppButton(
                text = "Sign In",
                onClick = { viewModel.onLogin(email, password) },
                enabled = email.isNotBlank() && password.isNotBlank() && password.length >= 8,
                containerColor = colors.primary,
                disabledContainerColor = Grey,
                enabledAlpha = 1f,
                shape = RoundedCornerShape(18.dp),
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Sign Up Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onBackground
                )
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.tertiary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        onSignUpClick()
                        viewModel.clearLoginError()
                    }
                )
            }
        }
    }
}