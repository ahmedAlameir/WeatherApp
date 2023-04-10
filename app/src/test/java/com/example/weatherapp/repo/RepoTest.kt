package com.example.weatherapp.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.TestCoroutineRule
import com.example.weatherapp.datasource.RepositoryImpl
import com.example.weatherapp.datasource.database.AlertDao
import com.example.weatherapp.datasource.database.WeatherDao
import com.example.weatherapp.datasource.network.ApiService
import com.example.weatherapp.model.AlertModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class RepoTest  {
private lateinit var api: ApiService
private lateinit var dao: WeatherDao
private lateinit var alertDao: AlertDao
private lateinit var repository: RepositoryImpl

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule= TestCoroutineRule()
    @Before
    fun setUp() {
        api = FakeApi()
        dao = FakeWeatherDao()
        alertDao = FakeAlertDao()
        repository = RepositoryImpl(api, dao, alertDao)
    }





    @Test
    fun `getAlarts returns alertDao response`() = runTest {
        val mockAlert = AlertModel("a")

        repository.insertAlarts(mockAlert)

        val mockList = listOf(mockAlert)

        val result= repository.getAlarts().toList()

        assertEquals(1, result.size)
        assertEquals(mockList, mockList)

    }

    @Test
    fun `getAlart returns alertDao response`() = runTest {
        val mockAlert = AlertModel("test")

        alertDao.insertAlert(mockAlert)
        val result = repository.getAlart("test")

        assertEquals(mockAlert, result)
    }



    @Test
    fun `insertAlarts calls alertDao`() = runTest {
        val mockAlert = AlertModel("test")

        repository.insertAlarts(mockAlert)

        assertEquals(mockAlert, alertDao.getAlert("test"))

    }
}