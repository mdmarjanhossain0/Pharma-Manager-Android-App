package com.appbytes.pharma_manager.business.datasource.network.sales

import com.appbytes.pharma_manager.business.datasource.network.GenericResponse
import com.appbytes.pharma_manager.business.datasource.network.inventory.network_responses.LocalMedicineResponse
import com.appbytes.pharma_manager.business.datasource.network.sales.network_response.CreateSalesOrderResponse
import com.appbytes.pharma_manager.business.datasource.network.sales.network_response.SalesOderListResponse
import com.appbytes.pharma_manager.business.datasource.network.sales.network_response.CreateSalesReturnResponse
import com.appbytes.pharma_manager.business.domain.models.CreateSalesOrder
import com.appbytes.pharma_manager.business.domain.models.CreateSalesReturn
import retrofit2.http.*

interface SalesApiService {


    @POST("sales/generate")
    suspend fun createSalesOder (
        @Header("Authorization") authorization: String,
        @Body createSalesOder: CreateSalesOrder
    ) : CreateSalesOrderResponse

    @POST("sales/returnorder")
    suspend fun createReturnOrder (
        @Header("Authorization") authorization: String,
        @Body createReturnOrder: CreateSalesReturn
    ) : CreateSalesReturnResponse


    @GET("sales/list")
    suspend fun searchSalesOder (
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("status") status : Int,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ) : SalesOderListResponse


    @PUT("sales/completed/{pk}")
    suspend fun salesCompleted (
        @Header("Authorization") authorization: String,
        @Path("pk") pk : Int,
        @Body createSalesOder: CreateSalesOrder
    ) : SalesOrderDto



    @DELETE("sales/delete/{pk}")
    suspend fun salesOrderDelete (
        @Header("Authorization") authorization: String,
        @Path("pk") pk : Int,
    ) : GenericResponse





    @GET("sales/details")
    suspend fun searchLocalMedicineList(
        @Header("Authorization") authorization: String,
        @Query("pk") pk: Int
    ) : LocalMedicineResponse


}