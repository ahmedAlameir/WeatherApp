package com.example.weatherapp.datasource

import com.example.weatherapp.datasource.database.AlertDao
import com.example.weatherapp.datasource.database.WeatherDao
import com.example.weatherapp.datasource.network.ApiService
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryImpl(
    private val api: ApiService,
    private val dao: WeatherDao,
    private val alertDao: AlertDao
):Repository {
    override suspend fun weatherData(
        lat:Double,
        lon:Double,
        units:String,
        lang:String
    )= flow{

        emit(api.weatherData(lat,lon,units,lang))

    }
    override suspend fun getFave(): Flow<List<FavouriteLocation>> {
        return dao.getAll()
    }
    override suspend fun deleteFave(favouriteLocation: FavouriteLocation) {
        dao.delete(favouriteLocation)
    }
    override suspend fun insertFave(favouriteLocation: FavouriteLocation)
        { dao.insert(favouriteLocation) }
    override suspend fun getAlarts(): Flow<List<AlertModel>> {
        return alertDao.getAlerts()
    }
    override suspend fun getAlart(name:String): AlertModel? {
        return alertDao.getAlert(name)
    }
    override suspend fun deleteAlarts(alertModel: AlertModel) {
        alertDao.deleteAlert(alertModel)
    }
    override suspend fun insertAlarts(alertModel: AlertModel)
    {
        alertDao.insertAlert(alertModel)
    }
}