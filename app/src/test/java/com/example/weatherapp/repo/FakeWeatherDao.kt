package com.example.weatherapp.repo

import com.example.weatherapp.datasource.database.WeatherDao
import com.example.weatherapp.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow

class FakeWeatherDao:WeatherDao {
    override fun getAll(): Flow<List<FavouriteLocation>> {
        TODO("Not yet implemented")
    }

    override fun insert(users: FavouriteLocation) {
        TODO("Not yet implemented")
    }

    override fun delete(user: FavouriteLocation) {
        TODO("Not yet implemented")
    }
}