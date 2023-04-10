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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.datasource.RepositoryImpl
import com.example.weatherapp.datasource.database.WeatherDataBase
import com.example.weatherapp.datasource.network.API
import com.example.weatherapp.navigation.NavGraph
import com.example.weatherapp.navigation.Screens
import com.example.weatherapp.types_enums.Language
import com.example.weatherapp.types_enums.LocationMethod
import com.example.weatherapp.types_enums.ThemeType
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.android.gms.location.*
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var editor : SharedPreferences.Editor
    private val weatherDatabase by lazy { WeatherDataBase.getDatabase(this).WeatherDao() }
    private val alertDatabase by lazy { WeatherDataBase.getDatabase(this).AlertDao() }

private var test = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory = MainViewModelFactory(RepositoryImpl(API.retrofitService,weatherDatabase,alertDatabase))
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
        viewModel.location.observe(this){
            editor.putFloat("lat",it.first.toFloat())
            editor.putFloat("lon",it.second.toFloat())
            editor.apply()
        }

        viewModel.locationMethod.observe(this){

            if (it==LocationMethod.GPS) getLastLocation()
            else if (it==LocationMethod.Gps2)getLastLocation()
        }



        setContent {
            val theme = viewModel.themeType.observeAsState().value
            WeatherAppTheme(darkTheme = theme==ThemeType.Dark) {
                var layoutDirection  by remember {
                    mutableStateOf(LayoutDirection.Ltr)
                }
                val language = viewModel.language.observeAsState().value
                if (language==Language.Arabic){
                    layoutDirection=LayoutDirection.Rtl
                }else{
                    layoutDirection=LayoutDirection.Ltr

                }
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection ) {
                    WeatherAppMain(viewModel)
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()){
            if (locationEnabled()){
                requestNewLocationData()
            }else{
                Toast.makeText(this,getString(R.string.turn_on_location), Toast.LENGTH_LONG).show()
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

                viewModel.setLocation(it.latitude,it.longitude,this@MainActivity)
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation()
            }
        }

    }
    companion object {
        private const val PERMISSION_ID = 44
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
        topBar = { WeatherAppBar(state,currentRoute,cityName,navController) },
        drawerContent = { DrawerView(navController,currentRoute,state) },
        modifier= Modifier,
        drawerGesturesEnabled = currentRoute!=Screens.Map.route


        ){
        Box(
            modifier= Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            NavGraph(viewModel =viewModel, navController =  navController, state =  state)
        }
    }
}

@Composable
fun WeatherAppBar(
    state: ScaffoldState,
    currentRoute: String?,
    cityName: State<String>,
    navController: NavHostController

)  {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = when(currentRoute){
                    (Screens.Home.route)->cityName.value
                    (Screens.Setting.route)-> stringResource(R.string.setting)
                    (Screens.Notification.route)-> stringResource(R.string.notification)
                    (Screens.Favourite.route)-> stringResource(R.string.favourite)
                    (Screens.Map.route),(Screens.FavMap.route)-> stringResource(R.string.map)
                        (Screens.FavHome.route)-> stringResource(R.string.chosen_city)
                        (Screens.Alert.route)-> stringResource(R.string.new_alert)
                    else -> ""
                    }
                )
            }
        },
        navigationIcon = {
            when(currentRoute){
                Screens.Home.route, Screens.Favourite.route, Screens.Setting.route ->{
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.navigation_drawer),
                            contentDescription = null,
                            modifier = Modifier
                                .clickable(onClick = {

                                    scope.launch {
                                        state.drawerState.open()
                                    }
                                })
                                .padding(start = 16.dp),
                            tint = Color.Unspecified

                        )
                    }
                }
                null ->{}
                else ->{
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = null,
                            modifier = Modifier
                                .clickable(onClick = {

                                    navController.popBackStack()
                                })
                                .padding(start = 16.dp)
                                .size(36.dp),
                            tint = Color.Unspecified

                        )
                    }
                }
            }


        },
        elevation = 0.dp,
        actions = {WeatherAppBarActions(navController,currentRoute)}
        ,backgroundColor = Color.Transparent
    , modifier = Modifier.height(72.dp)


    )

}
@Composable
fun WeatherAppBarActions(navController: NavHostController, currentRoute: String?) {
    IconButton(
        modifier = Modifier.padding(end=16.dp),
        onClick = {
            navController.navigate(Screens.Notification.route)
    }) {
        when(currentRoute) {
            Screens.Home.route, Screens.Favourite.route, Screens.Setting.route -> {
                Icon(
                    painter = painterResource(id = R.drawable.notification),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            else ->{}
        }
    }
}


@Composable
fun DrawerView(navController: NavHostController, currentRoute: String?, state: ScaffoldState) {
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.background(color = MaterialTheme.colors.primary)) {
        Column(modifier = Modifier.fillMaxHeight()) {

            AddDrawerHeader {
                scope.launch {
                    state.drawerState.close()
                }
            }
            AddDrawerContentView(stringResource(R.string.home),R.drawable.home,currentRoute==Screens.Home.route){
                navController.navigate(Screens.Home.route)
                scope.launch {
                    state.drawerState.close()
                }
            }
            Spacer(modifier = Modifier.size(16.dp) )
            AddDrawerContentView(stringResource(R.string.setting),R.drawable.settings,currentRoute==Screens.Setting.route){
                navController.navigate(Screens.Setting.route)
                scope.launch {
                    state.drawerState.close()
                }
            }
            AddDrawerContentView(stringResource(R.string.favourite),R.drawable.heart,currentRoute==Screens.Favourite.route){
                navController.navigate(Screens.Favourite.route)
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
        AddDrawerHeader {}
    }
}

@Composable
fun AddDrawerHeader(onclick: () -> Unit) {
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
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onclick() }
                )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = stringResource(R.string.skysight), fontSize = 24.sp)
                Text(text = stringResource(R.string.your_guide_to_the_sky))

            }

        }

    }
}