package com.example.weatherapp.ui.screen

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.Current
import com.example.weatherapp.model.Weather
import com.example.weatherapp.R
import com.example.weatherapp.model.Daily
import com.example.weatherapp.model.Hourly
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SimpleDateFormat")
fun unixToHour(timeStamp: Int) : String? {
    val time = Date(timeStamp.toLong()  * 1000)
    val sdf = SimpleDateFormat("hh a")
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    return sdf.format(time)

}
@SuppressLint("SimpleDateFormat")
fun unixToDay(timeStamp: Int) : String? {
    val time = Date(timeStamp.toLong()  * 1000)
    val sdf = SimpleDateFormat("E, MMM dd")
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    return sdf.format(time)

}
@Preview
@Composable
fun HomePrev(){

    Surface {

            Home()

    }
}
@Composable
fun Home() {
        LazyColumn {
            item {
                CurrentCard(Modifier.padding(start =  16.dp,end =  48.dp, top = 24.dp, bottom = 24.dp),
                    Current(weather = arrayListOf(Weather(main = "clear")))
                )
            }
            item {
                HourlyCard(arrayListOf(Hourly(dt=1646318698, weather = arrayListOf(Weather(main = "clear")))) ,
                    modifier =Modifier.padding(start =  16.dp,end =  16.dp, top = 16.dp))
            }
            item {
                DailyCard(arrayListOf(Daily(dt=1646318698, weather = arrayListOf(Weather(main = "clear")))))
            }

        }

}



@Composable
fun CurrentCard(modifier: Modifier=Modifier, current: Current){
    Box(modifier= modifier){
        Row(
            Modifier
                .fillMaxSize()
                .padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = current.temp.toString() + "°C", fontSize = 54.sp)
                Text(text = current.weather[0].main.toString(), fontSize = 12.sp)
                Text(text = "Real Feel " +current.feelsLike.toString(), fontSize = 10.sp)
            }
            when(current.weather[0].main){
                ("clear")-> Image(painter = painterResource(R.drawable.day_clear_sky ) ,
                    contentDescription = null,
                    modifier=Modifier.size(122.dp))

            }
        }
    }
}

@Composable
fun HourlyCard(hourly: ArrayList<Hourly>, modifier:Modifier=Modifier) {
    Box(modifier= modifier){
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 16.dp)) {
            Text(text =stringResource(R.string.today),fontSize=14.sp)
            LazyRow{
                items(hourly){
                    HourlyItem(it)
                }
            }

        }
    }
}

@Composable
fun HourlyItem(hourly: Hourly) {
    Card(
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(31 / 255f, 31 / 255f, 31 / 255f, 0.08f),
        contentColor = Color(31 / 255f, 31 / 255f, 31 / 255f, 0.08f)
    ) {
        Column(verticalArrangement = Arrangement.Center
            , horizontalAlignment = Alignment.CenterHorizontally){
            Text(text =  unixToHour(hourly.dt?:0)?:"12 AM",
            modifier = Modifier.padding(start = 8.dp, top = 8.dp,end=8.dp))
            when(hourly.weather[0].main){
                ("clear")-> Image(painter = painterResource(R.drawable.day_clear_sky ) ,
                    contentDescription = null,
                    modifier= Modifier
                        .size(24.dp)
                        .padding(top = 4.dp))
            }
            Text(text = hourly.temp.toString()+"°C",fontSize = 14.sp)
            Row(Modifier.padding(top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.group_13986),
                    contentDescription = null,Modifier.size(8.dp) )
                Text(text = ((hourly.pressure?:0)*100).toString()+"%")
                
            }
        }
    }
}
@Composable
fun DailyCard(daily: ArrayList<Daily>) {
    Column(modifier = Modifier.padding(start = 16.dp,end=16.dp, top=24.dp)) {
        Text(text = "Forecast", fontSize = 14.sp)
        Spacer(modifier = Modifier.padding(top=14.dp))
        var iterator = daily.iterator()
        var i = 0;
        while (iterator.hasNext()&&i<7){
            i++
            DailyItem(iterator.next())
        }
    }
}

@Composable
fun DailyItem(next: Daily) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically
        , modifier = Modifier
            .drawWithContent {
                drawContent()
                clipRect { // Not needed if you do not care about painting half stroke outside
                    val strokeWidth = Stroke.DefaultMiter
                    val y = size.height // - strokeWidth
                    // if the whole line should be inside component
                    drawLine(
                        brush = SolidColor(Color(1.0f, 1.0f, 1.0f, 0.15f)),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Square,
                        start = Offset.Zero.copy(y = y),
                        end = Offset(x = size.width, y = y)
                    )
                }
            }
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp) ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text =unixToDay(next.dt?:0)?:"Tue, Apr 04",
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.padding(start =  14.dp))
                Image(
                    painter = painterResource(id = R.drawable.group_13986),
                    contentDescription = null,Modifier.size(8.dp) )
                Text(text = ((next.pressure?:0)*100).toString()+"%")

            
        }
        Row() {
            when(next.weather[0].main){
                ("clear")-> Image(painter = painterResource(R.drawable.day_clear_sky ) ,
                    contentDescription = null,
                    modifier=Modifier.size(20.dp))

            }
            Spacer(modifier = Modifier.padding(start =  24.dp))

            Text(text = next.temp?.min.toString() + "°C", fontSize = 14.sp)
            Spacer(modifier = Modifier.padding(start =  16.dp))

            Text(text = next.temp?.max.toString() + "°C", fontSize = 14.sp)

        }
    }
}
@Composable
fun  DetailsItem( id:Int,value:String){
    Card(Modifier.fillMaxSize(),
        backgroundColor = Color(1.0f,1.0f,1.0f,0.06f),
        shape = RoundedCornerShape(8.dp)) {
            Column(Modifier.padding(start =  8.dp)) {
                Image(painter = painterResource(id = id)
                    , contentDescription = null,
                    modifier = Modifier.padding( top = 8.dp) )
                Spacer(modifier = Modifier.padding(start =  8.dp))
                Text(text = value, fontSize = 16.sp)
                Spacer(modifier = Modifier.padding(start =  16.dp))
                Text(text = value, fontSize = 14.sp)


            }
    }
}

