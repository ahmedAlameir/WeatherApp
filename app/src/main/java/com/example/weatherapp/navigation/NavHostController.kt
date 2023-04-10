package com.example.weatherapp.navigation

import android.os.Build
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.ui.screen.*

@Composable
fun NavGraph(navController: NavHostController,viewModel: MainViewModel,state: ScaffoldState) {
    NavHost(
        navController = navController,
        startDestination =  Screens.Home.route
    ) {
        composable(route = Screens.Home.route) {
            Home(viewModel = viewModel,state)
        }

        composable(route = Screens.Setting.route) {
            Setting(viewModel =viewModel,navController)
        }
        composable(route = Screens.Favourite.route) {
            Favourite(viewModel = viewModel, navController = navController)
        }
        composable(route = Screens.Notification.route) {
            Alerts(viewModel, navController)
        }
        composable(route = Screens.Map.route) {
            Map(viewModel,navController)
        }
        composable(route = Screens.FavHome.route) {
            FavouriteData(viewModel = viewModel, state = state)
        }
        composable(route = Screens.FavMap.route) {
            FavMap(viewModel,navController)
        }
        composable(route = Screens.Alert.route) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Alert(viewModel, navController)
            }
        }


    }
}
sealed class Screens(val route: String) {
    object Setting : Screens("setting")
    object Favourite : Screens("favourite")
    object Notification : Screens("notification")
    object Home : Screens("home")
    object FavHome : Screens("FavHome")
    object Map : Screens("map")
    object FavMap : Screens("favmap")
    object Alert : Screens("Alert")

}