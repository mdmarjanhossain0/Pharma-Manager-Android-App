package com.appbytes.pharma_manager.business.interactors.inventory.local

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.*
import com.appbytes.pharma_manager.business.datasource.network.ExtractHTTPException

import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.inventory.InventoryApiService
import com.appbytes.pharma_manager.business.datasource.network.inventory.searchLocalMedicine
import com.appbytes.pharma_manager.business.datasource.network.inventory.toLocalMedicine
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class SearchLocalMedicine(
    private val service: InventoryApiService,
    private val cache: LocalMedicineDao,
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        action : String,
        page: Int
    ): Flow<DataState<List<LocalMedicine>>> = flow {
        emit(DataState.loading<List<LocalMedicine>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        val success = cache.searchLocalMedicine(
            query = query,
            action = action,
            page = page
        ).map { it.toLocalMedicine() }

        val failure = cache.searchFailureMedicineWithUnits(
            query = query,
        ).map {
            it.toLocalMedicine()
        }
        emit(DataState.data(response = null, data = marge(success, failure)))

        try{
            Log.d(TAG, "Call Api Section")
            val medicines = service.searchLocalMedicine(
                "Token ${authToken.token}",
                query = query,
                action = action,
                page = page
            ).results.map {
                Log.d(TAG, "looping toLocalMedicine")
                it.toLocalMedicine()
            }

            for(medicine in medicines){
                try{
                    Log.d(TAG, "Caching size" + medicines.size.toString())
                    cache.insertLocalMedicine(medicine.toLocalMedicineEntity())
                    for (unit in medicine.toLocalMedicineUnitEntity()) {
                        cache.insertLocalMedicineUnit(unit)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            when (e) {
                is HttpException -> {
                    when (e.code()) {
                        401 ->{
                            Log.d(TAG, "401 Unauthorized " + e.response()?.errorBody().toString())
                            emit(
                                DataState.error<List<LocalMedicine>>(
                                    response = Response(
                                        message = "Session Expired",
                                        uiComponentType = UIComponentType.Dialog(),
                                        messageType = MessageType.Error()
                                    )
                                )
                            )
                            ExtractHTTPException.getInstance().unauthorized()
                            return@flow
                        }
                    }
                }


                is IOException -> {
                    Log.d(TAG, "IOException exception")
                    emit(
                        DataState.error<List<LocalMedicine>>(
                            response = Response(
                                message = "Unable to update the cache for network exception.",
                                uiComponentType = UIComponentType.None(),
                                messageType = MessageType.Error()
                            )
                        )
                    )
                }
                else -> {
                    Log.d(TAG, "Unknown exception")
                    emit(
                        DataState.error<List<LocalMedicine>>(
                            response = Response(
                                message = "Unable to update the cache for network exception.",
                                uiComponentType = UIComponentType.None(),
                                messageType = MessageType.Error()
                            )
                        )
                    )
                }
            }
        }

        val localMedicine = cache.searchLocalMedicine(
            query = query,
            action = action,
            page = page
        ).map { it.toLocalMedicine() }

        val failureMedicine = cache.searchFailureMedicineWithUnits(
            query = query,
        ).map {
            it.toLocalMedicine()
        }





        emit(DataState.data(response = null, data = marge(localMedicine, failureMedicine)))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}

fun marge(localMedicine: List<LocalMedicine>, failureMedicine : List<LocalMedicine>) : List<LocalMedicine> {
    val allMedicine  = mutableListOf<LocalMedicine>()
    allMedicine.addAll(failureMedicine)
    allMedicine.addAll(localMedicine)
    return allMedicine
}
