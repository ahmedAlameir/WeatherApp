package com.example.weatherapp

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.datasource.Repository
import com.example.weatherapp.model.WeatherRespond
import com.example.weatherapp.types_enums.Language
import com.example.weatherapp.types_enums.LocationMethod
import com.example.weatherapp.types_enums.TempTypes
import com.example.weatherapp.types_enums.WindTypes
import kotlinx.coroutines.launch

class MainViewModel(private val repo: Repository): ViewModel()  {
    private var _weatherData=MutableLiveData<WeatherRespond>()
    val weatherData:LiveData<WeatherRespond>
    get()=_weatherData

    private var _locationMethod=MutableLiveData<LocationMethod>()
    val locationMethod:LiveData<LocationMethod>
    get()=_locationMethod

    private var _respondStat=MutableLiveData(RespondStat.LOADING)
    val respondStat:LiveData<RespondStat>
    get()=_respondStat

    private var _tempType=MutableLiveData(TempTypes.Celsius)
    val tempType:LiveData<TempTypes>
    get()=_tempType

    private var _city=MutableLiveData("")
    val city:LiveData<String>
    get()=_city

    private var _language=MutableLiveData(Language.English)
    val language:LiveData<Language>
    get()=_language

    private var _windType=MutableLiveData<WindTypes>(WindTypes.MeterSec)
    val windType:LiveData<WindTypes>
        get()=_windType
    private var location:Pair<Double,Double> = Pair(0.0,0.0)


    fun setToGPS(){
        _locationMethod.value=LocationMethod.GPS
    }
    fun setToMap(){
        _locationMethod.value= LocationMethod.MAP
    }
    @Suppress("DEPRECATION")
    fun setLocation(lat:Double, long:Double, context:Context){
        val geocoder= Geocoder(context)
        geocoder.getFromLocation(lat,long,1,)?.let {
            for (location in it){
                _city.value = location.adminArea

            }
        }

        location= Pair(lat,long)
        getWeatherData()
    }
    fun setToFahrenheit(){
        _tempType.value =TempTypes.Fahrenheit
    }
    fun setToCelsius(){
        _tempType.value =TempTypes.Celsius
    }
    fun setToKelvin(){
        _tempType.value = TempTypes.Kelvin
    }
    fun setToMeterPerSec(){
        _windType.value =WindTypes.MeterSec
    }
    fun setToMilesPerHour(){
        _windType.value = WindTypes.MilesHour
    }
    fun setToArabic(){
        _language.value= Language.Arabic
    }
    fun setToEnglish(){
        _language.value= Language.English

    }

    private fun getWeatherData(){
        viewModelScope.launch{
            repo.weatherData(location.first,location.second).onSuccess{
                _weatherData.value=it
                _respondStat.value=RespondStat.SUCCESS
            }.onFailure {
                _respondStat.value=RespondStat.ERROR
            }
        }
    }
}
enum class RespondStat{
    LOADING, ERROR, SUCCESS

}
