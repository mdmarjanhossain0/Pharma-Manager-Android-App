package com.appbytes.pharma_manager.business.interactors.purchases

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.purchases.PurchasesDao
import com.appbytes.pharma_manager.business.datasource.cache.purchases.toPurchasesOder
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.purchases.PurchasesApiService
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.PurchasesOrder
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class PurchasesOrderLocalDetailsInteractor(
    private val service : PurchasesApiService,
    private val cache : PurchasesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int
    ): Flow<DataState<PurchasesOrder>> = flow {
        emit(DataState.loading<PurchasesOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try {
            Log.d(TAG, "Pk " + pk)
            val order = cache.getPurchasesOrder(pk).toPurchasesOder()
            emit(DataState.data(response = null, data = order))
        }
        catch (e : java.lang.Exception) {
            e.printStackTrace()
            emit(
                DataState.error<PurchasesOrder>(
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