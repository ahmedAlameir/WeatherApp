package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.ui.screen.Home
import com.example.weatherapp.ui.screen.Setting

@Composable
fun NavGraph(navController: NavHostController,viewModel: MainViewModel) {
    NavHost(
        navController = navController,
        startDestination =  Screens.Home.route
    ) {
        composable(route = Screens.Home.route) {
            Home(viewModel = viewModel)
        }

        composable(route = Screens.Setting.route) {
            Setting(viewModel =viewModel)
        }
        composable(route = Screens.Favourite.route) {
            //call HomeScreen composable function here
        }
        composable(route = Screens.Notification.route) {
            //call HomeScreen composable function here
        }


    }
}
sealed class Screens(val route: String) {
    object Setting : Screens("setting")
    object Favourite : Screens("favourite")
    object Notification : Screens("notification")
    object Home : Screens("home")
}