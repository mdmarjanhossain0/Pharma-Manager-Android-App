package com.appbytes.pharma_manager.business.interactors.sales

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.LocalMedicineDao
import com.appbytes.pharma_manager.business.datasource.cache.sales.SalesDao
import com.appbytes.pharma_manager.business.datasource.cache.sales.toSalesOder
import com.appbytes.pharma_manager.business.datasource.network.ExtractHTTPException
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.sales.SalesApiService
import com.appbytes.pharma_manager.business.datasource.network.sales.toSalesOrder
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class SearchSalesOder(
    private val service : SalesApiService,
    private val cache : SalesDao,
    private val localMedicineDao: LocalMedicineDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        status : Int,
        page: Int
    ): Flow<DataState<List<SalesOrder>>> = flow {
        emit(DataState.loading<List<SalesOrder>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }


        /*if (status == 0) {
            val success = cache.searchGenerateOrderWithMedicine(
                page = page
            ).map { it.toSalesOder() }

            val failure = cache.searchFailureSalesOderWithMedicine(
            ).map {
                it.toSalesOder()
            }
            emit(DataState.data(response = null, data = marge(success, failure)))
        }
        else {
            val success = cache.searchCompleteOrderWithMedicine(
                query = query,
                status = status,
                page = page
            ).map { it.toSalesOder() }
            emit(DataState.data(response = null, data = success))
        }*/


        if (status == 0) {
            val success = cache.searchGenerateOrderWithMedicine(
                page = page
            ).map { it.toSalesOder() }

            val failure = cache.searchFailureSalesOderWithMedicine().map {
                it.toSalesOder()
            }
            Log.d(TAG, "Sales Failure " + failure.size + " " + failure.toString())
            emit(DataState.data(response = null, data = marge(success, failure)))
        }
        else {
            val success = cache.searchCompleteOrderWithMedicine(
                query = query,
                status = status,
                page = page
            ).map { it.toSalesOder() }
            emit(DataState.data(response = null, data = success))
        }

        try{
            Log.d(TAG, "Call Api Section")
            val result = service.searchSalesOder(
                "Token ${authToken.token}",
                query = query,
                status = status,
                page = page
            )

            Log.d(TAG, result.toString())

            val oderList = result.results.map {
                it.toSalesOrder()
            }

            for(oder in oderList){
                try{
                    Log.d(TAG, "Sales Caching size" + oderList.size.toString())
                    cache.insertSalesOder(oder.toSalesOrderEntity())
                    for (medicine in oder.toSalesOrderMedicinesEntity()) {
                        cache.insertSaleOderMedicine(medicine)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }

            }

//            for (medicines in result.results) {
//                for (medicine in medicines.sales_oder_medicines) {
//                    try {
//                        localMedicineDao.insertLocalMedicine(medicine.details?.toLocalMedicine()?.toLocalMedicineEntity()!!)
//                        for (unit in medicine.details?.toLocalMedicine()?.toLocalMedicineUnitEntity()!!) {
//                            localMedicineDao.insertLocalMedicineUnit(unit)
//                        }
//                    }
//                    catch (e : Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
        }catch (e: Exception){
            e.printStackTrace()
            when (e) {
                is HttpException -> {
                    when (e.code()) {
                        401 ->{
                            Log.d(TAG, "401 Unauthorized " + e.response()?.errorBody().toString())
                            emit(DataState.loading<List<SalesOrder>>(isLoading = false))
                            ExtractHTTPException.getInstance().unauthorized()
                            return@flow
                        }
                    }
                }
            }
            emit(
                DataState.error<List<SalesOrder>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }



        if (status == 0) {
            val success = cache.searchGenerateOrderWithMedicine(
                page = page
            ).map { it.toSalesOder() }

            val failure = cache.searchFailureSalesOderWithMedicine().map {
                it.toSalesOder()
            }
            Log.d(TAG, "Sales Failure " + failure.size + " " + failure.toString())
            emit(DataState.data(response = null, data = marge(success, failure)))
        }
        else {
            val success = cache.searchCompleteOrderWithMedicine(
                query = query,
                status = status,
                page = page
            ).map { it.toSalesOder() }
            emit(DataState.data(response = null, data = success))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}

fun marge(successList: List<SalesOrder>, failureList : List<SalesOrder>) : List<SalesOrder> {
    val allMedicine  = mutableListOf<SalesOrder>()
    allMedicine.addAll(failureList)
    allMedicine.addAll(successList)
    return allMedicine
}