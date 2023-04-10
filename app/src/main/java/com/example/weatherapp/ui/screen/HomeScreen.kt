package com.example.weatherapp.ui.screen

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.*
import com.example.weatherapp.R
import com.example.weatherapp.model.Current
import com.example.weatherapp.model.Daily
import com.example.weatherapp.model.Hourly
import com.example.weatherapp.model.WeatherRespond
import com.example.weatherapp.types_enums.TempTypes
import com.example.weatherapp.types_enums.WindTypes
import com.example.weatherapp.ui.component.SnackbarWithoutScaffold
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SimpleDateFormat")
fun unixToHour(timeStamp: Int): String? {
    val time = Date(timeStamp.toLong() * 1000)
    val sdf = SimpleDateFormat("hh a")
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    return sdf.format(time)

}

@SuppressLint("SimpleDateFormat")
fun unixToDay(timeStamp: Int): String? {
    val time = Date(timeStamp.toLong() * 1000)
    val sdf = SimpleDateFormat("E, MMM dd")
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    return sdf.format(time)

}

@Composable
fun Home(viewModel: MainViewModel, state: ScaffoldState) {
    val weather = viewModel.weatherData.collectAsState().value

    when(weather){
        is RespondState.Loading -> {
            LoadingAnimation(MaterialTheme.colors.secondary)
        }

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
fun DataScreen(viewModel: MainViewModel,weather: WeatherRespond) {
    val wind = viewModel.windType.observeAsState()
    val temp = viewModel.tempType.observeAsState()
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        CurrentCard(
            Modifier.padding(start = 16.dp, end = 48.dp, top = 24.dp, bottom = 24.dp),
            weather.current!!, temp.value!!,
            weather.timezoneOffset
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(MaterialTheme.colors.primary)
        ) {
            HourlyCard(
                weather.hourly,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                temp.value!!,
                sunset = weather.current?.sunset,
                sunrise = weather.current?.sunrise,
                weather.timezoneOffset
            )

            DailyCard(
                weather.daily,
                temp.value!!
            )

            WeatherDetails(weather.current!!, wind.value)
        }
    }

}

@Composable
fun CurrentCard(
    modifier: Modifier = Modifier,
    current: Current,
    temp: TempTypes,
    timezoneOffset: Int?
) {
    Box(modifier = modifier) {
        Row(
            Modifier
                .fillMaxSize()
                , horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TempText(tempType = temp, temp = current.temp!!, size = 54)
                Text(text = current.weather[0].main.toString(), fontSize = 16.sp)
                Text(
                    text = unixToHour((current.dt ?: 0) + (timezoneOffset ?: 0)) ?: "12 AM",
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)
                )
            }
            WeatherCondition(
                weatherCondition = current.weather[0].id!!,
                size = 122,
                modifier = Modifier,
                current.sunset!! < current.dt!! && current.sunrise!! > current.dt!!
            )
        }
    }
}

@Composable
fun HourlyCard(
    hourly: ArrayList<Hourly>,
    modifier: Modifier = Modifier,
    temp: TempTypes,
    sunset: Int?,
    sunrise: Int?,
    timezoneOffset: Int?
) {
    Box(modifier = modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.today),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.size(16.dp))

            LazyRow {
                items(hourly) {
                    HourlyItem(it, temp, sunset, sunrise, timezoneOffset)
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }

        }
    }
}

@Composable
fun HourlyItem(hourly: Hourly, temp: TempTypes, sunset: Int?, sunrise: Int?, timezoneOffset: Int?) {
    Card(
        elevation = 0.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0x14FFFFFF)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = unixToHour((hourly.dt ?: 0) + (timezoneOffset ?: 0)) ?: "12 AM",
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)
            )

            WeatherCondition(
                weatherCondition = hourly.weather[0].id!!, size = 24,
                modifier = Modifier.padding(top = 4.dp),
                sunrise!! < hourly.dt!! && sunset!! > hourly.dt!!
            )
            TempText(tempType = temp, temp = hourly.temp!!, size = 14)

            Row(
                Modifier.padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.group_13986),
                    contentDescription = null, Modifier.size(8.dp)
                )
                Text(text = ((hourly.humidity ?: 0)).toString() + "%")

            }
        }
    }
}

@Composable
fun DailyCard(daily: ArrayList<Daily>, temp: TempTypes) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
        Text(text = stringResource(id = R.string.forecast), fontSize = 16.sp)
        Spacer(modifier = Modifier.padding(top = 14.dp))
        val iterator = daily.iterator()
        var i = 0;
        while (iterator.hasNext() && i < 7) {
            i++
            DailyItem(iterator.next(), temp)

        }
    }
}

@Composable
fun DailyItem(next: Daily, temp: TempTypes) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
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
            .padding(start = 16.dp, end = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = unixToDay(next.dt ?: 0) ?: "Tue, Apr 04",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
            Spacer(modifier = Modifier.padding(start = 14.dp))
            Image(
                painter = painterResource(id = R.drawable.group_13986),
                contentDescription = null, Modifier.size(8.dp)
            )
            Text(
                text = ((next.humidity ?: 0)).toString() + "%",
                fontSize = 12.sp
            )


        }
        Row() {

            WeatherConditionDay(
                weatherCondition = next.weather[0].id!!,
                size = 20,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.padding(start = 24.dp))
            TempText(tempType = temp, temp = next.temp?.min!!, size = 16)

            Spacer(modifier = Modifier.padding(start = 16.dp))
            TempText(tempType = temp, temp = next.temp?.max!!, size = 16)


        }
    }
}

@Composable
fun WeatherDetails(current: Current, wind: WindTypes?) {
    Column(Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Text(text = stringResource(id = R.string.details))
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                DetailsItem(
                    R.drawable.humidity,
                    "${current.humidity ?: 0} %", R.string.humidity, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Spacer(modifier = Modifier.size(16.dp))
                DetailsItem(
                    R.drawable.pressure,
                    "${current.pressure ?: 0} mb",
                    R.string.pressure,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Spacer(modifier = Modifier.size(16.dp))
                DetailsItem(
                    R.drawable.uv_rays, "${current.uvi ?: 0}", R.string.uv_rays, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.size(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                DetailsItem(
                    R.drawable.dew_point,
                    "${current.dewPoint ?: 0} °",
                    R.string.dew_point,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Spacer(modifier = Modifier.size(16.dp))
                if (wind != null) {
                    DetailsItemWind(
                        R.drawable.wind, current.windSpeed ?: 0.0,
                        R.string.wind,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        wind
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                DetailsItem(
                    R.drawable.clouds,
                    "${current.clouds ?: 0} %",
                    R.string.clouds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun DetailsItem(id: Int, value: String, type: Int, modifier: Modifier = Modifier) {
    Card(
        modifier, elevation = 0.dp,
        backgroundColor = Color(1.0f, 1.0f, 1.0f, 0.06f),
        border = null,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(start = 8.dp, bottom = 8.dp, top = 8.dp)) {
            Image(
                painter = painterResource(id = id), contentDescription = null,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = value, fontSize = 16.sp)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = stringResource(type), fontSize = 14.sp)


        }

    }
}

@Composable
fun DetailsItemWind(
    id: Int,
    value: Double,
    type: Int,
    modifier: Modifier = Modifier,
    windType: WindTypes
) {
    Card(
        modifier, elevation = 0.dp,
        backgroundColor = Color(1.0f, 1.0f, 1.0f, 0.06f),
        border = null,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(start = 8.dp, bottom = 8.dp, top = 8.dp)) {
            Image(
                painter = painterResource(id = id), contentDescription = null,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            WindText(windSpeed = value, size = 16, windType = windType)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = stringResource(type), fontSize = 14.sp)


        }

    }
}

@Composable
fun TempText(tempType: TempTypes, temp: Double, size: Int) {
    when (tempType) {
        TempTypes.Fahrenheit -> Text(
            text = String.format(
                "%.2f",
                ((temp - 273.15) * 9 / 5 + 32)
            ) + stringResource(R.string.f_degree), fontSize = size.sp
        )
        //String.format("%.2f",  (temp-273.15))
        TempTypes.Celsius -> Text(
            text = String.format("%.2f", (temp - 273.15)) + stringResource(R.string.c_degree),
            fontSize = size.sp
        )
        TempTypes.Kelvin -> Text(text = (temp).toString() + stringResource(R.string.kelvin_symbol), fontSize = size.sp)
    }
}

@Composable
fun WindText(windType: WindTypes, windSpeed: Double, size: Int) {
    when (windType) {
        WindTypes.MilesHour -> Text(
            text = String.format("%.2f", (windSpeed * 2.237)) + stringResource(R.string.mph),
            fontSize = size.sp
        )
        WindTypes.MeterSec -> Text(text = (windSpeed).toString() + stringResource(R.string.m_per_s), fontSize = size.sp)
    }
}

@Composable
fun WeatherCondition(
    weatherCondition: Int,
    size: Int,
    modifier: Modifier,
    isDay: Boolean
) {
    if (isDay) {
        WeatherConditionDay(weatherCondition, size, modifier)
    } else {
        WeatherConditionNight(weatherCondition, size, modifier)
    }
}

@Composable
fun WeatherConditionDay(weatherCondition: Int, size: Int, modifier: Modifier) {
    when (weatherCondition) {
        in 200..232 -> Image(
            painter = painterResource(R.drawable.thunderstorm),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        in 300..321 -> Image(
            painter = painterResource(R.drawable.day_rain),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        in 500..531 -> Image(
            painter = painterResource(R.drawable.day_shower_rain),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        in 600..622 -> Image(
            painter = painterResource(R.drawable.day_snow),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        in 701..781 -> Image(
            painter = painterResource(R.drawable.day_snow),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        800 -> Image(
            painter = painterResource(R.drawable.day_clear_sky),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        801 -> Image(
            painter = painterResource(R.drawable.day_few_clouds),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        802 -> Image(
            painter = painterResource(R.drawable.day_scattered_clouds),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        803, 804 -> Image(
            painter = painterResource(R.drawable.day_broken_clouds),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )

    }

}

@Composable
fun WeatherConditionNight(weatherCondition: Int, size: Int, modifier: Modifier) {
    when (weatherCondition) {
        in 200..232 -> Image(
            painter = painterResource(R.drawable.thunderstorm),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        in 300..321 -> Image(
            painter = painterResource(R.drawable.night_rain),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        in 500..531 -> Image(
            painter = painterResource(R.drawable.day_shower_rain),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        in 600..622 -> Image(
            painter = painterResource(R.drawable.night_snow),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        in 701..781 -> Image(
            painter = painterResource(R.drawable.day_snow),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        800 -> Image(
            painter = painterResource(R.drawable.night_clear_sky),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        801 -> Image(
            painter = painterResource(R.drawable.night_few_clouds),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        802 -> Image(
            painter = painterResource(R.drawable.night_scattered_clouds),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
        803, 804 -> Image(
            painter = painterResource(R.drawable.day_broken_clouds),
            contentDescription = null,
            modifier = modifier.size(size.dp)
        )
    }
}
@Composable
fun LoadingAnimation(
    circleColor: Color = Color.Magenta,
    animationDelay: Int = 1000
) {

    // circle's scale state
    var circleScale by remember {
        mutableStateOf(0f)
    }

    // animation
    val circleScaleAnimate = animateFloatAsState(
        targetValue = circleScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDelay
            )
        )
    )

    // This is called when the app is launched
    LaunchedEffect(Unit) {
        circleScale = 1f
    }

    // animating circle
    Box(
        modifier = Modifier
            .size(size = 64.dp)
            .scale(scale = circleScaleAnimate.value)
            .border(
                width = 4.dp,
                color = circleColor.copy(alpha = 1 - circleScaleAnimate.value),
                shape = CircleShape
            )
    ) {

    }
}