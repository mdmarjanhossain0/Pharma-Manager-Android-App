package com.appbytes.pharma_manager.presentation

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.appbytes.pharma_manager.presentation.util.GlobalWorker
import com.appbytes.pharma_manager.presentation.util.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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

        val constraints = Constraints.Builder().setRequiresCharging(false).build()

        val pdfWorker : WorkRequest =
            OneTimeWorkRequestBuilder<GlobalWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(applicationContext)
            .enqueue(pdfWorker)

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