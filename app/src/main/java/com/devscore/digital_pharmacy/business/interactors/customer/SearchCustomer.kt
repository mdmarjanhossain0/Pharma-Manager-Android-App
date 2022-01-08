package com.devscore.digital_pharmacy.business.interactors.customer

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.customer.CustomerDao
import com.devscore.digital_pharmacy.business.datasource.cache.customer.toCustomer
import com.devscore.digital_pharmacy.business.datasource.network.customer.CustomerApiService
import com.devscore.digital_pharmacy.business.datasource.network.customer.network_response.toCustomer
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.toCustomerEntity
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchCustomer (
    private val service : CustomerApiService,
    private val cache : CustomerDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        page: Int
    ): Flow<DataState<List<Customer>>> = flow {
        emit(DataState.loading<List<Customer>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        val success = cache.searchAllCustomer(
            query = query,
            page = page
        ).map { it.toCustomer() }

        val failure = cache.searchAllFailureSupplier(
            query = query,
        ).map {
            it.toCustomer()
        }


        emit(DataState.data(response = null, data = marge(success, failure)))

        try{
            Log.d(TAG, "Call Api Section")
            val customers = service.searchCustomer(
                "Token ${authToken.token}",
                query = query,
                page = page
            ).results.map {
                it.toCustomer()
            }

            Log.d(TAG, customers.toString())
            for(customer in customers){
                try{
                    Log.d(TAG, "Caching size" + customers.size.toString())
                    Log.d(TAG, customer.toString())
                    cache.insertCustomer(customer.toCustomerEntity())
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(
                DataState.error<List<Customer>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        val successList = cache.searchAllCustomer(
            query = query,
            page = page
        ).map { it.toCustomer() }

        val failureList = cache.searchAllFailureSupplier(
            query = query,
        ).map {
            it.toCustomer()
        }





        emit(DataState.data(response = null, data = marge(successList, failureList)))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}

fun marge(successList: List<Customer>, failureList : List<Customer>) : List<Customer> {
    val allMedicine  = mutableListOf<Customer>()
    allMedicine.addAll(successList)
    allMedicine.addAll(failureList)
    return allMedicine
}