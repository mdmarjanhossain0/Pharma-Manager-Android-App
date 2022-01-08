package com.devscore.digital_pharmacy.business.datasource.network.report

import com.devscore.digital_pharmacy.business.datasource.network.inventory.network_responses.GlobalMedicineResponse
import com.devscore.digital_pharmacy.business.datasource.network.report.network_response.NotificationResponse
import com.devscore.digital_pharmacy.business.datasource.network.report.network_response.ReportResponse
import com.devscore.digital_pharmacy.business.datasource.network.report.network_response.SalesDetailsMonthResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReportApiService {



    @GET("payments/report")
    suspend fun searchReport (
        @Header("Authorization") authorization: String,
        @Query("query") query : String? =  null,
        @Query("start") start : String? = null,
        @Query("end") end : String? = null,
        @Query("page") page: Int
    ) : ReportResponse


    @GET("payments/monthdetails")
    suspend fun monthDetailsSales (
        @Header("Authorization") authorization: String
    ) : SalesDetailsMonthResponse



    @GET("notifications/notification")
    suspend fun notification (
        @Header("Authorization") authorization: String
    ) : NotificationResponse
}