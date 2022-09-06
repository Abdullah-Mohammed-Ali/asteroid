package com.udacity.asteroidradar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.udacity.asteroidradar.worker.BackGround
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        delayedInit()
    }

    private fun delayedInit () {
        MainScope().launch {
            backGroundSetup()
        }
    }

    private fun backGroundSetup(){
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.METERED).setRequiresCharging(true).build()
        val request = PeriodicWorkRequestBuilder<BackGround>(1,TimeUnit.DAYS).setConstraints(constraints).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            BackGround.Work_name,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
