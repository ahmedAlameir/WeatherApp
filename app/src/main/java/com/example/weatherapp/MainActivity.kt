package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.datasource.Repository
import com.example.weatherapp.datasource.network.API
import com.example.weatherapp.navigation.NavGraph
import com.example.weatherapp.navigation.Screens
import com.example.weatherapp.types_enums.LocationMethod
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.android.gms.location.*
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var editor : SharedPreferences.Editor
    private val PERMISSION_ID = 44

private var test = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory = MainViewModelFactory(Repository(API.retrofitService))
        viewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val sharedPreference = this.getSharedPreferences("setting",Context.MODE_PRIVATE)
        editor = sharedPreference.edit()

        val lat = sharedPreference.getFloat("lat",91f)
        val lon =  sharedPreference.getFloat("lon",181f)
        if(lat!=91f&& lon!=181f ){
            viewModel.setLocation(lat.toDouble(),lon.toDouble(),this)
        } else{
            viewModel.setToGPS()
        }

        viewModel.locationMethod.observe(this){
            when (it){
                LocationMethod.GPS -> getLastLocation()
                LocationMethod.MAP -> TODO()
                null -> TODO()
            }
        }
        setContent {
            WeatherAppTheme {
                WeatherAppMain(viewModel)
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
                editor.putFloat("lat",it.latitude.toFloat())
                editor.putFloat("lon",it.longitude.toFloat())
                editor.apply()
                viewModel.setLocation(it.latitude,it.longitude,this@MainActivity)
            }
        }
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}




@Composable
fun WeatherAppMain(viewModel: MainViewModel) {
    val navController=rememberNavController()
    val state = rememberScaffoldState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute =navBackStackEntry?.destination?.route
    val cityName =viewModel.city.observeAsState("")
    Scaffold(
        scaffoldState = state,
        backgroundColor = MaterialTheme.colors.background,
        topBar = { WeatherAppBar(state,currentRoute,cityName) },
        drawerContent = { DrawerView(navController,currentRoute,state) },
        modifier= Modifier


        ){
        Box(
            modifier= Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            NavGraph(viewModel =viewModel, navController =  navController)

        }
    }
}

@Composable
fun WeatherAppBar(
    state: ScaffoldState,
    currentRoute: String?,
    cityName: State<String>

)  {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = when(currentRoute){
                    (Screens.Home.route)->cityName.value
                    (Screens.Setting.route)->"Setting"
                    (Screens.Notification.route)->"Notification"
                    (Screens.Favourite.route)->"Favourite"

                    else -> "{}"
                })
            }
        },
        navigationIcon = {
            Row {
                Icon(
                    painter =  painterResource(id = R.drawable.navigation_drawer),
                    contentDescription = null,
                    modifier = Modifier.clickable(onClick = {

                        scope.launch {
                             state.drawerState.open()
                        }
                    }),
                    tint = Color.Unspecified

                )
            }
        },
        elevation = 0.dp,
        actions = {WeatherAppBarActions()}
        ,backgroundColor = Color.Transparent
    , modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)

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
fun DrawerView(navController: NavHostController, currentRoute: String?, state: ScaffoldState) {
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.background(color = MaterialTheme.colors.primary)) {
        Column(modifier = Modifier.fillMaxHeight()) {

            AddDrawerHeader()
            AddDrawerContentView("Home",R.drawable.home,currentRoute==Screens.Home.route){
                navController.navigate(Screens.Home.route)
                scope.launch {
                    state.drawerState.close()
                }
            }
            Spacer(modifier = Modifier.size(16.dp) )
            AddDrawerContentView("Setting",R.drawable.settings,currentRoute==Screens.Setting.route){
                navController.navigate(Screens.Setting.route)
                scope.launch {
                    state.drawerState.close()
                }
            }
            AddDrawerContentView("Favourite",R.drawable.heart,currentRoute==Screens.Favourite.route){
                navController.navigate(Screens.Home.route)
                scope.launch {
                    state.drawerState.close()
                }
            }
        }

    }
}



@Composable
fun AddDrawerContentView(itemName: String,
                         imageID: Int,
                         selected:Boolean,
                         onclick:()->Unit ) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        backgroundColor = if(selected) {
            MaterialTheme.colors.primaryVariant
        }else{
             Color.Transparent
             },
        modifier = Modifier
            .fillMaxWidth()
            .size(56.dp)
            .padding(start = 12.dp, end = 12.dp)
            .clickable {
                onclick()
            }
    ){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = imageID),
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = itemName,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            }
        }
}
@Composable
@Preview
fun DrawerHeaderPrev(){
    Surface(color = Color(0xff372988)) {
        AddDrawerHeader()
    }
}

@Composable
fun AddDrawerHeader() {
    Card(
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .size(76.dp)
            .padding(top = 12.dp, start = 12.dp)
        ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
                )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = "Skysight", fontSize = 24.sp)
                Text(text = "Your Guide to the Sky")

            }

        }

    }
}