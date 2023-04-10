package com.example.weatherapp.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.MaterialTheme

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.work.*
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.workers.AlarmPeriodicWorkManger

import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Alert(viewModel: MainViewModel,navController: NavController){
    val inputValue = remember { mutableStateOf(TextFieldValue()) }
    var pickedDateStart by remember {
        mutableStateOf(LocalDate.now())
    }
    var pickedTimeStart by remember {
        mutableStateOf(LocalTime.NOON)
    }
    var pickedDateEnd by remember {
        mutableStateOf(LocalDate.now())
    }
    var pickedTimeEnd by remember {
        mutableStateOf(LocalTime.NOON)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isAlarm by remember {
        mutableStateOf(false)
    }

    Column(
        modifier =Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TextField(
                value = inputValue.value,
                onValueChange = {
                    inputValue.value = it
                },
                modifier = Modifier
                    .height(88.dp)
                    .padding(16.dp)
                    .fillMaxWidth(),
                maxLines = 1
            )
            DateItem(
                title = stringResource(R.string.start),
                onChoseDate = {
                    pickedDateStart = it
                },
                onChoseTime = {
                    pickedTimeStart = it
                })
            Spacer(modifier = Modifier.size(16.dp))
            DateItem(
                title = stringResource(R.string.end),
                onChoseDate = {
                    pickedDateEnd = it
                },
                onChoseTime = {
                    pickedTimeEnd = it
                })
            Text(
                text = stringResource(R.string.type),
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colors.primaryVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .selectableGroup()

            ) {
                RadioButtonItem(selected = !isAlarm, stringResource(R.string.notification)) {
                    isAlarm = !isAlarm
                }
                RadioButtonItem(selected = isAlarm, stringResource(R.string.alarm)) {
                    checkPermissionOfOverlay(context)
                    isAlarm = !isAlarm
                }

            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp))
        {
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        navController.popBackStack()

                    }
            ) {
                Text(text = stringResource(R.string.cancel),Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.size(24.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colors.secondaryVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        val stringBuilder = StringBuilder()
                        var error = false
                        if (inputValue.value.text.isEmpty()) {
                            stringBuilder.append("no name entered ")
                            error = true
                        }
                        if (pickedDateStart > pickedDateEnd ||
                            (pickedTimeStart > pickedTimeEnd && pickedDateStart == pickedDateEnd)
                        ) {
                            stringBuilder.append("End date is Bigger than start date")
                            error = true
                        }
                        if (error) {
                            Toast
                                .makeText(
                                    context, "$stringBuilder ", Toast.LENGTH_LONG
                                )
                                .show()
                        } else {

                            val alertModel = AlertModel(
                                name = inputValue.value.text,
                                startTime = pickedTimeStart.toSecondOfDay().toLong()+pickedDateStart.toEpochDay()*86400L,
                                endTime =pickedTimeEnd.toSecondOfDay().toLong()+pickedDateEnd.toEpochDay()*86400L,
                                isAlarm = isAlarm
                            )
                            val lat = viewModel.location.value?.first
                            val lon = viewModel.location.value?.second
                            setPeriodWorkManger(alertModel.name, context = context,lat,lon)
                            viewModel.insertAlert(alertModel)
                            navController.popBackStack()

                        }




                    }
            ) {
                Text(text = stringResource(R.string.save),Modifier.align(Alignment.Center))
            }
        }

    }
}
@Composable
fun DateItem(

    onChoseDate: (LocalDate) -> Unit,
    onChoseTime: (LocalTime) -> Unit,
    title: String
) {
    var pickedDateText by remember {
    mutableStateOf(LocalDate.now())
}
    var pickedTimeText by remember {
        mutableStateOf(LocalTime.now())
    }

    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("EE dd MMM")
                .format(pickedDateText)
        }
    }
    val formattedTime by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern(" hh:mm a")
                .format(pickedTimeText)
        }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    Row(
        modifier= Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .height(56.dp)
            .background(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                dateDialogState.show()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Text(text = title,Modifier.padding(start = 16.dp))

        Row(Modifier.padding(end = 16.dp)) {
            Text(text = formattedDate)
            Text(text = formattedTime)
        }
    }
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                onChoseDate(pickedDateText)

                timeDialogState.show()
            }
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Pick a date",
            allowedDateValidator = {

                it>LocalDate.now()
            }
        ) {
            pickedDateText=it
        }
    }
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                onChoseTime(pickedTimeText)
            }
            negativeButton(text = "Cancel")
        }
    ) {
        timepicker(
            initialTime = LocalTime.now(),
            title = "Pick a time"
        ) {
            pickedTimeText=it

        }
    }
}
private fun checkPermissionOfOverlay(context: Context) {

    if (!Settings.canDrawOverlays(context)) {

        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + context.applicationContext.packageName)
        )
        context.startActivity(intent)


    }
}
private fun setPeriodWorkManger(name: String, context: Context, lat: Double?, lon: Double?) {

    val data = Data.Builder()
    data.putString("name", name)
    data.putFloat("lat",lat?.toFloat()!!)
    data.putFloat("lon",lon?.toFloat()!!)

    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()

    val periodicWorkRequest = PeriodicWorkRequest.Builder(
       AlarmPeriodicWorkManger::class.java,
        24, TimeUnit.HOURS
    )
        .setConstraints(constraints)
        .setInputData(data.build())
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        name,
        ExistingPeriodicWorkPolicy.REPLACE,
        periodicWorkRequest
    )
}