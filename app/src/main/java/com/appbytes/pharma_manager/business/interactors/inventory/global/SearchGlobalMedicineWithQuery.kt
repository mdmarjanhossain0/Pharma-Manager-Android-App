package com.appbytes.pharma_manager.business.interactors.inventory.global

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.inventory.global.GlobalMedicineDao
import com.appbytes.pharma_manager.business.datasource.cache.inventory.global.toGlobalMedicine
import com.appbytes.pharma_manager.business.datasource.cache.inventory.global.toGlobalMedicineEntity
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.inventory.InventoryApiService
import com.appbytes.pharma_manager.business.datasource.network.inventory.toGlobalMedicine
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.GlobalMedicine
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchGlobalMedicineWithQuery(
    private val service: InventoryApiService,
    private val cache: GlobalMedicineDao,
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        page: Int
    ): Flow<DataState<List<GlobalMedicine>>> = flow {
        emit(DataState.loading<List<GlobalMedicine>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try{ // catch network exception
            Log.d(TAG, "Call Api Section")
            val medicines = service.searchAllListGlobalMedicine(
                "Token ${authToken.token}",
                query = query,
                page = page
            ).results.map {
                Log.d(TAG, "looping toGLobalMedicine")
                it.toGlobalMedicine()
            }

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
            Log.d(TAG, "Exception " + e.toString())
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
        val cachedBlogs = cache.searchGlobalMedicineWithQuery(
            query = query,
            page = page,
//            ordering = "id"
        ).map { it.toGlobalMedicine() }

        emit(DataState.data(response = null, data = cachedBlogs))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}