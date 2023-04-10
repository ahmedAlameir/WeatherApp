package com.example.weatherapp.ui.screen

import android.content.res.Resources.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R
import com.example.weatherapp.navigation.Screens
import com.example.weatherapp.types_enums.Language

import com.example.weatherapp.types_enums.TempTypes
import com.example.weatherapp.types_enums.ThemeType
import com.example.weatherapp.types_enums.WindTypes
import com.example.weatherapp.ui.theme.WeatherAppTheme

@Preview
@Composable
fun Prev(){
    WeatherAppTheme {
        Surface(color = MaterialTheme.colors.primary) {
            //Setting()
        }
    }
}
@Composable
fun Setting(viewModel: MainViewModel,navController: NavController){
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colors.primary
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LocationType(viewModel,navController)
        Spacer(modifier = Modifier.size(24.dp))
        ThemeType(viewModel)
        Spacer(modifier = Modifier.size(24.dp))
        TemperatureType(viewModel)
        Spacer(modifier = Modifier.size(24.dp))
        WindType(viewModel)
        Spacer(modifier = Modifier.size(24.dp))
        LanguageType(viewModel)
    }
}

@Composable
fun LocationType(viewModel: MainViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
    ) {
        Text(text = stringResource(R.string.change_location), fontSize = 16.sp)
        Spacer(modifier = Modifier.size(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colors.primaryVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        viewModel.setToGPS()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.gps), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.size(28.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .weight(1f)
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colors.primaryVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        viewModel.setToMap()
                        navController.navigate(Screens.Map.route)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text =stringResource(R.string.map), fontSize = 14.sp)
            }

        }
    }
}
@Composable
fun TemperatureType(viewModel: MainViewModel) {
    val temp =viewModel.tempType.observeAsState()
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
    ) {
        Text(text = stringResource(R.string.temperature), fontSize = 16.sp)
        Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.primaryVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .selectableGroup(),
            horizontalAlignment = Alignment.Start
        ) {
            RadioButtonItem(selected = temp.value== TempTypes.Kelvin,
                            stringResource(R.string.kelvin)
                        ){
                viewModel.setToKelvin()
            }
            RadioButtonItem(selected = temp.value==TempTypes.Celsius,
                            stringResource(R.string.celsius)
                        ){
                viewModel.setToCelsius()

            }
            RadioButtonItem(selected =  temp.value==TempTypes.Fahrenheit,
                            stringResource(R.string.fahrenheit)
                        ){
                viewModel.setToFahrenheit()

            }
        }
    }
}
@Composable
fun WindType(viewModel: MainViewModel) {
    val wind = viewModel.windType.observeAsState()
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
    ) {
        Text(text = stringResource(R.string.wind_speed), fontSize = 16.sp)
        Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.primaryVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .selectableGroup(),
            horizontalAlignment = Alignment.Start
        ) {
            RadioButtonItem(selected = wind.value== WindTypes.MeterSec,"Meter/Second"){
                viewModel.setToMeterPerSec()
            }
            RadioButtonItem(selected = wind.value==WindTypes.MilesHour,"Mile/hour"){
                viewModel.setToMilesPerHour()
            }

        }
    }
}
@Composable
fun LanguageType(viewModel: MainViewModel) {
    var language =viewModel.language.observeAsState()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
    ) {
        Text(text = stringResource(R.string.change_language), fontSize = 16.sp)
        Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.primaryVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .selectableGroup(),
            horizontalAlignment = Alignment.Start
        ) {
            RadioButtonItem(selected = language.value==Language.English,
                            stringResource(R.string.english)
                        ){
                viewModel.setToEnglish(context)
            }
            RadioButtonItem(selected = language.value==Language.Arabic,
                            stringResource(R.string.arabic)
                        ){
                viewModel.setToArabic(context)
            }

        }
    }
}
@Composable
fun ThemeType(viewModel: MainViewModel) {
    var themeType =viewModel.themeType.observeAsState()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
    ) {
        Text(text = stringResource(R.string.change_theme), fontSize = 16.sp)
        Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.primaryVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .selectableGroup(),
            horizontalAlignment = Alignment.Start
        ) {
            RadioButtonItem(selected = themeType.value==ThemeType.Dark,
               stringResource(R.string.dark)
            ){
                viewModel.setToDark()
            }
            RadioButtonItem(selected = themeType.value==ThemeType.Light,
                stringResource(R.string.light)
            ){
                viewModel.setToLight()
            }

        }
    }
}

@Composable
fun RadioButtonItem(selected:Boolean,text:String,onClick:()->Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(40.dp)
            .padding(start = 8.dp, end = 8.dp)
            .selectable(
                selected = selected,
                onClick = { onClick() },
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(20.dp),
            selected = selected,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colors.secondary,
                unselectedColor = MaterialTheme.colors.onPrimary
            ),
            onClick = null
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = text, fontSize = 14.sp )
        
    }
}
