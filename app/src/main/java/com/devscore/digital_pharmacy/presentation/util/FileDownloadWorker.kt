package com.devscore.digital_pharmacy.presentation.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.appbytes.beautywallpaper.util.extentions.writeToFile
import com.devscore.digital_pharmacy.business.datasource.cache.account.AccountDao
import com.devscore.digital_pharmacy.business.datasource.cache.customer.CustomerDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineEntity
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineUnitEntity
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.PurchasesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.shortlist.ShortListDao
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.SupplierDao
import com.devscore.digital_pharmacy.business.datasource.datastore.AppDataStore
import com.devscore.digital_pharmacy.business.datasource.network.auth.AuthService
import com.devscore.digital_pharmacy.business.datasource.network.purchases.PurchasesOrderDto
import com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response.PurchasesOderItemDto
import com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response.toPurchasesOderMedicine
import com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response.toPurchasesOderMedicineEntity
import com.devscore.digital_pharmacy.business.datasource.network.purchases.toPurchasesOder
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesOrderDto
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.SalesOrderItemDto
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.toSalesOderEntity
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.toSalesOrderMedicine
import com.devscore.digital_pharmacy.business.datasource.network.sales.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

@HiltWorker
class FileDownloadWorker @AssistedInject
constructor(
    @Assisted
    context: Context,
    @Assisted
    parameters: WorkerParameters,
    val appDataStoreManager : AppDataStore,
    val accountDao: AccountDao,
    val service: AuthService,
    val localMedicineDao: LocalMedicineDao,
    val customerDao: CustomerDao,
    val supplierDao: SupplierDao,
    val shortListDao: ShortListDao,
    val salesDao : SalesDao,
    val purchasesDao: PurchasesDao
) : CoroutineWorker(context, parameters) {




    val TAG = "AppDebug"
    val DOWNLOAD_TIMEOUT_MS = 30_000L

    override suspend fun doWork(): Result {
        val url = inputData.getString("url")
        if (url != null) {
            try {
                insertMedicine(url)
                return Result.success()
            }
            catch (e : Exception) {
                e.printStackTrace()
                return Result.retry()
            }
        }
        else {
            return Result.failure()
        }
    }

    private suspend fun download(
        url: String
    ) : String {
        Log.d(TAG, "on start downloading")
            val path = applicationContext.filesDir?.absolutePath
            val file = File(path, "userdetails.json")
            val outpurStream = FileOutputStream(file)

            val responseBody = withTimeout(DOWNLOAD_TIMEOUT_MS) {
                service.downloadFile(url)
            }

            Log.d(TAG, "outputFile download onNext, " + "size=${responseBody.contentLength()}")

            responseBody.writeToFile(file!!.path) { p ->
                Log.d(TAG, "dao setting progress: $p")
            }
        return getJson(file)
    }







    fun getJson(file : File) : String {
        val stream = FileInputStream(file)
        var jString: String? = null
        jString = try {
            val fc: FileChannel = stream.getChannel()
            val bb: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
            /* Instead of using default, pass in a decoder. */
            Charset.defaultCharset()
                .decode(bb).toString()
        } finally {
            stream.close()
        }
        return jString!!
    }

    suspend fun insertMedicine(url: String) {
        val jsonObject = JSONObject(download(url))
        val localMedicines : JSONArray = jsonObject.getJSONArray("local_medicine")
        for (j in 0 until localMedicines.length()) {
            val medicineData = localMedicines.getJSONObject(j)

            val medicineUnits = medicineData.getJSONArray("units")
            val units = mutableListOf<MedicineUnits>()
            for (i in 0 until medicineUnits.length()) {
                val data = medicineUnits.getJSONObject(i)
                val medicineUnit = MedicineUnits (
                    id = data.getInt("id"),
                    quantity = data.getInt("quantity"),
                    name = data.getString("name"),
                    type = data.getString("type")
                )
                units.add(medicineUnit)
            }
            val localMedicine = LocalMedicine(
                id = medicineData.getInt("id"),
                brand_name = medicineData.getString("brand_name"),
                sku = medicineData.getString("brand_name"),
                dar_number = medicineData.getString("dar_number"),
                mr_number = medicineData.getString("mr_number"),
                generic = medicineData.getString("generic"),
                indication = medicineData.getString("indication"),
                symptom = medicineData.getString("symptom"),
                strength = medicineData.getString("brand_name"),
                description = medicineData.getString("brand_name"),
                image = medicineData.getString("brand_name"),
                mrp = medicineData.getString("mrp").toFloat(),
                purchase_price = medicineData.getString("purchase_price").toFloat(),
                discount = medicineData.getString("discount").toFloat(),
                is_percent_discount = medicineData.getBoolean("is_percent_discount"),
                manufacture = medicineData.getString("manufacture"),
                kind = medicineData.getString("kind"),
                form = medicineData.getString("form"),
                remaining_quantity = medicineData.getString("remaining_quantity").toFloat(),
                damage_quantity = medicineData.getString("damage_quantity").toFloat(),
                exp_date = medicineData.getString("exp_date"),
                rack_number = medicineData.getString("rack_number"),
                units = units
            )
            localMedicineDao.insertLocalMedicine(localMedicine.toLocalMedicineEntity())
            for (unit in localMedicine.toLocalMedicineUnitEntity()) {
                localMedicineDao.insertLocalMedicineUnit(unit)
            }
        }
        val customers : JSONArray = jsonObject.getJSONArray("customer")
        for (j in 0 until customers.length()) {
            val data = customers.getJSONObject(j)
            Log.d(TAG, data.toString())
            val customer = Customer (
                pk = data.getInt("id"),
                name = data.getString("name"),
                email = data.getString("email"),
                mobile = data.getString("mobile"),
                whatsapp = data.getString("whatsapp"),
                facebook = data.getString("facebook"),
                imo = data.getString("imo"),
                address = data.getString("address"),
                date_of_birth = "",
                loyalty_point = data.getInt("loyalty_point"),
                created_at = data.getString("created_at"),
                updated_at = data.getString("updated_at"),
                total_balance = data.getString("total_balance").toFloat(),
                due_balance = data.getString("due_balance").toFloat()
            )
            customerDao.insertCustomer(customer.toCustomerEntity())
        }

        val sales = jsonObject.getJSONArray("sales")
        for (i in 0 until sales.length()) {
            val sale : SalesOrderDto = Gson().fromJson(sales.getJSONObject(i).toString(), SalesOrderDto::class.java)
            salesDao.insertSalesOder(sale.toSalesOrder().toSalesOrderEntity())
            val salesOrderMedicinesJson = sales.getJSONObject(i).getJSONArray("sales_oder_medicines")
            for (j in 0 until salesOrderMedicinesJson.length()) {
                val salesOrderMedicine = Gson().fromJson(salesOrderMedicinesJson.getJSONObject(j).toString(), SalesOrderItemDto::class.java)
                salesDao.insertSaleOderMedicine(salesOrderMedicine.toSalesOrderMedicine().toSalesOderEntity(sale.pk))
            }
        }
        val purchases = jsonObject.getJSONArray("purchases")
        for (i in 0 until purchases.length()) {
            val purchase : PurchasesOrderDto = Gson().fromJson(purchases.getJSONObject(i).toString(), PurchasesOrderDto::class.java)
            purchasesDao.insertPurchasesOrder(purchase.toPurchasesOder().toPurchasesOrderEntity())
            val purchasesOderMedicineJson = purchases.getJSONObject(i).getJSONArray("purchases_order_medicines")
            for (j in 0 until purchasesOderMedicineJson.length()) {
                val purchasesOrderMedicine = Gson().fromJson(purchasesOderMedicineJson.getJSONObject(j).toString(), PurchasesOderItemDto::class.java)
                purchasesDao.insertPurchasesOrderMedicine(purchasesOrderMedicine.toPurchasesOderMedicine().toPurchasesOderMedicineEntity(purchase.pk))
            }
        }

        val suppliers : JSONArray = jsonObject.getJSONArray("vendor")
        for (j in 0 until suppliers.length()) {
            val data = suppliers.getJSONObject(j)
            val supplier = Supplier(
                pk = data.getInt("id"),
                company_name = data.getString("company_name"),
                agent_name = data.getString("agent_name"),
                email = data.getString("email"),
                mobile = data.getString("mobile"),
                whatsapp = data.getString("whatsapp"),
                facebook = data.getString("facebook"),
                imo = data.getString("imo"),
                address = data.getString("address"),
                created_at = data.getString("created_at"),
                updated_at = data.getString("updated_at"),
                total_balance = data.getString("total_balance").toFloat(),
                due_balance = data.getString("due_balance").toFloat()
            )


            supplierDao.insertSupplier(supplier.toSupplierEntity())
        }
    }
}