package com.devscore.digital_pharmacy.business.interactors.inventory

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.GlobalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.toGlobalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.toGlobalMedicineEntity
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.inventory.toGlobalMedicine
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class FetchGlobalMedicineData(
    private val service: InventoryApiService,
    private val cache: GlobalMedicineDao,
) {

    private val TAG: String = "AppDebug"

    fun execute(
        id : Int
    ): Flow<DataState<GlobalMedicine>> = flow {
        emit(DataState.loading<GlobalMedicine>())



        try {
            val data = cache.getGlobalMedicine(id = id)?.toGlobalMedicine()
            emit(DataState.data(
                response = null,
                data = data
            ))
        }
        catch (e : Exception) {
            emit(
                DataState.error<GlobalMedicine>(
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