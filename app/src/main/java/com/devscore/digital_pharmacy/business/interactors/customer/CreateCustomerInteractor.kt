package com.devscore.digital_pharmacy.business.interactors.customer

import android.content.Context
import android.util.Log
import androidx.work.*
import com.devscore.digital_pharmacy.business.datasource.cache.customer.CustomerDao
import com.devscore.digital_pharmacy.business.datasource.network.customer.CustomerApiService
import com.devscore.digital_pharmacy.business.datasource.network.customer.network_response.toCustomer
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.presentation.util.CustomerWorker
import com.devscore.digital_pharmacy.presentation.util.SalesWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateCustomerInteractor (
    private val service : CustomerApiService,
    private val cache : CustomerDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        createCustomer: CreateCustomer
    ): Flow<DataState<Customer>> = flow {

        emit(DataState.loading<Customer>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try{
            Log.d(TAG, "Call Api Section")
            val customer = service.createCustomer(
                "Token ${authToken.token}",
                createCustomer
            ).toCustomer()


            try{
                cache.insertCustomer(customer.toCustomerEntity())
            }catch (e: Exception){
                e.printStackTrace()
            }

            emit(
                DataState.data(response = Response(
                    message = "Successfully Created.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = customer))

            return@flow


        } catch (e: Exception){
            e.printStackTrace()

            try{
                val draftCustomer = createCustomer.toCustomer().copy(
                    room_id = null
                )
                cache.insertFailureCustomer(draftCustomer.toCustomerFailureEntity())
            } catch (e: Exception){
                e.printStackTrace()
            }







            /*val constraints = Constraints.Builder().setRequiresCharging(false).setRequiredNetworkType(
                NetworkType.CONNECTED).build()
            val syncWorkRequest : WorkRequest =
                OneTimeWorkRequestBuilder<CustomerWorker>()
                    .setConstraints(constraints)
                    .build()

            WorkManager
                .getInstance(context)
                .enqueue(syncWorkRequest)*/
        }


        val state = createCustomer.toCustomer()

        emit(
            DataState.data(response = Response(
                message = "Create a draft customer. Please be careful and don't uninstall or log out",
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Error()
            ), data = state))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}