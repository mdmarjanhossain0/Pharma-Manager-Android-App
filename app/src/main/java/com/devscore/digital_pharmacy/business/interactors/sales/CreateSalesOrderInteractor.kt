package com.devscore.digital_pharmacy.business.interactors.sales

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineEntity
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineUnitEntity
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateSalesOderInteractor (
    private val service : SalesApiService,
    private val cache : SalesDao,
    private val localMedicineDao: LocalMedicineDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        createSalesOder: CreateSalesOrder,
    ): Flow<DataState<SalesOrder>> = flow {

        emit(DataState.loading<SalesOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        if (createSalesOder.customer == -1) {
            createSalesOder.customer = null
        }

        try{
            Log.d(TAG, "Call Api Section")
            val result = service.createSalesOder(
                "Token ${authToken.token}",
                createSalesOder
            )


            val salesOder = result.toSalesOrder()
            try{
                cache.insertSalesOder(salesOder.toSalesOrderEntity())
                for (medicine in salesOder.toSalesOrderMedicinesEntity()) {
                    cache.insertSaleOderMedicine(medicine)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
//
//            for (medicine in result.sales_oder_medicines) {
//                try {
//                    localMedicineDao.insertLocalMedicine(medicine.details?.toLocalMedicine()?.toLocalMedicineEntity()!!)
//                    for (unit in medicine.details?.toLocalMedicine()?.toLocalMedicineUnitEntity()!!) {
//                        localMedicineDao.insertLocalMedicineUnit(unit)
//                    }
//                }
//                catch (e : Exception) {
//                    e.printStackTrace()
//                }
//            }

            emit(
                DataState.data(response = Response(
                    message = "Successfully Uploaded.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = salesOder))
            return@flow


        }
        catch (e: Exception){
            e.printStackTrace()

            try{
                val order = createSalesOder.toSalesOder()
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
            }
        }



        val stateSalesODer = createSalesOder.toSalesOder()


        emit(
            DataState.data(response = Response(
                message = "Create a draft order. Please be careful and don't uninstall or log out",
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Error()
            ), data = stateSalesODer))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}