package com.devscore.digital_pharmacy.business.interactors.supplier

import android.content.Context
import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.SupplierDao
import com.devscore.digital_pharmacy.business.datasource.network.GenericResponse
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.supplier.SupplierApiService
import com.devscore.digital_pharmacy.business.datasource.network.supplier.network_response.toSupplier
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class UpdateSupplierInteractor (
    private val service : SupplierApiService,
    private val cache : SupplierDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int,
        createSupplier: CreateSupplier
    ): Flow<DataState<Supplier>> = flow {

        emit(DataState.loading<Supplier>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }



        try {
            Log.d(TAG, "Call Api Section")
            val supplier = service.updateSupplier(
                "Token ${authToken.token}",
                pk = pk,
                createSupplier
            ).toSupplier()


            Log.d(TAG, "CreateSupplierInteractior " + supplier.toString())
            cache.insertSupplier(supplier.toSupplierEntity())

            emit(
                DataState.data(response = Response(
                    message = "Update successfully.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = supplier))
        }
        catch (e : Exception) {
            e.printStackTrace()


            when (e) {
                is HttpException -> {
                    val body = e.response()?.errorBody()?.string()!!
                    Log.d(com.devscore.digital_pharmacy.business.interactors.account.TAG, body)
                    val errorEntity : GenericResponse = Gson().fromJson(body, GenericResponse::class.java)
                    Log.d(com.devscore.digital_pharmacy.business.interactors.account.TAG, errorEntity.toString() + " ")
                    when (e.code()) {
                        400 ->
                            Log.d(TAG, "400 Bad Request " + e.response()?.errorBody().toString())
                        500 ->
                            Log.d(TAG, "500 Bad Request " + e.response()?.errorBody().toString())
                        401 ->
                            Log.d(TAG, "401 Bad Request " + e.response()?.errorBody().toString())

                        404 -> {
                            Log.d(TAG, "404 Bad Request " + e.response()?.errorBody().toString())
                            cache.deleteSupplier(pk)
                            emit(
                                DataState.error<Supplier>(
                                    response = Response(
                                        message = errorEntity.errorMessage,
                                        uiComponentType = UIComponentType.Dialog(),
                                        messageType = MessageType.Error()
                                    )
                                )
                            )
                            return@flow
                        }

                        else ->
                            Log.d(TAG, "else Bad Request " + e.response()?.errorBody().toString())

                    }
                    emit(
                        DataState.error<Supplier>(
                            response = Response(
                                message = errorEntity.errorMessage,
                                uiComponentType = UIComponentType.Dialog(),
                                messageType = MessageType.Error()
                            )
                        )
                    )
                }


                is IOException -> {
                    Log.d(TAG, "IOException excepiton")
                    emit(DataState.error<Supplier>(
                        response = Response(
                            message = e.message,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                    )
                }
                else -> {
                    Log.d(TAG, "Unknown excepiton")
                    emit(DataState.error<Supplier>(
                        response = Response(
                            message = e.message,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                    )
                }
            }
        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}