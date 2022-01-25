package com.devscore.digital_pharmacy.presentation.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.GlobalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.toGlobalMedicineEntity
import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import org.json.JSONArray

import org.json.JSONObject




@HiltWorker
class GlobalWorker @AssistedInject
constructor(
    @Assisted
    val context: Context,
    @Assisted
    parameters: WorkerParameters,
    val globalMedicineDao: GlobalMedicineDao
) : CoroutineWorker(context, parameters) {

    val TAG = "PdfWorker"

    companion object {
        const val Progress = "progress"
        private const val delayDuration = 1L
    }

    override suspend fun doWork(): Result {
        insertData()
        return Result.success()
    }

    private suspend fun insertData() {
        val count = globalMedicineDao.getRowCount()
        Log.d(TAG, "Count " + count.toString())
        if (count != null) {
            if (count < 44100) {
                insertMedicine()
            }
        }
        else {
            insertMedicine()
        }
    }

    suspend fun insertMedicine() {
        val jsonObject = JSONObject(loadJSONFromAsset())
        val results: JSONArray = jsonObject.getJSONArray("results")
        for (j in 0 until results.length()) {
            val data = results.getJSONObject(j)
            val brand_name = data.getString("brand_name")
            val generic = data.getString("generic")
            Log.d(TAG, j.toString())
            Log.d(TAG, brand_name)
            Log.d(TAG, generic)
            val medicine = GlobalMedicine (
                id = data.getInt("id"),
                brand_name = data.getString("brand_name"),
                sku = data.getString("brand_name"),
                darNumber = data.getString("dar_number"),
                mrNumber = data.getString("mr_number"),
                generic = data.getString("generic"),
                indication = data.getString("indication"),
                symptom = data.getString("symptom"),
                strength = data.getString("strength"),
                description = data.getString("description"),
                mrp = data.getString("mrp").toFloat(),
                purchases_price = data.getString("purchase_price").toFloat(),
                manufacture = data.getString("manufacture"),
                kind = data.getString("kind"),
                form = data.getString("form"),
                createdAt = data.getString("created_at"),
                updatedAt = data.getString("updated_at")
            )
            globalMedicineDao.insert(medicine.toGlobalMedicineEntity())
        }
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