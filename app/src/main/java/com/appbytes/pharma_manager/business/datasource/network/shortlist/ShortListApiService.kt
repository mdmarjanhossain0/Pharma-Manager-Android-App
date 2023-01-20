package com.appbytes.pharma_manager.business.datasource.network.shortlist

import com.appbytes.pharma_manager.business.datasource.network.GenericResponse
import com.appbytes.pharma_manager.business.datasource.network.shortlist.network_response.ShortListDto
import com.appbytes.pharma_manager.business.datasource.network.shortlist.network_response.ShortListNetworkResponse
import okhttp3.RequestBody
import retrofit2.http.*

interface ShortListApiService {


    @GET("inventory/shortlist")
    suspend fun searchShortList(
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("filter") filter : String,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ): ShortListNetworkResponse





    @Multipart
    @POST("inventory/addshortlist")
    suspend fun addShortList(
        @Header("Authorization") authorization: String,
        @Part("medicine") medicine : RequestBody,
    ) : ShortListDto




    @DELETE("inventory/shortlistdelete/{pk}")
    suspend fun deleteShortList(
        @Header("Authorization") authorization: String,
        @Path("pk") pk : Int
    ) : GenericResponse
}