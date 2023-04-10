package com.example.weatherapp.repo

import com.example.weatherapp.datasource.network.ApiService
import com.example.weatherapp.model.WeatherRespond

class FakeApi : ApiService {
    override suspend fun weatherData(
        lat: Double,
        lon: Double,
        units: String,
        lang: String,
        apiKey: String
    ): WeatherRespond {
        return WeatherRespond()
    }

}