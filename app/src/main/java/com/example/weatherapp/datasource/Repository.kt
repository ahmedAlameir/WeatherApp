package com.example.weatherapp.datasource

import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.FavouriteLocation
import com.example.weatherapp.model.WeatherRespond
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun weatherData(lat:Double,lon:Double,units:String="",lang:String=""):Flow<WeatherRespond>
    suspend fun getFave(): Flow<List<FavouriteLocation>>
    suspend fun deleteFave(favouriteLocation :FavouriteLocation)

    suspend fun insertFave(favouriteLocation: FavouriteLocation)
    suspend fun getAlarts(): Flow<List<AlertModel>>
    suspend fun getAlart(name: String): AlertModel?
    suspend fun deleteAlarts(alertModel: AlertModel)
    suspend fun insertAlarts(alertModel: AlertModel)
}