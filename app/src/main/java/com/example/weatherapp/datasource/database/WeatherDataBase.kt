package com.example.weatherapp.datasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.FavouriteLocation
import com.example.weatherapp.ui.screen.Favourite


@Database(
    entities = [FavouriteLocation::class,AlertModel::class],
    version = 1
)
abstract class WeatherDataBase : RoomDatabase() {

    abstract fun WeatherDao(): WeatherDao
    abstract fun AlertDao(): AlertDao

    companion object {

        @Volatile
        private var INSTANCE: WeatherDataBase? = null

        fun getDatabase(context: Context): WeatherDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): WeatherDataBase {
            return Room.databaseBuilder(
                context.applicationContext,
                WeatherDataBase::class.java,
                "notes_database"
            ).build()
        }
    }
}