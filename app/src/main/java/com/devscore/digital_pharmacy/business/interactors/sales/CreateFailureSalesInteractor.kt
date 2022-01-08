package com.devscore.digital_pharmacy.business.interactors.sales

import android.content.Context
import android.util.Log
import androidx.work.*
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.presentation.util.SalesWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateFailureSalesInteractor (
    private val service : SalesApiService,
    private val cache : SalesDao,
    private val context: Context
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        orders: List<SalesOrder>,
    ): Flow<DataState<SalesOrder>> = flow {

        emit(DataState.loading<SalesOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        for (createSalesOder in orders) {
            if (createSalesOder.customer == -1) {
                createSalesOder.customer = null
            }

            try{
                val salesOder = service.createSalesOder(
                    "Token ${authToken.token}",
                    createSalesOder.toCreateSalesOrder()
                ).toSalesOrder()


                try{
                    cache.insertSalesOder(salesOder.toSalesOrderEntity())
                    for (medicine in salesOder.toSalesOrderMedicinesEntity()) {
                        cache.insertSaleOderMedicine(medicine)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
                try{
                    cache.deleteFailureSalesOder(createSalesOder.room_id!!)
                }catch (e: Exception){
                    e.printStackTrace()
                }

            }
            catch (e: Exception){
                e.printStackTrace()

                /*try{
                    val order = createSalesOder
                    Log.d(TAG, "Order " + order.toString())
                    val room_id = cache.insertFailureSalesOder(order.toFailureSalesOrderEntity())
                    Log.d(TAG, "Room id " + room_id)
                    val draft = order.copy(
                        room_id = room_id
                    )
                    Log.d(TAG, "Draft " + draft.toString())
                    for (failureMedicine in draft.toFailureSalesOderMedicineEntity()) {
                        val room_id = cache.insertFailureSalesOderMedicine(failureMedicine)
                        Log.d(TAG, "Id " + room_id)
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                }*/
            }
        }


//        emit(
//            DataState.data(response = Response(
//                message = "Create a draft order. Please be careful and don't uninstall or log out",
//                uiComponentType = UIComponentType.Dialog(),
//                messageType = MessageType.Error()
//            ), data = orders[0]))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}