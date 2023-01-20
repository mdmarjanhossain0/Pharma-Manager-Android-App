package com.appbytes.pharma_manager.business.datasource.network.auth

import com.appbytes.pharma_manager.business.datasource.network.GenericResponse
import com.appbytes.pharma_manager.business.datasource.network.auth.network_responses.RegistrationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface AuthService {

    @POST("account/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): RegistrationResponse

    @GET("account/properties")
    suspend fun getAccount(
        @Header("Authorization") authorization : String
    ): RegistrationResponse




    @Multipart
    @POST("account/register")
    suspend fun register(
        @Part("email") email: RequestBody,
        @Part("shop_name") shop_name : RequestBody,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("password2") password2: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("license_key") license_key: RequestBody?,
        @Part("address") address: RequestBody,
        @Part profile_picture : MultipartBody.Part?
    ): RegistrationResponse


    @Multipart
    @POST("account/register")
    suspend fun sendOpt(
        @Part("email") email: RequestBody,
        @Part("shop_name") shop_name : RequestBody,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("password2") password2: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("license_key") license_key: RequestBody?,
        @Part("address") address: RequestBody,
        @Part profile_picture : MultipartBody.Part?
    ): GenericResponse


    @POST("sales/odermedicine")
    suspend fun testpost(
        @Header("Authorization") authorization : String,
        @Body data : Oder
    ): String


    data class Oder(
        val oderItem : OderItem,
        val customer_id : Int = -1
    )

    data class OderItem(
        val quantity : Int,
        val medicine_id : Int
    )
    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): ResponseBody
}
