package com.example.weatherapp.workers

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat.startForegroundService
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.alert.AlertService
import com.example.weatherapp.alert.MyNotificationService

class AlarmOneTimeWorkManger(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {



    override suspend fun doWork(): Result {
        val description = inputData.getString("description")!!
        if (inputData.getBoolean("isAlarm",false)) {

            startAlertService(description)
        }else{
            startNotificationService(description)
        }
        return Result.success()
    }

    private fun startAlertService(description: String) {
        val intent = Intent(applicationContext, AlertService::class.java)
        intent.putExtra("description", description)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(applicationContext, intent)
        } else {
            applicationContext.startService(intent)
        }

    }
    private fun startNotificationService(description: String) {
        val intent = Intent(applicationContext, MyNotificationService::class.java)
        intent.putExtra("description", description)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(applicationContext, intent)
        } else {
            applicationContext.startService(intent)
        }
    }

}
