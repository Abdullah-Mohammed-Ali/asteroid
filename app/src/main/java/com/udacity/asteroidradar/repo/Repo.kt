package com.udacity.asteroidradar.repo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApiService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.local_data.AsteroidDatabase
import com.udacity.asteroidradar.local_data.toDataBase
import com.udacity.asteroidradar.local_data.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val apiKey = "kMwegeUKr4aoPNzblrrrcSr90PrpDeUGLEwAZC0R"

class Repo(private val databaseAsteroid: AsteroidDatabase) {


    @RequiresApi(Build.VERSION_CODES.O)
    private val startDate = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val endDate = LocalDateTime.now().plusDays(7)

    val asteriods: LiveData<List<Asteroid>> =
        Transformations.map(databaseAsteroid.asteroidDao.getAsteroids()) {
            it.toDomain()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val todayAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(databaseAsteroid.asteroidDao.getAsteroidsDay(startDate.format(DateTimeFormatter.ISO_DATE))) {
            it.toDomain()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val weekAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(
            databaseAsteroid.asteroidDao.getAsteroidsDate(
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE)
            )
        ) {
            println(                endDate.format(DateTimeFormatter.ISO_DATE))
            it.toDomain()
        }


    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroids =
                    AsteroidApiService.MyApiServiceObject.retrofitService.getAsteroids(apiKey)
                val result = parseAsteroidsJsonResult(JSONObject(asteroids))
                databaseAsteroid.asteroidDao.insertAll(*result.toDataBase())
                Log.d("Success insertation", "Success")
            } catch (err: Exception) {
                println("error ${err.message}")
            }
        }
    }
}