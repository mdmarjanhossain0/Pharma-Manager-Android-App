package com.devscore.digital_pharmacy.business.datasource.network.customer

import com.devscore.digital_pharmacy.business.datasource.network.customer.network_response.CustomerCreateResponse
import com.devscore.digital_pharmacy.business.datasource.network.customer.network_response.CustomerListResponse
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.SalesOderListResponse
import com.devscore.digital_pharmacy.business.datasource.network.supplier.network_response.SupplierCreateResponse
import com.devscore.digital_pharmacy.business.datasource.network.supplier.network_response.SupplierListResponse
import com.devscore.digital_pharmacy.business.domain.models.CreateCustomer
import com.devscore.digital_pharmacy.business.domain.models.CreateSupplier
import retrofit2.http.*

interface CustomerApiService {


    @POST("account/createcustomer")
    suspend fun createCustomer (
        @Header("Authorization") authorization: String,
        @Body createCustomer : CreateCustomer
    ) : CustomerCreateResponse

    @PUT("account/customer/update/{pk}")
    suspend fun updateCustomer (
        @Header("Authorization") authorization: String,
        @Path("pk") pk : Int,
        @Body createCustomer : CreateCustomer
    ) : CustomerCreateResponse


    @GET("account/customerlist")
    suspend fun searchCustomer (
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ) : CustomerListResponse







    @GET("account/customerpreviousoderlist/{pk}")
    suspend fun customerOrderList (
        @Header("Authorization") authorization: String,
        @Path("pk") pk : Int,
        @Query("status") status : Int = 3,
        @Query("page") page: Int
    ) : SalesOderListResponse

}