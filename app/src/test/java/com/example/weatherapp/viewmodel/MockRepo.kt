package com.example.weatherapp.viewmodel

import com.example.weatherapp.datasource.Repository
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.FavouriteLocation
import com.example.weatherapp.model.WeatherRespond
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepo:Repository {
    var favouriteLocations = mutableListOf(
        FavouriteLocation("Egypt",20.5,20.6),
        FavouriteLocation("London",42.5,83.6),
        FavouriteLocation("France",70.5,120.6),
        FavouriteLocation("australia",-50.5,-102.6))
    var alertModels = mutableListOf(
        AlertModel("alert"))
    override suspend fun weatherData(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherRespond> {
        TODO("Not yet implemented")
    }

    override suspend fun getFave(): Flow<List<FavouriteLocation>> {
        return flow { emit(favouriteLocations) }
    }

    override suspend fun deleteFave(favouriteLocation: FavouriteLocation) {
        favouriteLocations.remove(favouriteLocation)
    }

    override suspend fun insertFave(favouriteLocation: FavouriteLocation) {
        favouriteLocations.add(favouriteLocation)
    }

    override suspend fun getAlarts(): Flow<List<AlertModel>> {
        return flow { emit(alertModels) }
    }

    override suspend fun getAlart(name: String): AlertModel? {
        return alertModels.find { it.name==name }
    }

    override suspend fun deleteAlarts(alertModel: AlertModel) {
        alertModels.remove(alertModel)
    }

    override suspend fun insertAlarts(alertModel: AlertModel) {
        alertModels.add(alertModel)

    }
}