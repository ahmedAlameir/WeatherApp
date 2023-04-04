package com.example.weatherapp.model

data class Current (
    var dt : Int? = null,
    var sunrise : Int? = null,
    var sunset : Int? = null,
    var temp : Double? = null,
    var feelsLike : Double? = null,
    var pressure : Int? = null,
    var humidity : Int? = null,
    var dewPoint : Double? = null,
    var uvi : Double? = null,
    var clouds : Int? = null,
    var visibility : Int? = null,
    var windSpeed : Double? = null,
    var windDeg : Int? = null,
    var windGust : Double? = null,
    var weather : ArrayList<Weather> = arrayListOf()
)