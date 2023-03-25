package com.example.weatherapp

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
    private var _respondStat=MutableLiveData<RespondStat>()
    val respondStat:LiveData<RespondStat>
    get()=_respondStat
    init {
        _respondStat.value=RespondStat.LOADING
    }

    fun getWeatherData(){
        viewModelScope.launch{
            repo.weatherData(50.0,50.0).onSuccess{
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