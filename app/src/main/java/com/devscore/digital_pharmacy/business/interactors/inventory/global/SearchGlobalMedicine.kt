package com.devscore.digital_pharmacy.business.interactors.inventory.global

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.GlobalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.searchCacheGlobalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.toGlobalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.toGlobalMedicineEntity
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.inventory.searchGlobalMedicine
import com.devscore.digital_pharmacy.business.datasource.network.inventory.toGlobalMedicine
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
import com.devscore.digital_pharmacy.business.domain.util.DataState
import com.devscore.digital_pharmacy.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.devscore.digital_pharmacy.business.domain.util.MessageType
import com.devscore.digital_pharmacy.business.domain.util.Response
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchGlobalMedicine(
    private val service: InventoryApiService,
    private val cache: GlobalMedicineDao,
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        page: Int,
        action : String
    ): Flow<DataState<List<GlobalMedicine>>> = flow {
        emit(DataState.loading<List<GlobalMedicine>>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        val initialData = cache.searchCacheGlobalMedicine(
            query = query,
            page = page,
            ordering = "id",
            action = action
        ).map { it.toGlobalMedicine() }


        Log.d(TAG, "Cache Data " + initialData.size + " " + "Query " + query + initialData.toString())


        if (initialData.size > 0) {
            emit(DataState.data(response = null, data = initialData))
        }
//        emit(DataState.loading<List<GlobalMedicine>>())

        try{ // catch network exception
            Log.d(TAG, "Call Api Section")
            val medicines = service.searchGlobalMedicine(
                "Token ${authToken.token}",
                query = query,
                page = page,
                action = action
            ).results.map {
                it.toGlobalMedicine()
            }
            Log.d(TAG, "Network data " + medicines.size +" " + "Query " + query +medicines.toString())

            Log.d(TAG, "Success Api Call")

            // Insert into cache
            for(medicine in medicines){
                try{
                    Log.d(TAG, "Caching size" + medicines.size.toString())
                    cache.insert(medicine.toGlobalMedicineEntity())
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(
                DataState.error<List<GlobalMedicine>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        // emit from cache
        val cachedBlogs = cache.searchCacheGlobalMedicine(
            query = query,
            page = page,
            ordering = "id",
            action = action
        ).map { it.toGlobalMedicine() }


        Log.d(TAG, "Cache Data " + cachedBlogs.size + " " + "Query " + query + cachedBlogs.toString())

        emit(DataState.data(response = null, data = cachedBlogs))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}



















