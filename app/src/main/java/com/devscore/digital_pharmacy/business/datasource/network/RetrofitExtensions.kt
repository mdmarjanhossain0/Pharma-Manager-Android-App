package com.devscore.digital_pharmacy.business.datasource.network

import android.util.Log
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.business.interactors.account.TAG
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

fun <T> handleUseCaseException(e: Throwable): DataState<T> {
    e.printStackTrace()
    when (e) {
        is HttpException -> { // Retrofit exception
            extractHttpExceptions(e)
            val errorResponse = convertErrorBody(e)
            return DataState.error<T>(
                response = Response(
                    message = errorResponse,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }


        is IOException -> {
            Log.d(TAG, "IOException excepiton")
            return DataState.error<T>(
                response = Response(
                    message = e.message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }
        else -> {
            Log.d(TAG, "Unknown excepiton hffhsdlfhsdlfsdhlfjsdlfsdhflsdfjlfjlfjslfjsdlfj")
            return DataState.error<T>(
                response = Response(
                    message = e.message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ErrorHandling.UNKNOWN_ERROR
    }
}

private fun extractHttpExceptions(ex: HttpException){
    val body = ex.response()?.errorBody()?.string()!!
    Log.d(TAG, body)
    val gson = GsonBuilder().create()
//    val responseBody = gson.fromJson(body.toString(), JsonObject::class.java)
    val errorEntity : GenericResponse = Gson().fromJson(body, GenericResponse::class.java)
    Log.d(TAG, errorEntity.toString() + " ")
    when (ex.code()) {
        400 ->
            Log.d(TAG, "400 Bad Request " + ex.response()?.errorBody().toString())
        500 ->
            Log.d(TAG, "500 Bad Request " + ex.response()?.errorBody().toString())
        401 ->
            Log.d(TAG, "401 Bad Request " + ex.response()?.errorBody().toString())

        404 ->
            Log.d(TAG, "404 Bad Request " + ex.response()?.errorBody().toString())

        else ->
            Log.d(TAG, "else Bad Request " + ex.response()?.errorBody().toString())

    }
}