package com.devscore.digital_pharmacy.presentation

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.work.*
import com.devscore.digital_pharmacy.business.datasource.cache.AppDatabase
import com.devscore.digital_pharmacy.presentation.util.SyncWorker
import dagger.Provides
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider{

//    lateinit var database: AppDatabase

    val TAG = "BaseApplication"


    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()


    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

//        syncEngine()
    }

    private fun syncEngine() {
        Log.d(TAG, "application")

        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val syncWorkRequest : WorkRequest =
             PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                 .setConstraints(constraints)
                 .build()

         WorkManager
             .getInstance(this)
             .enqueue(syncWorkRequest)

    }




}