package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert")
data class AlertModel(
    @PrimaryKey val name: String,

    var startTime: Long?=null,
    var endTime: Long?=null,

    val isAlarm:Boolean=false
)