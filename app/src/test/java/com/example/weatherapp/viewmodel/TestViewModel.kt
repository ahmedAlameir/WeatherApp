package com.example.weatherapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.TestCoroutineRule
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.types_enums.LocationMethod
import com.example.weatherapp.types_enums.TempTypes
import com.example.weatherapp.types_enums.WindTypes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestViewModel {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule= TestCoroutineRule()

    private lateinit var  viewModel : MainViewModel
    private lateinit var  repo : FakeRepo

    @Before
    fun setUp(){
        repo= FakeRepo()
        viewModel= MainViewModel(repo)
    }

    @Test
    fun `setToFahrenheit updates tempType LiveData`() {
        // Arrange
        val expectedTempType = TempTypes.Fahrenheit

        // Act
        viewModel.setToFahrenheit()

        // Assert
        assertEquals(expectedTempType, viewModel.tempType.value)
    }



    @Test
    fun `setToGPS updates locationMethod LiveData`() {
        // Arrange
        val expectedLocationMethod = LocationMethod.GPS

        // Act
        viewModel.setToGPS()

        // Assert
        assertEquals(expectedLocationMethod, viewModel.locationMethod.value)
    }



    @Test
    fun `setToMilesPerHour updates windType LiveData`() {
        // Arrange
        val expectedWindType = WindTypes.MilesHour

        // Act
        viewModel.setToMilesPerHour()

        // Assert
        assertEquals(expectedWindType, viewModel.windType.value)
    }

    @Test
    fun `setToCelsius updates tempType LiveData`() {
        // Arrange
        val expectedTempType = TempTypes.Celsius

        // Act
        viewModel.setToCelsius()

        // Assert
        assertEquals(expectedTempType, viewModel.tempType.value)
    }
    @Test
    fun `getAlert should update _alertModel with the result of repo#getAlart`() = runBlocking{
        // Arrange
        val name = "test alert"
        val alertModel = AlertModel(name)
        repo.insertAlarts(alertModel)

        // Act
         viewModel.getAlert(name)

        // Assert

        assertEquals(alertModel, viewModel.alertModel.value)
    }



    @Test
    fun `deleteAlert should call repo#deleteAlarts with the provided alert model`()= runBlocking {
        // Arrange
        val alertModel = AlertModel("test alert2")
        repo.insertAlarts(alertModel)
        // Act
        viewModel.deleteAlert(alertModel)

        // Assert
        assertEquals(false, viewModel.alertModels.value.contains(alertModel))

    }

    @Test
    fun `insertAlert should call repo#insertAlarts with the provided alert model`()= runBlocking {
        // Arrange
        val alertModel = AlertModel("test alert")

        // Act
        viewModel.insertAlert(alertModel)

        // Assert
        assertEquals(true, repo.alertModels.contains(alertModel))

    }

}