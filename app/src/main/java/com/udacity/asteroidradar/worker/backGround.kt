package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.local_data.getDatabase
import com.udacity.asteroidradar.repo.Repo
import retrofit2.HttpException

class BackGround(context: Context ,params : WorkerParameters ) : CoroutineWorker(context,params)   {
    companion object {
        const val Work_name = "RefreshDataWork"
    }
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repo = Repo(database)
       return try{
            repo.refreshAsteroids()
            Result.success()
        }catch (e : HttpException){
            Result.retry()
        }
    }
}