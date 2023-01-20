package com.appbytes.pharma_manager.business.datasource.network.account

import com.appbytes.pharma_manager.business.datasource.network.GenericResponse
import com.appbytes.pharma_manager.business.datasource.network.account.network_response.EmployeeDto
import com.appbytes.pharma_manager.business.datasource.network.account.network_response.EmployeeResponse
import com.appbytes.pharma_manager.business.datasource.network.auth.network_responses.RegistrationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface AccountApiService {

    @POST("account/createemployee")
    @FormUrlEncoded
    suspend fun createEmployee (
        @Header("Authorization") authorization: String,
        @Field("email") email: String = "abc1@gmail.com",
        @Field("username") username: String = "a",
        @Field("password") password: String = "adminpassword",
        @Field("password2") password2: String = "adminpassword",
        @Field("mobile") mobile: String = "54354",
        @Field("address") address: String = "BD",
        @Field("role") role : String,
        @Field("is_active") is_active : Boolean
    ): EmployeeDto

    @PUT("account/employeelist/{pk}/update")
    @FormUrlEncoded
    suspend fun updateEmployee (
        @Header("Authorization") authorization: String,
        @Path("pk") pk : Int,
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("mobile") mobile: String,
        @Field("address") address: String,
        @Field("role") role : String?,
        @Field("is_active") is_active : Boolean
    ): EmployeeDto


    @GET("account/employeelist")
    suspend fun getEmployeeList (
        @Header("Authorization") authorization : String
    ): EmployeeResponse

    @GET("account/employeelist/{pk}")
    suspend fun getEmployee (
        @Header("Authorization") authorization : String,
        @Path("pk") pk : Int
    ): EmployeeDto



    @PUT("account/changepassword")
    @FormUrlEncoded
    suspend fun updatePassword(
        @Header("Authorization") authorization: String,
        @Field("old_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirmNewPassword: String
    ): GenericResponse




    @PUT("account/properties/update")
    @Multipart
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Part profile_picture: MultipartBody.Part?,
        @Part("username") username: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("license_key") license_key: RequestBody,
        @Part("address") address: RequestBody
    ): RegistrationResponse
}