package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class WeatherRespond(var lat: Int?= null,var lon:Int? = null,var timezone:String? = null,
                   @SerializedName("timezone_offset" )var timezoneOffset:Int? = null,
                   var current:Current? = Current(),var hourly:ArrayList<Hourly> = arrayListOf(),
                   var daily:ArrayList<Daily> = arrayListOf()
)
