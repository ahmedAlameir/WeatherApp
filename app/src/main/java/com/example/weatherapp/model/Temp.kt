package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Temp (

    var day:Double? = null,
    var min:Double? = null,
    var max:Double? = null,
    var night:Double? = null,
    var eve:Double? = null,
    var morn:Double? = null

)