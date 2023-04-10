package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Favourite")
data class FavouriteLocation(@PrimaryKey  var name:String, var lat:Double, var lon:Double)