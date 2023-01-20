package com.appbytes.pharma_manager.business.interactors.customer

import com.appbytes.pharma_manager.business.datasource.cache.customer.CustomerDao
import com.appbytes.pharma_manager.business.datasource.cache.customer.toCustomer
import com.appbytes.pharma_manager.business.datasource.network.customer.CustomerApiService
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetCustomerDetails (
    private val service: CustomerApiService,
    private val cache : CustomerDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int
    ): Flow<DataState<Customer>> = flow {

        emit(DataState.loading<Customer>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        val customer = cache.getCustomer(pk = pk).toCustomer()
        emit(DataState.data(response = null, data = customer))


    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}