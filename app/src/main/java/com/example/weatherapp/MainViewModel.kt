package com.example.weatherapp

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.datasource.Repository
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.FavouriteLocation
import com.example.weatherapp.model.WeatherRespond
import com.example.weatherapp.types_enums.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(private val repo: Repository) : ViewModel() {
    private var _weatherData = MutableStateFlow<RespondState>(RespondState.Loading)
    val weatherData: StateFlow<RespondState>
        get() = _weatherData

    private var _weatherFavData = MutableStateFlow<RespondState>(RespondState.Loading)
    val weatherFavData: StateFlow<RespondState>
        get() = _weatherFavData

    private var _favList = MutableStateFlow<List<FavouriteLocation>>(emptyList())
    val favList :StateFlow<List<FavouriteLocation>>
        get() =_favList

    private var _locationMethod = MutableLiveData<LocationMethod>()
    val locationMethod: LiveData<LocationMethod>
        get() = _locationMethod


    private var _tempType = MutableLiveData(TempTypes.Celsius)
    val tempType: LiveData<TempTypes>
        get() = _tempType

    private var _themeType = MutableLiveData(ThemeType.Dark)
    val themeType: LiveData<ThemeType>
        get() = _themeType

    private var _city = MutableLiveData("")
    val city: LiveData<String>
        get() = _city

    private var _language = MutableLiveData(Language.English)
    val language: LiveData<Language>
        get() = _language

    private var _windType = MutableLiveData<WindTypes>(WindTypes.MeterSec)
    val windType: LiveData<WindTypes>
        get() = _windType
    private var _location = MutableLiveData<Pair<Double, Double>>()
    val location: LiveData<Pair<Double, Double>>
        get() = _location
    private val _alertModels = MutableStateFlow<List<AlertModel>>(emptyList())
    val alertModels: StateFlow<List<AlertModel>>
        get() = _alertModels
    private val _alertModel = MutableStateFlow<AlertModel>(AlertModel(""))
    val alertModel: StateFlow<AlertModel>
        get() = _alertModel

    fun setToGPS() {
        if( _locationMethod.value == LocationMethod.GPS)
        _locationMethod.value = LocationMethod.Gps2
        else
        _locationMethod.value = LocationMethod.GPS
    }

    fun setToMap() {
        _locationMethod.value = LocationMethod.MAP
    }

    @Suppress("DEPRECATION")
    fun setLocation(lat: Double, long: Double, context: Context) {
        val geocoder = Geocoder(context)
        geocoder.getFromLocation(lat, long, 1)?.let {
            for (location in it) {
                _city.value = location.adminArea
            }
        }
        _location.value = Pair(lat, long)
        getWeatherData()
    }
    @Suppress("DEPRECATION")
    fun setFavLocation(lat: Double, long: Double, context: Context) {
        val geocoder = Geocoder(context)
        geocoder.getFromLocation(lat, long, 1)?.let {
            for (location in it) {
                _city.value = location.adminArea
            }
        }
        _location.value = Pair(lat, long)
        getWeatherData()
    }

    fun setToDark(){
        _themeType.value =ThemeType.Dark
    }
    fun setToLight(){
        _themeType.value =ThemeType.Light
    }


    fun setToFahrenheit() {
        _tempType.value = TempTypes.Fahrenheit
    }

    fun setToCelsius() {
        _tempType.value = TempTypes.Celsius
    }

    fun setToKelvin() {
        _tempType.value = TempTypes.Kelvin
    }

    fun setToMeterPerSec() {
        _windType.value = WindTypes.MeterSec
    }

    fun setToMilesPerHour() {
        _windType.value = WindTypes.MilesHour
    }

    fun setToArabic(context: Context) {
        changeLanguage("ar", context = context)
        _language.value = Language.Arabic
    }

    private fun changeLanguage(language: String, context: Context) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale

        resources.updateConfiguration(configuration, resources.displayMetrics)

    }

    fun setToEnglish(context: Context) {
        changeLanguage("en", context)
        _language.value = Language.English

    }

    private fun getWeatherData() {
        val location = location.value
        if (location != null) {
            viewModelScope.launch {
                repo.weatherData(location.first, location.second)
                    .catch {
                        _weatherData.value = RespondState.Error(it)
                    }
                    .collect {
                        _weatherData.value = RespondState.Success(it)
                    }
            }
        } else {
            _weatherData.value = RespondState.Error(Throwable("the location is not set"))
        }

    }
    fun insertFav(lat: Double,long: Double,context:Context){
        val geocoder = Geocoder(context)
        geocoder.getFromLocation(lat, long, 1)?.let {
            for (location in it) {
                viewModelScope.launch(Dispatchers.IO) {
                    Log.i("TAG", "insertFav: "+Thread.currentThread().name)

                    repo.insertFave(FavouriteLocation(location.adminArea,lat,long))
                }
            }
        }

    }
    fun deleteFav(favouriteLocation: FavouriteLocation){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFave(favouriteLocation)
        }
    }
    fun getAllFav(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getFave()
                .collect{
                    _favList.value=it
                }
        }
    }
     fun getFavWeatherData(lat:Double,long: Double) {

            viewModelScope.launch {
                repo.weatherData(lat, long)
                    .catch {
                        _weatherFavData.value = RespondState.Error(it)
                    }
                    .collect {
                        _weatherFavData.value = RespondState.Success(it)
                    }
            }


    }
    fun getAlerts(){
        viewModelScope.launch {
            repo.getAlarts().collect{
                _alertModels.value = it
            }
        }
    }
    fun getAlert(name: String){
        viewModelScope.launch {
            _alertModel.value=repo.getAlart(name)?:AlertModel("")
        }
    }

    fun deleteAlert(alertModel: AlertModel) {
        viewModelScope.launch {
            repo.deleteAlarts(alertModel)
        }
    }

    fun insertAlert(alertModel: AlertModel) {
        viewModelScope.launch {
            repo.insertAlarts(alertModel)
        }
    }
}

sealed class RespondState {

    object Loading : RespondState()
    data class Success(val data: WeatherRespond) : RespondState()
    data class Error(val error: Throwable) : RespondState()
}
