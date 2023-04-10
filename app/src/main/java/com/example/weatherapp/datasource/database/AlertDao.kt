package com.example.weatherapp.datasource.database


import androidx.room.*
import com.example.weatherapp.model.AlertModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query("SELECT * FROM alert")
    fun getAlerts(): Flow<List<AlertModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertModel)

    @Delete
    suspend fun deleteAlert(alert: AlertModel)

    @Query("SELECT * FROM alert WHERE name = :name")
    suspend fun getAlert(name: String):AlertModel


}