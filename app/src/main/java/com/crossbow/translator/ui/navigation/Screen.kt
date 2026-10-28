package com.crossbow.translator.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Conversation : Screen("conversation", "Chat", Icons.Default.Chat)
    data object Practice : Screen("practice", "Practice", Icons.Default.School)
    data object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    // Sub-screens
    data object Voice : Screen("voice", "Voice Translation")
    data object CameraScan : Screen("camera_scan", "Scan Text")
    data object Dictionary : Screen("dictionary", "Dictionary", Icons.Default.Book)
    data object History : Screen("history", "History")

    companion object {
        val bottomNavItems = listOf(Home, Conversation, Practice, Favorites, Settings)
    }
}
