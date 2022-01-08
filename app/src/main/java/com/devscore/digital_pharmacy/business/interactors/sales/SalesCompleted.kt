package com.devscore.digital_pharmacy.business.interactors.sales

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesOrderDto
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.toSalesOrder
import com.devscore.digital_pharmacy.business.datasource.network.sales.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow





class SalesCompleted(
    private val service : SalesApiService,
    private val cache : SalesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int,
        createSalesOder: CreateSalesOrder
    ): Flow<DataState<SalesOrder>> = flow {
        emit(DataState.loading<SalesOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
            Log.d(TAG, "Call Api Section")
            if (createSalesOder.customer == -1) {
                createSalesOder.customer = null
            }
            createSalesOder.status = 3
            val result = service.salesCompleted(
                    "Token ${authToken.token}",
                    pk,
                    createSalesOder
                ).toSalesOrder()

            Log.d(TAG, result.toString())

            val order = result


            try{
                Log.d(TAG, "Data " + order.toString())
                cache.deleteSalesOder(pk = order.pk!!)
                cache.insertSalesOder(order.toSalesOrderEntity())
                for (medicine in order.toSalesOrderMedicinesEntity()) {
                    cache.insertSaleOderMedicine(medicine)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

            emit(DataState.data(response = Response(
                message = "Payment successful",
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Success()
            ), data = order))
            return@flow
//
//        }catch (e: Exception){
//            e.printStackTrace()
//            emit(
//                DataState.error<SalesOrder>(
//                    response = Response(
//                        message = "Can't pay with out internet",
//                        uiComponentType = UIComponentType.Dialog(),
//                        messageType = MessageType.Error()
//                    )
//                )
//            )
//            return@flow
//        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}

//class SalesCompleted(
//    private val service : SalesApiService,
//    private val cache : SalesDao
//) {
//
//    private val TAG: String = "AppDebug"
//
//    fun execute(
//        authToken: AuthToken?,
//        pk : Int,
//        createSalesOder: CreateSalesOrder
//    ): Flow<DataState<SalesOrder>> = flow {
//        emit(DataState.loading<SalesOrder>())
//        if(authToken == null){
//            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
//        }
//
//        try{
//            Log.d(TAG, "Call Api Section")
//            if (createSalesOder.customer == -1) {
//                createSalesOder.customer = null
//            }
//            createSalesOder.status = 3
//            var result : SalesOrder? = null
//            if (pk < 1) {
////                result = service.createSalesOder(
////                    "Token ${authToken.token}",
////                    createSalesOder
////                ).toSalesOrder()
//                emit(
//                    DataState.error<SalesOrder>(
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
//                result = service.salesCompleted(
//                    "Token ${authToken.token}",
//                    pk,
//                    createSalesOder
//                ).toSalesOrder()
//            }
//
//            Log.d(TAG, result.toString())
//
//            val order = result
//
//
//            try{
//                Log.d(TAG, "Data " + order.toString())
//                cache.deleteSalesOder(pk = order.pk!!)
//                cache.insertSalesOder(order.toSalesOrderEntity())
//                for (medicine in order.toSalesOrderMedicinesEntity()) {
//                    cache.insertSaleOderMedicine(medicine)
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
//                DataState.error<SalesOrder>(
//                    response = Response(
//                        message = "Can't pay with out internet",
//                        uiComponentType = UIComponentType.Dialog(),
//                        messageType = MessageType.Error()
//                    )
//                )
//            )
//            return@flow
//        }
//
//    }.catch { e ->
//        emit(handleUseCaseException(e))
//    }
//}