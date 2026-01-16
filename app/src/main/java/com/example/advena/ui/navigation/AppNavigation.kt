package com.example.advena.ui.navigation

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.advena.data.GeocodingRepository
import com.example.advena.data.remote.GeocodingApi
import com.example.advena.data.remote.GeocodingService
import com.example.advena.data.DbStorage
import com.example.advena.domain.Model
import com.example.advena.ui.screens.WelcomeScreen
import com.example.advena.ui.screens.LoginScreen
import com.example.advena.ui.screens.SignUpScreen
import com.example.advena.ui.screens.HomeScreen
import com.example.advena.ui.components.LoggedInScaffold
import com.example.advena.ui.screens.EventCreationScreen
import com.example.advena.ui.screens.EventsScreen
import com.example.advena.ui.screens.EditProfileScreen
import com.example.advena.ui.screens.FindFriendsScreen
import com.example.advena.ui.screens.ProfileScreen
import com.example.advena.viewmodel.FindFriendsViewModel
import com.example.advena.viewmodel.ProfileViewModel
import com.example.advena.viewmodel.AuthViewModel
import com.example.advena.viewmodel.EventsViewModel
import com.example.advena.viewmodel.EditProfileViewModel
import com.example.advena.viewmodel.HomeViewModel
import com.example.advena.viewmodel.EventCreationViewModel

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val FIND_FRIENDS = "find_friends"
    const val EDIT_PROFILE = "edit_profile"
    const val EVENTS = "events"
    const val CREATE_EVENT = "create_event"
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    model: Model // Accept Model from MainActivity
) {
    // Collect logged-in user ID as State to avoid calling StateFlow.value in composition
    val loggedInUserId by model.loggedInUserId.collectAsState()

    // authView model needs to ensure DbStorage is used even if mockstorage is passed in for testing
    val authViewModel = remember {
        val storage = model.storage
        val finalModel = if (storage is DbStorage) model else Model(DbStorage())
        AuthViewModel(model = finalModel)
    }
    val homeViewModel = remember (loggedInUserId) { HomeViewModel(model = model) }
    val eventsViewModel = remember (loggedInUserId) { EventsViewModel(model = model) }
    val httpClient = GeocodingApi.client
    val geocodingService = GeocodingService(client = httpClient)
    val geocodingRepository = GeocodingRepository(geocodingService)
    val eventCreationViewModel = remember {
        EventCreationViewModel(
            model = model,
            geocodingRepository = geocodingRepository
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME,
        modifier = modifier
    ) {
        // Welcome Screen
        composable(
            route = Routes.WELCOME,
            enterTransition = {
                // Fade in when entering
                fadeIn(animationSpec = tween(800))
            },
            exitTransition = {
                // No exit transition
                null
            },
        ) {
            WelcomeScreen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                },
                onSignUpClick = {
                    navController.navigate(Routes.SIGNUP)
                }
            )
        }

        // Login Screen
        composable(
            route = Routes.LOGIN,
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {
            // Navigate to Home on successful login
            LaunchedEffect(authViewModel.loginState.isSuccess) {
                if (authViewModel.loginState.isSuccess) {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                viewModel = authViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSignUpClick = {
                    navController.navigate(Routes.SIGNUP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Sign Up Screen
        composable(
            route = Routes.SIGNUP,
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {
            // Navigate to Home on successful signup
            LaunchedEffect(authViewModel.signUpState.isSuccess) {
                if (authViewModel.signUpState.isSuccess) {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            }

            SignUpScreen(
                viewModel = authViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSignInClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(
            route = Routes.HOME,
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {
            LoggedInScaffold(navController = navController) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onViewAttendees = { eventId ->
                        navController.navigate("${Routes.FIND_FRIENDS}?listType=event&id=$eventId")
                    },
                    onUserClick = { clickedUserId ->
                        navController.navigate("${Routes.PROFILE}?userId=$clickedUserId")
                    }
                )
            }
        }

        // Event Screen
        composable(
            route = Routes.EVENTS,
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {
            LoggedInScaffold(navController = navController) {
                EventsScreen(
                    viewModel = eventsViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToCreate = {
                        eventCreationViewModel.loadEvent(null)
                        navController.navigate(Routes.CREATE_EVENT)
                        eventsViewModel.onEditEvent(null)
                    },
                    onNavigateToEdit = {
                        eventCreationViewModel.loadEvent(eventsViewModel.uiState.selectedEvent)
                        navController.navigate(Routes.CREATE_EVENT)
                    },
                    onViewAttendees = { eventId ->
                        navController.navigate("${Routes.FIND_FRIENDS}?listType=event&id=$eventId")
                    },
                    onUserClick = { clickedUserId ->
                        navController.navigate("${Routes.PROFILE}?userId=$clickedUserId")
                    }
                )
            }
        }

        composable(
            route = Routes.CREATE_EVENT,
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {
            EventCreationScreen(
                navController = navController,
                viewModel = eventCreationViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddClick = {
                    eventsViewModel.onEditEvent(null)
                    navController.popBackStack()
                }
            )
        }

        // Profile Screen
        composable(
            route = Routes.PROFILE + "?userId={userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                    nullable = true
                }
            ),
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.takeIf { it.isNotEmpty() }
                ?: loggedInUserId.takeIf { it.isNotEmpty() }

            if (userId == null || userId.isEmpty()) {
                // If no valid userId, navigate back to login
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
                return@composable
            }

            val profileViewModel = remember(userId) { ProfileViewModel(model, userId = userId) }

            // Refresh user data when returning from Edit Profile
            val shouldRefresh by backStackEntry.savedStateHandle
                .getStateFlow("profile_updated", false)
                .collectAsState()

            LaunchedEffect(shouldRefresh) {
                if (shouldRefresh) {
                    profileViewModel.refreshUserBioName()
                    backStackEntry.savedStateHandle["profile_updated"] = false
                }
            }

            LoggedInScaffold(navController = navController) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSearchClick = { navController.navigate(Routes.FIND_FRIENDS) },
                    onEditClick = { navController.navigate(Routes.EDIT_PROFILE) },
                    onFriendsClick = { listType, userId ->
                        navController.navigate("${Routes.FIND_FRIENDS}?listType=$listType&id=$userId")
                    },
                    onLogout = {
                        authViewModel.onLogOut()
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onViewAttendees = { eventId ->
                        navController.navigate("${Routes.FIND_FRIENDS}?listType=event&id=$eventId")
                    },
                    onUserClick = { clickedUserId ->
                        navController.navigate("${Routes.PROFILE}?userId=$clickedUserId")
                    }
                )
            }
        }

        // Find Friends Screen
        composable(
            route = "${Routes.FIND_FRIENDS}?listType={listType}&id={id}",
            arguments = listOf(
                navArgument("listType") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                }
            ),
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val listType = backStackEntry.arguments?.getString("listType") ?: ""
            val id = backStackEntry.arguments?.getString("id") ?: loggedInUserId

            val findFriendsViewModel = remember {
                FindFriendsViewModel(model, listType = listType, id = id)
            }

            FindFriendsScreen(
                viewModel = findFriendsViewModel,
                onBackClick = { navController.popBackStack() },
                onUserClick = { clickedUserId ->
                    navController.navigate("${Routes.PROFILE}?userId=$clickedUserId")
                }
            )
        }

        // Edit Profile Screen
        composable(
            route = Routes.EDIT_PROFILE,
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {
            val editProfileViewModel = remember {
                EditProfileViewModel(
                    model = model,
                    onSaveComplete = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("profile_updated", true)
                        navController.popBackStack()
                    }
                )
            }

            EditProfileScreen(
                viewModel = editProfileViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
