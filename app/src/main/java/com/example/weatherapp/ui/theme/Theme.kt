package com.example.weatherapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple700,
    primaryVariant = Purple500,
    secondary = Yellow200,
    secondaryVariant= Yellow500,
    background = Purple600,
    surface = Purple200,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorPalette = lightColors(
    primary = White100,
    primaryVariant = White200,
    secondary = Yellow200,
    secondaryVariant= Yellow500,
    background = Cyan,
    surface = Purple200,
    onPrimary = Blue,
    onSecondary = Blue,
    onBackground = Blue,
    onSurface = Blue

)

@Composable
fun WeatherAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}