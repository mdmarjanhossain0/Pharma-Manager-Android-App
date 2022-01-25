package com.devscore.digital_pharmacy.presentation.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.GlobalMedicineDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

@HiltWorker
class GlobalWorker @AssistedInject
constructor(
    @Assisted
    val context: Context,
    @Assisted
    parameters: WorkerParameters,
    val globalMedicineDao: GlobalMedicineDao
) : Worker(context, parameters) {

    val TAG = "PdfWorker"

    companion object {
        const val Progress = "progress"
        private const val delayDuration = 1L
    }

    override fun doWork(): Result {
        insertData()
        return Result.success()
    }

    private fun insertData() {
        Log.d("AppDebug", "Load Json " + loadJSONFromAsset().toString())
    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = context.getAssets().open("global.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}