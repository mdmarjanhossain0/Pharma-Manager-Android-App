package com.appbytes.pharma_manager.business.interactors.inventory.local

import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.LocalMedicineDao
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.toLocalMedicine
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.inventory.InventoryApiService
import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.util.DataState
import com.appbytes.pharma_manager.business.domain.util.MessageType
import com.appbytes.pharma_manager.business.domain.util.Response
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class FetchLocalMedicineData(
    private val service: InventoryApiService,
    private val cache: LocalMedicineDao,
) {

    private val TAG: String = "AppDebug"

    fun execute(
        id : Int
    ): Flow<DataState<LocalMedicine>> = flow {
        emit(DataState.loading<LocalMedicine>())



        try {
            val data = cache.getLocalMedicine(id = id)?.toLocalMedicine()
            emit(
                DataState.data(
                response = null,
                data = data
            ))
        }
        catch (e : Exception) {
            emit(
                DataState.error<LocalMedicine>(
                    response = Response(
                        message = "Data not found",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}