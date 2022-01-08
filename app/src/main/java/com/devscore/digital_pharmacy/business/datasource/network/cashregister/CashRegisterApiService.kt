package com.devscore.digital_pharmacy.business.datasource.network.cashregister

import com.devscore.digital_pharmacy.business.datasource.network.cashregister.network_response.CreatePaymentResponse
import com.devscore.digital_pharmacy.business.datasource.network.cashregister.network_response.CreateReceiveResponse
import com.devscore.digital_pharmacy.business.domain.models.CreatePayment
import com.devscore.digital_pharmacy.business.domain.models.CreateReceive
import com.devscore.digital_pharmacy.business.domain.models.CreateSupplier
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