package com.appbytes.pharma_manager.business.interactors.sales

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.sales.SalesDao
import com.appbytes.pharma_manager.business.datasource.network.ExtractHTTPException
import com.appbytes.pharma_manager.business.datasource.network.GenericResponse
import com.appbytes.pharma_manager.business.datasource.network.convertErrorBody
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.sales.SalesApiService
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class DeleteSalesOrderInteractor(
    private val service: SalesApiService,
    private val cache: SalesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        order: SalesOrder
    ): Flow<DataState<GenericResponse>> = flow {

        emit(DataState.loading<GenericResponse>())
        if (authToken == null) {
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        Log.d(TAG, "Call Api Section")
        Log.d(TAG, order.toString())
        if (order.pk!! > 0) {
            try {
                val response = service.salesOrderDelete(
                    "Token ${authToken.token}",
                    order.pk!!
                )
                cache.deleteSalesOder(pk = order.pk!!)
                emit(
                    DataState.data(
                        response = Response(
                            message = "Delete Successfully.",
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ), data = response
                    )
                )
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> { // Retrofit exception
                        val body = e.response()?.errorBody()?.string()!!
                        val errorEntity: GenericResponse =
                            Gson().fromJson(body, GenericResponse::class.java)
                        when (e.code()) {
                            400 -> {
                                Log.d(TAG, "400 Bad Request " + e.response()?.errorBody().toString())
                                emit(
                                    DataState.error<GenericResponse>(
                                        response = Response(
                                            message = convertErrorBody(e),
                                            uiComponentType = UIComponentType.Dialog(),
                                            messageType = MessageType.Error()
                                        )
                                    )
                                )
                            }
                            500 -> {
                                Log.d(
                                    TAG,
                                    "500 Internal Server Error " + e.response()?.errorBody()
                                        .toString()
                                )
                                emit(
                                    DataState.error<GenericResponse>(
                                        response = Response(
                                            message = convertErrorBody(e),
                                            uiComponentType = UIComponentType.Dialog(),
                                            messageType = MessageType.Error()
                                        )
                                    )
                                )
                            }
                            401 -> {
                                Log.d(
                                    TAG,
                                    "401 Unauthorized " + e.response()?.errorBody().toString()
                                )
                                emit(
                                    DataState.error<GenericResponse>(
                                        response = Response(
                                            message = errorEntity.errorMessage,
                                            uiComponentType = UIComponentType.Dialog(),
                                            messageType = MessageType.Error()
                                        )
                                    )
                                )
                                ExtractHTTPException.getInstance().unauthorized()
                                return@flow
                            }

                            404 -> {
                                cache.deleteSalesOder(pk = order.pk!!)
                                emit(
                                    DataState.error<GenericResponse>(
                                        response = Response(
                                            message = errorEntity.errorMessage,
                                            uiComponentType = UIComponentType.Dialog(),
                                            messageType = MessageType.Error()
                                        )
                                    )
                                )
                                Log.d(TAG, "404 Not found" + e.response()?.errorBody().toString())
                                return@flow
                            }

                            else -> {
                                Log.d(
                                    TAG,
                                    "else Bad Request " + e.response()?.errorBody().toString()
                                )
                                emit(
                                    DataState.error<GenericResponse>(
                                        response = Response(
                                            message = convertErrorBody(e),
                                            uiComponentType = UIComponentType.Dialog(),
                                            messageType = MessageType.Error()
                                        )
                                    )
                                )
                            }

                        }
                    }


                    is IOException -> {
                        Log.d(
                            com.appbytes.pharma_manager.business.interactors.account.TAG,
                            "IOException exception"
                        )
                        emit(
                            DataState.error<GenericResponse>(
                                response = Response(
                                    message = "May be network not available",
                                    uiComponentType = UIComponentType.Toast(),
                                    messageType = MessageType.Error()
                                )
                            )
                        )
                    }
                    else -> {
                        Log.d(TAG, "Unknown exception")
                        emit(
                            DataState.error<GenericResponse>(
                                response = Response(
                                    message = e.message,
                                    uiComponentType = UIComponentType.Toast(),
                                    messageType = MessageType.Error()
                                )
                            )
                        )
                    }
                }
            }
        } else {
            cache.deleteFailureSalesOder(order.room_id!!)
            emit(
                DataState.data(
                    response = Response(
                        message = "Delete Successfully.",
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Success()
                    ), data = GenericResponse(
                        response = "Delete Successfully",
                        errorMessage = null
                    )
                )
            )
        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}