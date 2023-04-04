package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.datasource.Repository
import com.example.weatherapp.model.WeatherRespond
import kotlinx.coroutines.launch

class MainViewModel(private val repo: Repository): ViewModel()  {
    private var _weatherData=MutableLiveData<WeatherRespond>()
    val weatherData:LiveData<WeatherRespond>
    get()=_weatherData
    private var _locationMethod=MutableLiveData<LocationMethod>()
    val locationMethod:LiveData<LocationMethod>
    get()=_locationMethod
    private var _respondStat=MutableLiveData<RespondStat>()
    val respondStat:LiveData<RespondStat>
    get()=_respondStat

    private var location:Pair<Double,Double> = Pair(0.0,0.0)

    init {

        _respondStat.value=RespondStat.LOADING
    }
    fun setToGPS(){
        _locationMethod.value=LocationMethod.GPS
    }
    fun setToMap(){
        _locationMethod.value=LocationMethod.MAP
    }
    fun setLocation(lat:Double,long:Double){
        location= Pair(lat,long)
        getWeatherData()
    }

    private fun getWeatherData(){
        viewModelScope.launch{
            repo.weatherData(location.first,location.second).onSuccess{
                _weatherData.value=it
                Log.i("TAG", "getWeatherData: ////////////////")
                _respondStat.value=RespondStat.SUCCESS
            }.onFailure {
                _respondStat.value=RespondStat.ERROR
                Log.i("TAG", "getWeatherData: ${it.message}")

            }
        }
    }
}
enum class RespondStat{
    LOADING, ERROR, SUCCESS

}
enum class LocationMethod{
    GPS, MAP
}