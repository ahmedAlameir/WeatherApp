package com.example.weatherapp.ui.screen

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.AlertModel
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R
import com.example.weatherapp.navigation.Screens
import java.util.*

fun unixToLocalHour(timeStamp: Long): String? {
    val time = Date(timeStamp * 1000)
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    return sdf.format(time)

}
@Composable
fun Alerts(viewModel: MainViewModel, navController: NavHostController) {
    viewModel.getAlerts()
    val alertModel= viewModel.alertModels.collectAsState().value
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn{
            items(alertModel){
                AlertItem(it){
                    viewModel.deleteAlert(it)
                }
                Spacer(modifier = Modifier.size(16.dp))

            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 112.dp),
            onClick = {
                navController.navigate(Screens.Alert.route)
            },

            shape = CircleShape,

            backgroundColor = MaterialTheme.colors.onPrimary,

            contentColor = MaterialTheme.colors.primary,
        ) {
            // on below line we are
            // adding icon for fab.
            Icon(Icons.Filled.Add, "")
        }
    }
}

@Composable
fun AlertItem(alertModel: AlertModel,onSwitch:()->Unit) {
    var switchOn by remember {
        mutableStateOf(true)
    }
    Row(
        modifier = Modifier
            .height(
                110.dp
            )
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colors.primaryVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column {
            Text(
                text = if (alertModel.isAlarm) stringResource(id = R.string.alarm)
                else {
                    stringResource(id = R.string.notification)
                },
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp,top=8.dp)
            )
            Text(
                text = alertModel.name,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text ="${unixToLocalHour(alertModel.startTime!!)}-${unixToLocalHour(alertModel.endTime!!)}",
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp,)
            )
        }
        Switch(
            checked = switchOn,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colors.secondary,
                uncheckedTrackColor = MaterialTheme.colors.primary,
                ),
            onCheckedChange = {
                onSwitch()
            }
        )

    }
}
