package com.devscore.digital_pharmacy.business.interactors.purchases

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.PurchasesDao
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.toPurchasesOder
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.toPurchasesOrder
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.purchases.PurchasesApiService
import com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response.toPurchasesOrder
import com.devscore.digital_pharmacy.business.datasource.network.purchases.toPurchasesOder
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.toSalesOrder
import com.devscore.digital_pharmacy.business.datasource.network.sales.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow







class PurchasesCompleted (
    private val service : PurchasesApiService,
    private val cache : PurchasesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int,
        createPurchasesOrder : CreatePurchasesOder
    ): Flow<DataState<PurchasesOrder>> = flow {
        emit(DataState.loading<PurchasesOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
            Log.d(TAG, "Call Api Section")
            if (createPurchasesOrder.vendor == -1) {
                createPurchasesOrder.vendor = null
            }
            createPurchasesOrder.status = 3
            val result = service.purchasesCompleted(
                    "Token ${authToken.token}",
                    pk,
                    createPurchasesOrder
                ).toPurchasesOder()

            Log.d(TAG, result.toString())

            val order = result


            try{
                Log.d(TAG, "Data " + order.toString())
                cache.deletePurchasesOrder(pk = order.pk!!)
                cache.insertPurchasesOrder(order.toPurchasesOrderEntity())
                for (medicine in order.toPurchasesOrderMedicines()) {
                    cache.insertPurchasesOrderMedicine(medicine)
                }
                emit(DataState.data(response = Response(
                    message = "Payment successful",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = order))
                return@flow
            }catch (e: Exception){
                e.printStackTrace()
            }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}

//class PurchasesCompleted (
//    private val service : PurchasesApiService,
//    private val cache : PurchasesDao
//) {
//
//    private val TAG: String = "AppDebug"
//
//    fun execute(
//        authToken: AuthToken?,
//        pk : Int,
//        createPurchasesOrder : CreatePurchasesOder
//    ): Flow<DataState<PurchasesOrder>> = flow {
//        emit(DataState.loading<PurchasesOrder>())
//        if(authToken == null){
//            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
//        }
//
//        try{
//            Log.d(TAG, "Call Api Section")
//            if (createPurchasesOrder.vendor == -1) {
//                createPurchasesOrder.vendor = null
//            }
//            createPurchasesOrder.status = 3
//            var result : PurchasesOrder? = null
//            if (pk <= 0) {
////                result = service.purchasesCompleted(
////                    "Token ${authToken.token}",
////                    pk = pk,
////                    createPurchasesOrder
////                ).toPurchasesOder()
//                emit(
//                    DataState.error<PurchasesOrder>(
//                        response = Response(
//                            message = "Can't pay this order",
//                            uiComponentType = UIComponentType.Dialog(),
//                            messageType = MessageType.Error()
//                        )
//                    )
//                )
//                return@flow
//            }
//            else {
//                result = service.purchasesCompleted(
//                    "Token ${authToken.token}",
//                    pk,
//                    createPurchasesOrder
//                ).toPurchasesOder()
//            }
//
//            Log.d(TAG, result.toString())
//
//            val order = result
//
//
//            try{
//                Log.d(TAG, "Data " + order.toString())
//                cache.deletePurchasesOrder(pk = order.pk!!)
//                cache.insertPurchasesOrder(order.toPurchasesOrderEntity())
//                for (medicine in order.toPurchasesOrderMedicines()) {
//                    cache.insertPurchasesOrderMedicine(medicine)
//                }
//                emit(DataState.data(response = Response(
//                    message = "Successfully Uploaded.",
//                    uiComponentType = UIComponentType.Dialog(),
//                    messageType = MessageType.Success()
//                ), data = order))
//                return@flow
//            }catch (e: Exception){
//                e.printStackTrace()
//            }
//
//        }catch (e: Exception){
//            e.printStackTrace()
//            emit(
//                DataState.error<PurchasesOrder>(
//                    response = Response(
//                        message = "Can't pay with out internet",
//                        uiComponentType = UIComponentType.Dialog(),
//                        messageType = MessageType.Error()
//                    )
//                )
//            )
//            return@flow
//        }
//    }.catch { e ->
//        emit(handleUseCaseException(e))
//    }
//}
