package com.devscore.digital_pharmacy.business.interactors.sales

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineEntity
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineUnitEntity
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.network_responses.toList
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.SalesOrder
import com.devscore.digital_pharmacy.business.domain.models.SalesReturnOrder
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SalesOrderLocalDetailsInteractor(
    private val service : SalesApiService,
    private val cache : SalesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int
    ): Flow<DataState<SalesOrder>> = flow {
        emit(DataState.loading<SalesOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try {
            val order = cache.getSalesOrder(pk).toSalesOder()
            emit(DataState.data(response = null, data = order))
        }
        catch (e : java.lang.Exception) {
            e.printStackTrace()
            emit(
                DataState.error<SalesOrder>(
                    response = Response(
                        message = "Order not found or network not available",
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