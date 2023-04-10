package com.example.weatherapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R
import com.example.weatherapp.RespondState
import com.example.weatherapp.model.FavouriteLocation
import com.example.weatherapp.navigation.Screens

@Composable
fun Favourite(viewModel: MainViewModel,navController:NavController){
   viewModel.getAllFav()
    var dialogOpen by remember {
        mutableStateOf(false)
    }
    var favouriteLocation by remember {
        mutableStateOf(FavouriteLocation("",0.0,0.0))
    }
    val favouriteLocations = viewModel.favList.collectAsState().value
    Box(Modifier.fillMaxSize()) {
        LazyColumn {
            items(favouriteLocations) {

                FavouriteItem(favouriteLocation = it,
                    onClickDelete = {
                        favouriteLocation=it
                        dialogOpen =true
                    },
                    onClick = {
                        viewModel.getFavWeatherData(it.lat,it.lon)
                        navController.navigate(Screens.FavHome.route)
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 64.dp),
            onClick = {
                navController.navigate(Screens.FavMap.route)

            },

            shape = CircleShape,

            backgroundColor = MaterialTheme.colors.primary,

            contentColor = MaterialTheme.colors.onPrimary,
        ) {
            // on below line we are
            // adding icon for fab.
            Icon(Icons.Filled.Add, "")
        }
    }

    if (dialogOpen) {
        AlertDialog(
            onDismissRequest = {

                dialogOpen = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFav(favouriteLocation)
                        dialogOpen = false
                    }
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dialogOpen = false
                    }
                ) {
                    Text(text = stringResource(R.string.dismiss))
                }
            },
            title = {
                Text(text = stringResource(R.string.delete_Item) , color = Color.Black)
            },
            text = {
                Text(text = stringResource(R.string.delete_confirmation),color = Color.Black)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(5.dp),
            backgroundColor = Color.White
        )
    }

}
@Composable
fun FavouriteItem(favouriteLocation: FavouriteLocation,onClickDelete:()->Unit,onClick:()->Unit) {
    Row(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colors.primaryVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = favouriteLocation.name,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        Icon(painter = painterResource(id = R.drawable.trash),
            contentDescription = "",
            modifier = Modifier
                .size(44.dp)
                .padding(end = 16.dp)
                .clickable {
                    onClickDelete()
                })
    }
}

@Composable
fun FavouriteData(viewModel:MainViewModel,state:ScaffoldState){
    val weather= viewModel.weatherFavData.collectAsState().value
    when(weather){
        is RespondState.Loading -> {}
        is RespondState.Error -> {
            LaunchedEffect(state.snackbarHostState) {
                state.snackbarHostState.showSnackbar(
                    message = weather.error.toString()

                )
            }
        }
        is RespondState.Success -> DataScreen(viewModel, weather.data)

    }
}
@Composable
fun FavMap(viewModel: MainViewModel,navController: NavController){
    val context = LocalContext.current
    MapScreen{
        viewModel.insertFav(it.latitude,it.longitude,context)
        navController.popBackStack()
    }
}