package com.example.weatherapp.datasource.database

import androidx.room.*
import com.example.weatherapp.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM Favourite")
    fun getAll(): Flow<List<FavouriteLocation>>

    @Upsert
    fun insert(users: FavouriteLocation)

    @Delete
    fun delete(user: FavouriteLocation)
}