package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.datasource.Repository
import com.example.weatherapp.datasource.network.API
import com.example.weatherapp.ui.screen.Home
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.android.gms.location.*
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_ID = 44

private var test = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory = MainViewModelFactory(Repository(API.retrofitService))
        viewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        viewModel.locationMethod.observe(this){
            when (it){
                LocationMethod.GPS -> getLastLocation()
                LocationMethod.MAP -> TODO()
                null -> TODO()
            }
        }
        viewModel.setToGPS()
        setContent {
            WeatherAppTheme {
                WeatherAppMain()
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()){
            if (locationEnabled()){
                requestNewLocationData()
            }else{
                Toast.makeText(this,"turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        }else{
            requestPermissions()
        }
    }
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION)==
                PackageManager.PERMISSION_GRANTED||ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED
    }
    private fun locationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest =  LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
            .setMinUpdateIntervalMillis(50)
            .setMaxUpdateDelayMillis(200).setMaxUpdates(1)
            .build()


        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION ,
                Manifest.permission.ACCESS_COARSE_LOCATION

            ),
            PERMISSION_ID
        )
    }
    private var locationCallback: LocationCallback
            = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let {
                viewModel.setLocation(it.latitude,it.longitude)
            }
        }
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}




@Preview
@Composable
fun WeatherAppMain(){
    val state = rememberScaffoldState()
    Scaffold(
        scaffoldState = state,
        backgroundColor = MaterialTheme.colors.primary,
        topBar = { WeatherAppBar(state) }

    ){
        Box(
            modifier= Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colors.primary,
                            Color(0xff5241B9),
                            MaterialTheme.colors.primary
                        )
                    )
                )
                .fillMaxSize()
        ) {
            NavGraf(Modifier.padding(it))

        }
    }
}

@Composable
fun WeatherAppBar(
    state: ScaffoldState
)  {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "SemicolonSpace")
            }
        },
        navigationIcon = {
            Row {
                Icon(
                    painter =  painterResource(id = R.drawable.burger_menu),
                    contentDescription = null,
                    modifier = Modifier.clickable(onClick = {

                        scope.launch {
                            if (state.drawerState.isClosed) state.drawerState.open() else state.drawerState.close()
                        }
                    }),
                    tint = Color.Unspecified

                )
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        actions = {WeatherAppBarActions()}
    , modifier = Modifier.padding(start = 16.dp,top=16.dp,end =16.dp)
    )
}
@Composable
fun WeatherAppBarActions(){
    IconButton(onClick = {

    }) {
        Icon(
            painter =  painterResource(id = R.drawable.notification),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun NavGraf(modifier: Modifier){
    var direction by rememberSaveable {
        mutableStateOf( LayoutDirection.Ltr )
    }

        CompositionLocalProvider(LocalLayoutDirection provides direction ) {

            Home()
        }
    }
