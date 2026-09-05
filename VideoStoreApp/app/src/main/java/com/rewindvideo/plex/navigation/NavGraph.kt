package com.rewindvideo.plex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rewindvideo.plex.RewindApplication
import com.rewindvideo.plex.ui.screens.browse.BrowseScreen
import com.rewindvideo.plex.ui.screens.detail.DetailScreen
import com.rewindvideo.plex.ui.screens.login.LoginScreen
import com.rewindvideo.plex.ui.screens.player.PlayerScreen

private object Routes {
    const val LOGIN = "login"
    const val BROWSE = "browse"
    const val DETAIL = "detail/{ratingKey}"
    const val PLAYER = "player/{ratingKey}"

    fun detail(ratingKey: String) = "detail/$ratingKey"
    fun player(ratingKey: String) = "player/$ratingKey"
}

@Composable
fun RewindNavHost() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as RewindApplication
    val startDestination = if (app.repository.isSignedIn) Routes.BROWSE else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(onSignedIn = {
                navController.navigate(Routes.BROWSE) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }
        composable(Routes.BROWSE) {
            BrowseScreen(
                onItemClick = { item -> navController.navigate(Routes.detail(item.ratingKey)) },
                onSignedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.BROWSE) { inclusive = true }
                    }
                },
            )
        }
        composable(route = Routes.DETAIL) { backStackEntry ->
            val ratingKey = backStackEntry.arguments?.getString("ratingKey").orEmpty()
            DetailScreen(
                ratingKey = ratingKey,
                onBack = { navController.popBackStack() },
                onPlay = { item -> navController.navigate(Routes.player(item.ratingKey)) },
            )
        }
        composable(route = Routes.PLAYER) { backStackEntry ->
            val ratingKey = backStackEntry.arguments?.getString("ratingKey").orEmpty()
            PlayerScreen(ratingKey = ratingKey)
        }
    }
}
