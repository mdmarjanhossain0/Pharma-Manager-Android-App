package com.devscore.digital_pharmacy.business.interactors.auth

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.network.GenericResponse
import com.devscore.digital_pharmacy.business.datasource.network.auth.AuthService
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.RequestBody

class SendOtp (
    private val service : AuthService
) {

    private val TAG: String = "AppDebug"

    fun execute(
        email: String,
        shop_name : String,
        username: String,
        password: String,
        confirmPassword: String,
        mobile : String,
        license_key : String?,
        address : String,
        image : String?
    ): Flow<DataState<GenericResponse>> = flow {

        emit(DataState.loading<GenericResponse>())

        val response = service.sendOpt(
            email = RequestBody.create(
                MediaType.parse("text/plain"),
                email),
            shop_name = RequestBody.create(
                MediaType.parse("text/plain"),
                shop_name),
            username = RequestBody.create(
                MediaType.parse("text/plain"),
                username),
            password = RequestBody.create(
                MediaType.parse("text/plain"),
                password),
            password2 = RequestBody.create(
                MediaType.parse("text/plain"),
                confirmPassword),
            mobile = RequestBody.create(
                MediaType.parse("text/plain"),
                mobile),
            license_key = RequestBody.create(
                MediaType.parse("text/plain"),
                license_key),
            address = RequestBody.create(
                MediaType.parse("text/plain"),
                address),
            profile_picture = null
        )

        if(response.response.equals("Can't send opt")){
            throw java.lang.Exception(response.errorMessage)
        }
        emit(
            DataState.data(response = Response(
                message = "Send Opt",
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Success()
            ), data = response))
    }.catch { e ->
        emit(com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException(e))
    }
}