package com.appbytes.pharma_manager.business.datasource.network.supplier

import com.appbytes.pharma_manager.business.datasource.network.purchases.network_response.PurchasesOderListResponse
import com.appbytes.pharma_manager.business.datasource.network.supplier.network_response.SupplierCreateResponse
import com.appbytes.pharma_manager.business.datasource.network.supplier.network_response.SupplierListResponse
import com.appbytes.pharma_manager.business.domain.models.CreateSupplier
import retrofit2.http.*

interface SupplierApiService {


    @POST("account/createsupplier")
    suspend fun createSupplier (
        @Header("Authorization") authorization: String,
        @Body createSupplier : CreateSupplier
    ) : SupplierCreateResponse



    @PUT("account/supplier/update/{pk}")
    suspend fun updateSupplier (
        @Header("Authorization") authorization: String,
        @Path("pk") pk : Int,
        @Body createSupplier : CreateSupplier
    ) : SupplierCreateResponse


    @GET("account/supplierlist")
    suspend fun searchSupplier (
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("page") page: Int
    ) : SupplierListResponse


    @GET("account/supplierpreviousoderlist/{pk}")
    suspend fun searchSupplierOrderList (
        @Header("Authorization") authorization: String,
        @Path("pk") pk : Int,
        @Query("status") status : Int = 3,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ) : PurchasesOderListResponse

}