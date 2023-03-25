package com.example.weatherapp.datasource

import com.example.weatherapp.datasource.network.ApiService
import com.example.weatherapp.model.WeatherRespond

class Repository(private val api: ApiService) {
    suspend fun weatherData(lat:Double,lon:Double,units:String="",lang:String=""):Result<WeatherRespond>{
        return api.weatherData(lat,lon,units,lang)
    }
}