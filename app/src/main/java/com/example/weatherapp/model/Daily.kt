package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Daily (
    var dt:Int? = null,
    var sunrise:Int? = null,
    var sunset:Int? = null,
    var moonrise:Int? = null,
    var moonset:Int? = null,
    @SerializedName("moon_phase" )var moonPhase:Double? = null,
    var temp:Temp? = Temp(),
    @SerializedName("feels_like" )var feelsLike:FeelsLike? = FeelsLike(),
    var pressure:Int? = null,
    var humidity:Int? = null,
    @SerializedName("dew_point"  ) var dewPoint:Double? = null,
    @SerializedName("wind_speed" ) var windSpeed:Double? = null,
    @SerializedName("wind_deg"   ) var windDeg:Int? = null,
    @SerializedName("wind_gust"  ) var windGust:Double? = null,
    var weather:ArrayList<Weather> = arrayListOf(),
    var clouds:Int? = null,
    var pop:Double? = null,
    var rain:Double? = null,
    var uvi:Double? = null
)