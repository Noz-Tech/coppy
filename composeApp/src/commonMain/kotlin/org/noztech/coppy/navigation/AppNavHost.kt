package org.noztech.coppy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.noztech.coppy.core.AppSettings
import org.noztech.coppy.feature.auth.AuthScreen
import org.noztech.coppy.feature.home.presentation.CreateListScreen
import org.noztech.coppy.feature.home.presentation.EntryDetailScreen
import org.noztech.coppy.feature.home.presentation.GroupScreen
import org.noztech.coppy.feature.home.presentation.HomeScreen
import org.noztech.coppy.feature.settings.presentation.SettingsScreen
import org.noztech.coppy.feature.welcome.presentation.WelcomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    appSettings: AppSettings,
    onWelcomeCompleted: () -> Unit = {},
) {

    NavHost(
        navController = navController,
        startDestination = when {
            appSettings.isFirstLaunch() -> GuestRoutes.Welcome
            appSettings.isLockOnLaunchEnabled() -> GuestRoutes.Auth
            else -> AuthRoutes.Home
        }
    ) {
        composable<GuestRoutes.Welcome> {
            WelcomeScreen(
                navController = navController,
                onWelcomeCompleted = onWelcomeCompleted,
            )
        }
        composable<GuestRoutes.Auth> { AuthScreen(navController, appSettings) }

        composable<AuthRoutes.Home> { HomeScreen(navController) }
        composable<AuthRoutes.Group> { GroupScreen(navController) }


        composable<AuthRoutes.CreateList> { backStackEntry ->
            val profile: AuthRoutes.CreateList = backStackEntry.toRoute()
            CreateListScreen(navController, profile.id)
        }
        composable<AuthRoutes.EntryDetail> { backStackEntry ->
            val route: AuthRoutes.EntryDetail = backStackEntry.toRoute()
            EntryDetailScreen(navController, route.id)
        }

        composable<AuthRoutes.Settings> { SettingsScreen(navController) }

    }
}
