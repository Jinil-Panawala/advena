package com.example.advena.ui.components


import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavBar(navController: NavController) {
    val navItems = listOf(
        NavItem("home", Icons.Default.Home, "Home"),
        NavItem("events", Icons.Default.DateRange, "Events"),
        NavItem("profile", Icons.Default.Person, "Profile")
    )

    val navBarColor = Color(0xFFDD88CF) // Pink-
    val selectedIconColor = Color.White
    val unselectedIconColor = Color.Black.copy(alpha = 0.75f)
    val iconModifier = Modifier.size(55.dp)
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = selectedIconColor,
        unselectedIconColor = unselectedIconColor,
        indicatorColor = Color.Transparent
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = navBarColor) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute?.startsWith(item.route) ?: false,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.description,
                        modifier = iconModifier
                    )
                },
                colors = itemColors
            )
        }
    }
}

data class NavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val description: String
)

