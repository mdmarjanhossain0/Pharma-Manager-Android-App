package com.devscore.digital_pharmacy.business.interactors.inventory.local

import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.GlobalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.toGlobalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.DataState
import com.devscore.digital_pharmacy.business.domain.util.MessageType
import com.devscore.digital_pharmacy.business.domain.util.Response
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
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