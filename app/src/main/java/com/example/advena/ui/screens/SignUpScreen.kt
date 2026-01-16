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
fun SignUpScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var revealPassword by remember { mutableStateOf(false) }
    var showFullNameLabel by remember { mutableStateOf(false) }
    var showUsernameLabel by remember { mutableStateOf(false) }
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
                            viewModel.clearSignUpError()
                        },
                        icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        buttonModifier = Modifier
                            .background(
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
                text = "Sign Up Now!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.surfaceDim
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please fill in your details to create an account",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.surfaceDim,
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Username Field
            TextField(
                value = username,
                onValueChange = {
                    username = it
                    viewModel.clearSignUpError()
                },
                label = if (!showUsernameLabel && username.isEmpty()) { { Text("Username") } } else null,
                placeholder = { Text("Enter your username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        showUsernameLabel = focusState.isFocused
                    },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent, // Remove bottom line
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom line
                    disabledIndicatorColor = Color.Transparent,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(18.dp)
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Full Name Field
            TextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = if (!showFullNameLabel && fullName.isEmpty()) { { Text("Full Name") } } else null,
                placeholder = { Text("Enter your full name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        showFullNameLabel = focusState.isFocused
                    },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent, // Remove bottom line
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom line
                    disabledIndicatorColor = Color.Transparent,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearSignUpError()
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
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent, // Remove bottom line
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom line
                    disabledIndicatorColor = Color.Transparent,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            TextField(
                value = password,
                onValueChange = { password = it },
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
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent, // Remove bottom line
                    unfocusedIndicatorColor = Color.Transparent, // Remove bottom line
                    disabledIndicatorColor = Color.Transparent,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password
            Text(
                text = "Password must be at least 8 characters",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.surfaceDim,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { /* Handle forgot password */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // wrap in box so upon rendering, doesn't push sign up button down
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                viewModel.signUpState.errorMessage?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sign Up Button
            AppButton(
                text = "Sign Up",
                onClick = { viewModel.onSignUp(username, fullName, email, password) },
                containerColor = colors.primary,

                disabledContainerColor = Grey,
                enabled = fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank() && username.isNotBlank() && password.length >= 8,
                enabledAlpha = 1f,
                shape = RoundedCornerShape(18.dp),
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Sign Up Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        onSignInClick()
                        viewModel.clearSignUpError()
                    }
                )
            }
        }
    }
}