package com.example.weatherapp.datasource.network

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.datasource.network.errorhandling.ResultCallAdapterFactory
import com.example.weatherapp.model.WeatherRespond
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/data/2.5/onecall")
    suspend fun weatherData(@Query("lat") lat:Double,
                           @Query("lon") lon:Double,
                           @Query("units") units:String,
                           @Query("lang") lang:String,
                           @Query("appid") apiKey:String=BuildConfig.WEATHER_API_KEY):WeatherRespond
}
const val BASE_URL="https://api.openweathermap.org"
private val retrofit = Retrofit
    .Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

object API {
    val retrofitService : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
