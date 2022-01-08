package com.devscore.digital_pharmacy.business.datasource.network.purchases

import com.devscore.digital_pharmacy.business.datasource.network.GenericResponse
import com.devscore.digital_pharmacy.business.datasource.network.inventory.network_responses.LocalMedicineResponse
import com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response.CreatePurchasesOderResponse
import com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response.CreatePurchasesReturnResponse
import com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response.PurchasesOderListResponse
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.CreateSalesReturnResponse
import com.devscore.digital_pharmacy.business.domain.models.CreatePurchasesOder
import com.devscore.digital_pharmacy.business.domain.models.CreatePurchasesReturn
import com.devscore.digital_pharmacy.business.domain.models.CreateSalesReturn
import retrofit2.http.*

interface PurchasesApiService {


    @POST("purchases/purchasesoder")
    suspend fun createPurchasesOder (
        @Header("Authorization") authorization: String,
        @Body createPurchasesOder : CreatePurchasesOder
    ) : CreatePurchasesOderResponse




    @POST("purchases/returnorder")
    suspend fun createReturnOrder (
        @Header("Authorization") authorization: String,
        @Body createReturnOrder: CreatePurchasesReturn
    ) : CreatePurchasesReturnResponse


    @GET("purchases/purchaseslist")
    suspend fun searchPurchasesOder (
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("status") status : Int,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ) : PurchasesOderListResponse


    @PUT("purchases/statuscompleted/{pk}")
    suspend fun purchasesCompleted (
        @Header("Authorization") authorization: String,
        @Path("pk") pk : Int,
        @Body createPurchasesOder: CreatePurchasesOder
    ) : PurchasesOrderDto




    @DELETE("purchases/orderdelete/{pk}")
    suspend fun purchasesOrderDelete (
        @Header("Authorization") authorization: String,
        @Path("pk") pk : Int,
    ) : GenericResponse



    @GET("purchases/details")
    suspend fun searchLocalMedicineList(
        @Header("Authorization") authorization: String,
        @Query("pk") pk: Int
    ) : LocalMedicineResponse
}