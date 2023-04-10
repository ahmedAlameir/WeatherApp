package com.example.weatherapp.repo

import com.example.weatherapp.datasource.database.AlertDao
import com.example.weatherapp.model.AlertModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAlertDao:AlertDao {
    var alertModels = mutableListOf(AlertModel("a"))
    override fun getAlerts(): Flow<List<AlertModel>> {
        return flow {emit(alertModels)}
    }

    override suspend fun insertAlert(alert: AlertModel) {
        alertModels.add(alert)
    }

    override suspend fun deleteAlert(alert: AlertModel) {
        alertModels.remove(alert)
    }

    override suspend fun getAlert(name: String): AlertModel {
       return alertModels.find { it.name==name }!!
    }
}