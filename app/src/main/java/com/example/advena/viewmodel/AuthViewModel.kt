package com.example.advena.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advena.data.AuthRepository
import com.example.advena.data.DbStorage
import com.example.advena.domain.Model
import kotlinx.coroutines.launch

/**
 * This viewmodel supports two screens: Login and SignUp. It allows users to login, sign up, and log out.
 */
class AuthViewModel(
    private val model: Model = Model(DbStorage()),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var loginState by mutableStateOf(LoginUiState())
        private set

    var signUpState by mutableStateOf(SignUpUiState())
        private set



    /**
     * Handle login attempt
     */
    fun onLogin(email: String, password: String) {
        viewModelScope.launch {
            loginState = loginState.copy(isLoading = true, errorMessage = null)

            val result = authRepository.signIn(email, password)

            if (result) {
                // Login succeeded in auth table
                try {
                    model.loggedInUserId.value = model.getUserByEmail(email).id
                    loginState = loginState.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                } catch (e: Exception) {
                    loginState = loginState.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "Login succeeded but failed to fetch user profile: ${e.message}"
                    )
                }
            } else { // login failed in auth table
                loginState = loginState.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = authRepository.signInState.errorMessage ?: "Invalid email or password"
                )
            }
        }
    }

    /**
     * Handle sign up attempt
     */
    fun onSignUp(username: String, fullName : String, email: String, password: String) {
        viewModelScope.launch {
            signUpState = signUpState.copy(isLoading = true, errorMessage = null)

            try {
                val userWithExistingUsername = model.getUser(username)
                signUpState = signUpState.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = "Username already taken"
                )
            }  catch (e: Exception) {
                // Username is available, proceed with sign up
                val result = authRepository.signUp(email, password)

                if (result) {
                    // Sign up succeeded in auth table, proceed to create user profile
                    createUserProfile(username, fullName, email)
                    var failed = false
                    var failedErrorMessage: String? = null
                    try {
                        model.loggedInUserId.value = model.getUser(username).id
                    } catch (e: Exception) {
                        failed = true
                        failedErrorMessage = "Sign up succeeded but failed to fetch user profile"
                    }

                    signUpState = signUpState.copy(
                        isLoading = false,
                        errorMessage = failedErrorMessage,
                        isSuccess = !failed
                    )
                    loginState = loginState.copy(
                        isLoading = false,
                        isSuccess = !failed,
                        errorMessage = null
                    )
                } else { // Sign up failed in auth table
                    signUpState = signUpState.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = authRepository.signUpState.errorMessage ?: "Sign up failed"
                    )
                    loginState = loginState.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = null
                    )
                }

            }

        }
    }

    fun onLogOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                model.loggedInUserId.value = ""

                // Reset login and signup states
                loginState = LoginUiState()
                signUpState = SignUpUiState()
            } catch (_: Exception) {

            }
        }
    }


    /**
     * Clear login error when user starts typing
     */
    fun clearLoginError() {
        loginState = loginState.copy(errorMessage = null)
    }

    /**
     * Clear sign up error when user starts typing
     */
    fun clearSignUpError() {
        signUpState = signUpState.copy(errorMessage = null)
    }


    /**
     * Create user profile in the public.Users table after successful signup in the auth.Users table
     */
    private suspend fun createUserProfile(userId: String, fullName: String, email: String) {
        try {
            model.createUser(
                id = userId,
                name = fullName,
                email = email
            )

            signUpState = signUpState.copy(
                isLoading = false,
                isSuccess = true
            )
        } catch (e: Exception) {
            signUpState = signUpState.copy(
                isLoading = false,
                errorMessage = "Account created but profile setup failed: ${e.message}"
            )
        }
    }
}


data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)


data class SignUpUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

