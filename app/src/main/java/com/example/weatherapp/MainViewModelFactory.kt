package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.datasource.Repository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory (private val repo: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            MainViewModel(repo) as T
        }else{
            throw IllegalArgumentException("")
        }

    }
}