package com.example.advena.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.advena.domain.Model
import kotlinx.coroutines.launch

class EditProfileViewModel(
    model: Model,
    private val onSaveComplete: (() -> Unit)? = null
) : BaseViewModel(model) {

    var name by mutableStateOf("")
    var bio by mutableStateOf("")

    var profileInitial by mutableStateOf("")
    var isSaving by mutableStateOf(false)

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            // Don't attempt to load if userId is empty
            if (loggedInUserId.isEmpty()) {
                return@launch
            }

            try {
                val user = model.getUser(loggedInUserId)
                name = user.name
                profileInitial = user.name.first().uppercase()
                bio = user.bio ?: ""
            } catch (e: Exception)  {
                println("Error loading user data: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun updateName(newName: String) {
        name = newName
        if (newName.isNotBlank()) {
            profileInitial = newName.first().uppercase()
        }
    }

    fun updateBio(newBio: String) {
        // Limit bio to 200 characters
        if (newBio.length <= 200) {
            bio = newBio
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            if (loggedInUserId.isEmpty()) {
                return@launch
            }

            try {
                isSaving = true

                val user = model.getUser(loggedInUserId)
                user.name = name
                user.bio = bio
                model.updateUser(user)

                isSaving = false

                // Call the callback to signal save completion
                onSaveComplete?.invoke()

            } catch (e: Exception)  {
                println("Error saving profile: ${e.message}")
                e.printStackTrace()
                isSaving = false
            }
        }
    }
}