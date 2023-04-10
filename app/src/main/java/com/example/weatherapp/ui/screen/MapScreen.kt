package com.example.weatherapp.ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.MainViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun Map(viewModel: MainViewModel,navController: NavController){

    val context = LocalContext.current

    MapScreen{
        Log.i("TAG", "Map: "+it)
        viewModel.setLocation(it.latitude, it.longitude, context)
            navController.popBackStack()

    }
}

@Composable
 fun MapScreen(

    onClick:(LatLng)->Unit
) {
    var latLng by remember {
        mutableStateOf(LatLng(0.0,0.0))
    }
    Box(Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),

            onMapClick = {
                latLng = it

            }
        ) {

            Marker(
                state = MarkerState(position = latLng),
            )

        }
        FloatingActionButton(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 112.dp),
            onClick = {
                onClick(latLng)
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
}