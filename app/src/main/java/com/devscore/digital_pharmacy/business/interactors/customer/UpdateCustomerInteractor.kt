package com.devscore.digital_pharmacy.business.interactors.customer

import android.content.Context
import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.customer.CustomerDao
import com.devscore.digital_pharmacy.business.datasource.network.customer.CustomerApiService
import com.devscore.digital_pharmacy.business.datasource.network.customer.network_response.toCustomer
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UpdateCustomerInteractor (
    private val service : CustomerApiService,
    private val cache : CustomerDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int,
        createCustomer: CreateCustomer
    ): Flow<DataState<Customer>> = flow {

        emit(DataState.loading<Customer>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

            Log.d(TAG, "Call Api Section")
            val customer = service.updateCustomer(
                "Token ${authToken.token}",
                pk = pk,
                createCustomer
            ).toCustomer()


            try{
                cache.insertCustomer(customer.toCustomerEntity())
            }catch (e: Exception){
                e.printStackTrace()
            }

            emit(
                DataState.data(response = Response(
                    message = "Update successfully",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = customer))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}