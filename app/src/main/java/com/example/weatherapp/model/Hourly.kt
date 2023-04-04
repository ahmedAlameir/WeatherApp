package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Hourly (
    var dt:Int? = null,
    var temp:Double? = null,
    @SerializedName("feels_like" ) var feelsLike  : Double?            = null,
    var pressure:Int? = null,
    var humidity:Int? = null,
    @SerializedName("dew_point"  ) var dewPoint:Double? = null,
    var uvi:Double? = null,
    var clouds:Int? = null,
    var visibility:Int? = null,
    @SerializedName("wind_speed" ) var windSpeed:Double? = null,
    @SerializedName("wind_deg"   ) var windDeg:Int? = null,
    @SerializedName("wind_gust"  ) var windGust:Double? = null,
     var weather:ArrayList<Weather> = arrayListOf(),
     var pop:Double? = null

)