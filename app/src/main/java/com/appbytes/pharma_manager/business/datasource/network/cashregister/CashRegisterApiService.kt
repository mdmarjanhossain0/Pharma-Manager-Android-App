package com.appbytes.pharma_manager.business.datasource.network.cashregister

import com.appbytes.pharma_manager.business.datasource.network.cashregister.network_response.CreatePaymentResponse
import com.appbytes.pharma_manager.business.datasource.network.cashregister.network_response.CreateReceiveResponse
import com.appbytes.pharma_manager.business.domain.models.CreatePayment
import com.appbytes.pharma_manager.business.domain.models.CreateReceive
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CashRegisterApiService {


    @POST("payments/addreceive")
    suspend fun createReceive (
        @Header("Authorization") authorization: String,
        @Body createReceive : CreateReceive
    ): CreateReceiveResponse



    @POST("payments/addpayment")
    suspend fun createPayment (
        @Header("Authorization") authorization: String,
        @Body createPayment: CreatePayment
    ): CreatePaymentResponse


}