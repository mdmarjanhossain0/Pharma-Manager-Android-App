package com.devscore.digital_pharmacy.business.interactors.auth

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.account.AccountDao
import com.devscore.digital_pharmacy.business.datasource.cache.account.toEntity
import com.devscore.digital_pharmacy.business.datasource.cache.auth.AuthTokenDao
import com.devscore.digital_pharmacy.business.datasource.cache.auth.toEntity
import com.devscore.digital_pharmacy.business.datasource.datastore.AppDataStore
import com.devscore.digital_pharmacy.business.datasource.network.GenericResponse
import com.devscore.digital_pharmacy.business.datasource.network.auth.AuthService
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.Account
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.business.domain.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.devscore.digital_pharmacy.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class Register(
    private val service: AuthService,
    private val accountDao: AccountDao,
    private val authTokenDao: AuthTokenDao,
    private val appDataStoreManager: AppDataStore,
){
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





        var multipartBody: MultipartBody.Part? = null
        try {
            val imageFile = java.io.File(image)
            if (imageFile.exists()) {
                val requestBody =
                    RequestBody.create(
                        MediaType.parse("image/jpeg"),
                        imageFile
                    )
                multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestBody
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AppDebug", "Handle File Process Exception")
        }


        val registerResponse = service.register(
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
            profile_picture = multipartBody
        )
        // Incorrect login credentials counts as a 200 response from server, so need to handle that
        if(registerResponse.response.equals(ErrorHandling.GENERIC_AUTH_ERROR)){
            throw Exception(registerResponse.errorMessage)
        }

        // cache account information
//        accountDao.insertAndReplace(
//            Account(
//                registerResponse.pk,
//                registerResponse.email,
//                registerResponse.shop_name,
//                registerResponse.username,
//                registerResponse.profile_picture,
//                registerResponse.mobile.toString(),
//                registerResponse.license_key,
//                registerResponse.address,
//                registerResponse.is_employee,
//                registerResponse.role
//            ).toEntity()
//        )

        // cache the auth token
//        val authToken = AuthToken(
//            registerResponse.pk,
//            registerResponse.token
//        )
//        val result = authTokenDao.insert(authToken.toEntity())
//        // can't proceed unless token can be cached
//        if(result < 0){
//            throw Exception(ERROR_SAVE_AUTH_TOKEN)
//        }
        // save authenticated user to datastore for auto-login next time
//        appDataStoreManager.setValue(DataStoreKeys.PREVIOUS_AUTH_USER, email)
        emit(DataState.data(
            response = Response(
                message = "Successfully Created.",
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Success()
            ),
            data = GenericResponse(
            response = "Successfully create account. Please check your email",
            errorMessage = null
        )))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}














