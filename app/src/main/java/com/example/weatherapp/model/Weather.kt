package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Weather(
   var id:Int?    = null,
   var main:String? = null,
   var description:String? = null,
   var icon:String? = null
)