package com.devscore.digital_pharmacy.business.datasource.network

import android.util.Log
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.business.interactors.account.TAG
import com.devscore.digital_pharmacy.presentation.session.SessionEvents
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

fun <T> handleUseCaseException(e: Throwable): DataState<T> {
    e.printStackTrace()
    when (e) {
        is HttpException -> { // Retrofit exception
            return ExtractHTTPException.instance?.extractHttpExceptions(e)!!
        }


        is IOException -> {
            Log.d(TAG, "IOException exception")
            return DataState.error<T>(
                response = Response(
                    message = e.message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }
        else -> {
            Log.d(TAG, "Unknown exception")
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

fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ErrorHandling.UNKNOWN_ERROR
    }
}
class ExtractHTTPException(val sessionManager: SessionManager) {

    companion object {
        var instance : ExtractHTTPException? = null
        @JvmName("getInstance1")
        fun getInstance() : ExtractHTTPException {
            return instance!!
        }


        fun setInstance(sessionManager: SessionManager) {
            instance = ExtractHTTPException(sessionManager)
        }

    }
    fun <T>extractHttpExceptions(e: HttpException) : DataState<T>{
        val body = e.response()?.errorBody()?.string()!!
        val errorEntity : GenericResponse = Gson().fromJson(body, GenericResponse::class.java)
        when (e.code()) {
            400 ->
                Log.d(TAG, "400 Bad Request " + e.response()?.errorBody().toString())
            500 ->
                Log.d(TAG, "500 Internal Server Error " + e.response()?.errorBody().toString())
            401 ->{
                Log.d(TAG, "401 Unauthorized " + e.response()?.errorBody().toString())
                return DataState.error<T>(
                    response = Response(
                        message = errorEntity.errorMessage,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
                unauthorized()
            }

            404 ->
                Log.d(TAG, "404 Not found" + e.response()?.errorBody().toString())

            else ->
                Log.d(TAG, "else Bad Request " + e.response()?.errorBody().toString())

        }
        return DataState.error<T>(
            response = Response(
                message = convertErrorBody(e),
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Error()
            )
        )
    }


    fun unauthorized() {
        sessionManager.onTriggerEvent(SessionEvents.Logout)
    }
}




//            val errorResponse = convertErrorBody(e)
//            return DataState.error<T>(
//                response = Response(
//                    message = errorResponse,
//                    uiComponentType = UIComponentType.Dialog(),
//                    messageType = MessageType.Error()
//                )
//            )