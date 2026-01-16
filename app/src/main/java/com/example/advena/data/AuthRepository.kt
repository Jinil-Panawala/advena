package com.example.advena.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
/**
 * Repo for authentication, logging in, signing up, and logging out operations using Supabase Auth.
 * Uses supabase's inbuilt auth.Users table (different from our public Users table) and supabase's inbuilt
 * authentication functions. Can be further extended in the future to things like checking email
 * verification status, reset password, check user sessions, etc.
 */
class AuthRepository {

    private val supabase = SupabaseClient.client

    var signInState by mutableStateOf(SignInState())
        private set

    var signUpState by mutableStateOf(SignUpState())
        private set

    // Sign up a new user with email and password
    suspend fun signUp(email: String, password: String): Boolean {
        try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            // After signup, attempt to get the user info
            val userInfo = supabase.auth.currentUserOrNull()
            if (userInfo != null) {
                signUpState = signUpState.copy(
                    isSuccess = true,
                    errorMessage = null
                )
                return true
            } else {
                signUpState = signUpState.copy(
                    isSuccess = false,
                    errorMessage = "Sign up succeeded but user info not available"
                )
                return false
            }
        } catch (e: Exception) {
            if (e.message?.contains("HTTP request to") == true) {
                signUpState = signUpState.copy(
                    isSuccess = false,
                    errorMessage = "Please check your internet connection and try again"
                )
            } else {
                signUpState = signUpState.copy(
                    isSuccess = false,
                    errorMessage = e.message
                )
            }
            return false
        }
    }


    // Sign in an existing user with email and password
    suspend fun signIn(email: String, password: String): Boolean {
        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            // After login, attempt to get the user info
            val userInfo = supabase.auth.currentUserOrNull()
            if (userInfo != null) {
                signInState = signInState.copy(
                    isSuccess = true,
                    errorMessage = null
                )
                return true
            } else {
                signInState = signInState.copy(
                    isSuccess = false,
                    errorMessage = "Login succeeded but user info not available"
                )
                return false
            }
        } catch (e: Exception) {
            if (e.message?.contains("HTTP request to") == true) {
                signInState = signInState.copy(
                    isSuccess = false,
                    errorMessage = "Please check your internet connection and try again"
                )
            } else {
                signInState = signInState.copy(
                    isSuccess = false,
                    errorMessage = e.message
                )
            }
            return false
        }
    }

    // Sign out the current user
    suspend fun signOut() {
        try {
            supabase.auth.signOut()
        } catch (e: Exception) {
            println("Error signing out: ${e.message}")
        }
    }

    // TODO: reset password



}

data class SignInState(
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class SignUpState(
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

