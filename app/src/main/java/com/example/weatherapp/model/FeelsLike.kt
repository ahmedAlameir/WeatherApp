package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName


data class FeelsLike (

    var day   : Double? = null,
    var night : Double? = null,
    var eve   : Double? = null,
    var morn  : Double? = null

)