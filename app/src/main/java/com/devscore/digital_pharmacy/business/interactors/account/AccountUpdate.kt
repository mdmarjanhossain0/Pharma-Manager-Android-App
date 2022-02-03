package com.devscore.digital_pharmacy.business.interactors.account

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.account.AccountDao
import com.devscore.digital_pharmacy.business.datasource.cache.account.toEntity
import com.devscore.digital_pharmacy.business.datasource.cache.auth.AuthTokenDao
import com.devscore.digital_pharmacy.business.datasource.cache.auth.toEntity
import com.devscore.digital_pharmacy.business.datasource.datastore.AppDataStore
import com.devscore.digital_pharmacy.business.datasource.network.account.AccountApiService
import com.devscore.digital_pharmacy.business.datasource.network.auth.AuthService
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.Account
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.util.DataState
import com.devscore.digital_pharmacy.business.domain.util.ErrorHandling
import com.devscore.digital_pharmacy.presentation.util.DataStoreKeys
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

val TAG = "AppDebug"
class AccountUpdate(
    private val service: AccountApiService,
    private val accountDao: AccountDao
){
    fun execute(
        authToken: AuthToken? = null,
        username: String,
        profile_picture : String?,
        mobile : String,
        license_key : String,
        address : String
    ): Flow<DataState<Account>> = flow {
        emit(DataState.loading<Account>())





        var multipartBody: MultipartBody.Part? = null
        try {
            val imageFile = java.io.File(profile_picture)
            if(imageFile.exists()) {
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
        }
        catch (e : Exception) {
            e.printStackTrace()
        }

        val registerResponse = service.update(
            authorization = "Token ${authToken?.token}",
            username = RequestBody.create(MediaType.parse("text/plain"), username),
            mobile = RequestBody.create(MediaType.parse("text/plain"), mobile),
            license_key = RequestBody.create(MediaType.parse("text/plain"), license_key),
            address = RequestBody.create(MediaType.parse("text/plain"), address),
            profile_picture = multipartBody
        )

        if(registerResponse.response.equals(ErrorHandling.GENERIC_AUTH_ERROR)){
            throw Exception(registerResponse.errorMessage)
        }


        val account = Account(
            registerResponse.pk,
            registerResponse.email,
            registerResponse.shop_name,
            registerResponse.username,
            registerResponse.profile_picture!!,
            registerResponse.mobile,
            registerResponse.license_key,
            registerResponse.address,
            registerResponse.is_employee,
            registerResponse.role,
            registerResponse.file
        )
        accountDao.insertAndReplace(
            account.toEntity()
        )

        emit(DataState.data(data = account, response = null))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}






private fun extractHttpExceptions(ex: HttpException){
    Log.d(TAG, ex.toString())
    val body = ex.response()?.errorBody()
    val gson = GsonBuilder().create()
    val responseBody = gson.fromJson(body.toString(), JsonObject::class.java)
//    val errorEntity = gson.fromJson(responseBody, HttpErrorEntity::class.java)
    when (ex.code()) {
        400 ->
            Log.d(TAG, "400 Bad Request " + responseBody.toString())
        500 ->
            Log.d(TAG, "500 Bad Request " + responseBody.toString())
        401 ->
            Log.d(TAG, "401 Bad Request " + responseBody.toString())

        404 ->
            Log.d(TAG, "404 Bad Request " + responseBody.toString())

        else ->
            Log.d(TAG, "else Bad Request " + responseBody.toString())

    }
}