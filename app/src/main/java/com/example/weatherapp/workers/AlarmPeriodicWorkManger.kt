package com.example.weatherapp.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.weatherapp.datasource.RepositoryImpl
import com.example.weatherapp.datasource.database.WeatherDataBase
import com.example.weatherapp.datasource.network.API
import com.example.weatherapp.model.AlertModel
import java.util.concurrent.TimeUnit

class AlarmPeriodicWorkManger (private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    private val weatherDatabase by lazy { WeatherDataBase.getDatabase(context).WeatherDao() }
    private val alertDatabase by lazy { WeatherDataBase.getDatabase(context).AlertDao() }


    private val repository = RepositoryImpl(API.retrofitService,weatherDatabase,alertDatabase)

    override suspend fun doWork(): Result {
        if (!isStopped) {
            val name = inputData.getString("name")?:""
            val lat = inputData.getFloat("lat", 91F)
            val lon = inputData.getFloat("lon", 181F)
            getData(name, lat.toDouble(), lon.toDouble())
        }
        return Result.success()
    }

    private suspend fun getData(name: String, lat: Double, lon: Double) {
        // request data from room or network

        val currentWeather = repository.weatherData(lat,lon)
        val alert = repository.getAlart(name)

        if (alert != null)
         {
            if (checkTimeLimit(alert)) {
                val delay: Long = getDelay(alert)
                currentWeather.collect{
                    if (it.alerts.isNullOrEmpty()) {
                        setOneTimeWorkManger(
                            delay,
                            alert.name,
                            it.current?.weather?.get(0)?.description ?: "",
                            alert.isAlarm
                        )
                    } else {
                        setOneTimeWorkManger(
                            delay,
                            alert.name,
                            it.alerts?.get(0)?.tags?.get(0) ?:"",
                            alert.isAlarm,
                        )
                    }
                }

            } else {
                repository.deleteAlarts(alert)
                WorkManager.getInstance().cancelAllWorkByTag( alert.name)
            }
        }
    }

    private fun setOneTimeWorkManger(delay: Long, id: String, description: String, alarm: Boolean) {
        val data = Data.Builder()
        data.putString("description", description)
        data.putBoolean("isAlarm",alarm)
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)

            .build()


        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
            AlarmOneTimeWorkManger::class.java,
        )
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        Log.i("TAG", "setOneTimeWorkManger: $delay")
        WorkManager.getInstance(context).enqueueUniqueWork(
            id,
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    }

    private fun getDelay(alert: AlertModel): Long {
        val unixTime = System.currentTimeMillis() / 1000L
        return (alert.startTime!!-7200) -unixTime
    }

    private fun checkTimeLimit(alert: AlertModel): Boolean {
        val unixTime = System.currentTimeMillis() / 1000L

        return          unixTime <= (alert.endTime!!-7200)
    }



}