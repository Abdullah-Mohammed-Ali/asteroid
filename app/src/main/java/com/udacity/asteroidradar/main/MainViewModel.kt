package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.FilterAsteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApiService
import com.udacity.asteroidradar.local_data.getDatabase
import com.udacity.asteroidradar.repo.Repo
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = Repo(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay



    private val _navigateToDetailAsteroid = MutableLiveData<Asteroid>()
    val navigateToDetailAsteroid: LiveData<Asteroid>
        get() = _navigateToDetailAsteroid

    private var _filterAsteroid = MutableLiveData(FilterAsteroid.ALL)


    @RequiresApi(Build.VERSION_CODES.O)
    val asteroidList = Transformations.switchMap(_filterAsteroid) {
        when (it!!) {
            FilterAsteroid.WEEK -> asteroidRepository.weekAsteroids
            FilterAsteroid.TODAY -> asteroidRepository.todayAsteroids
            else -> asteroidRepository.asteriods
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        print(asteroid.id)
        _navigateToDetailAsteroid.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetailAsteroid.value = null
    }

    fun onChangeFilter(filter: FilterAsteroid) {
        _filterAsteroid.postValue(filter)
    }


    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            getDayPicture()
        }
    }


    private fun getDayPicture() {
        viewModelScope.launch {
            val image =
                AsteroidApiService.MyApiServiceObject.retrofitService.getPictureOfTheDay(com.udacity.asteroidradar.repo.apiKey)
            println("image url : " + image.url)
            _pictureOfDay.value = image
        }
    }

}