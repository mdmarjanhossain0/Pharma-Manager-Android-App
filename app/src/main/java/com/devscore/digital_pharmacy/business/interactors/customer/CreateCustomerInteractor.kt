package com.devscore.digital_pharmacy.business.interactors.customer

import android.content.Context
import android.util.Log
import androidx.work.*
import com.devscore.digital_pharmacy.business.datasource.cache.customer.CustomerDao
import com.devscore.digital_pharmacy.business.datasource.network.GenericResponse
import com.devscore.digital_pharmacy.business.datasource.network.customer.CustomerApiService
import com.devscore.digital_pharmacy.business.datasource.network.customer.network_response.toCustomer
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.presentation.util.CustomerWorker
import com.devscore.digital_pharmacy.presentation.util.SalesWorker
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

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

            cache.insertCustomer(customer.toCustomerEntity())

            emit(
                DataState.data(response = Response(
                    message = "Successfully Created.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = customer))

        }
        catch (e: Exception){
            e.printStackTrace()


            when (e) {
                is HttpException -> {
                    val body = e.response()?.errorBody()?.string()!!
                    Log.d(TAG, body)
                    val gson = GsonBuilder().create()
                    val errorEntity : GenericResponse = Gson().fromJson(body, GenericResponse::class.java)
                    Log.d(TAG, errorEntity.toString() + " ")
                    when (e.code()) {
                        400 ->
                            Log.d(TAG, "400 Bad Request " + e.response()?.errorBody().toString())
                        500 ->
                            Log.d(TAG, "500 Bad Request " + e.response()?.errorBody().toString())
                        401 ->
                            Log.d(TAG, "401 Bad Request " + e.response()?.errorBody().toString())

                        404 -> {
                            Log.d(TAG, "404 Bad Request " + e.response()?.errorBody().toString())
                        }

                        else ->
                            Log.d(TAG, "else Bad Request " + e.response()?.errorBody().toString())

                    }
                    emit(
                        DataState.error<Customer>(response = Response(
                            message = errorEntity.errorMessage,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )))
                }


                is IOException -> {
                    val draftCustomer = createCustomer.toCustomer().copy(
                        room_id = null
                    )
                    cache.insertFailureCustomer(draftCustomer.toCustomerFailureEntity())

                    val state = createCustomer.toCustomer()
                    emit(
                        DataState.data(response = Response(
                            message = "Create a draft customer. Please be careful and don't uninstall or log out",
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Error()
                        ), data = state))
                    Log.d(TAG, "IOException excepiton")
                }
                else -> {
                    Log.d(TAG, "Unknown excepiton")
                    val state = createCustomer.toCustomer()
                    emit(
                        DataState.data(response = Response(
                            message = e.message,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        ), data = state))
                }
            }
        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}