package com.crossbow.translator.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.crossbow.translator.ui.screens.camera.CameraScanScreen
import com.crossbow.translator.ui.screens.camera.CameraScanViewModel
import com.crossbow.translator.ui.screens.conversation.ConversationScreen
import com.crossbow.translator.ui.screens.conversation.ConversationViewModel
import com.crossbow.translator.ui.screens.dictionary.DictionaryScreen
import com.crossbow.translator.ui.screens.dictionary.DictionaryViewModel
import com.crossbow.translator.ui.screens.favorites.FavoritesScreen
import com.crossbow.translator.ui.screens.favorites.FavoritesViewModel
import com.crossbow.translator.ui.screens.history.HistoryScreen
import com.crossbow.translator.ui.screens.history.HistoryViewModel
import com.crossbow.translator.ui.screens.home.HomeScreen
import com.crossbow.translator.ui.screens.home.HomeViewModel
import com.crossbow.translator.ui.screens.practice.PracticeScreen
import com.crossbow.translator.ui.screens.practice.PracticeViewModel
import com.crossbow.translator.ui.screens.settings.SettingsScreen
import com.crossbow.translator.ui.screens.settings.SettingsViewModel
import com.crossbow.translator.ui.screens.voice.VoiceScreen
import com.crossbow.translator.ui.screens.voice.VoiceViewModel
import com.crossbow.translator.ui.theme.BrandIndigo

@Composable
fun CrossbowNavGraph(
    homeViewModel: HomeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .navigationBarsPadding()
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .clip(RoundedCornerShape(26.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                RoundedCornerShape(26.dp)
                            ),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        Screen.bottomNavItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    screen.icon?.let { icon ->
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = screen.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BrandIndigo,
                                    selectedTextColor = BrandIndigo,
                                    indicatorColor = BrandIndigo.copy(alpha = 0.15f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            // Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToVoice = { navController.navigate(Screen.Voice.route) },
                    onNavigateToConversation = { navController.navigate(Screen.Conversation.route) },
                    onNavigateToCamera = { navController.navigate(Screen.CameraScan.route) },
                    onNavigateToDictionary = { navController.navigate(Screen.Dictionary.route) },
                    onNavigateToPractice = { navController.navigate(Screen.Practice.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Voice Screen
            composable(Screen.Voice.route) {
                val voiceViewModel: VoiceViewModel = viewModel()
                VoiceScreen(
                    viewModel = voiceViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Conversation Screen
            composable(Screen.Conversation.route) {
                val convViewModel: ConversationViewModel = viewModel()
                ConversationScreen(viewModel = convViewModel)
            }

            // Camera Scan Screen
            composable(Screen.CameraScan.route) {
                val cameraViewModel: CameraScanViewModel = viewModel()
                CameraScanScreen(
                    viewModel = cameraViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Dictionary Screen
            composable(Screen.Dictionary.route) {
                val dictViewModel: DictionaryViewModel = viewModel()
                DictionaryScreen(
                    viewModel = dictViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Language Practice Screen
            composable(Screen.Practice.route) {
                val practiceViewModel: PracticeViewModel = viewModel()
                PracticeScreen(viewModel = practiceViewModel)
            }

            // Favorites Screen
            composable(Screen.Favorites.route) {
                val favViewModel: FavoritesViewModel = viewModel()
                FavoritesScreen(viewModel = favViewModel)
            }

            // History Screen
            composable(Screen.History.route) {
                val histViewModel: HistoryViewModel = viewModel()
                HistoryScreen(
                    viewModel = histViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onReuseItem = { item ->
                        homeViewModel.reuseHistory(item)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // Settings Screen
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
