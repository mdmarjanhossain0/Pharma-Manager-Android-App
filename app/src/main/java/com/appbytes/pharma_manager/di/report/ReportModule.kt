package com.appbytes.pharma_manager.di.report

import com.appbytes.pharma_manager.business.datasource.cache.sales.SalesDao
import com.appbytes.pharma_manager.business.datasource.network.report.ReportApiService
import com.appbytes.pharma_manager.business.interactors.report.GetMonthDetailsSales
import com.appbytes.pharma_manager.business.interactors.report.GetNotification
import com.appbytes.pharma_manager.business.interactors.report.SearchReport
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import javax.inject.Singleton

@FlowPreview
@Module
@InstallIn(SingletonComponent::class)
object ReportModule {

    @Singleton
    @Provides
    fun provideReportApiService(retrofitBuilder: Retrofit.Builder): ReportApiService {
        return retrofitBuilder
            .build()
            .create(ReportApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideSearchReport(
        service: ReportApiService
    ) : SearchReport {
        return SearchReport(
            service = service
        )
    }





    @Singleton
    @Provides
    fun provideSalesDetailsMonth (
        service: ReportApiService,
        cache : SalesDao
    ) : GetMonthDetailsSales {
        return GetMonthDetailsSales(
            service = service,
            cache = cache
        )
    }







    @Singleton
    @Provides
    fun provideGetNotification(
        service: ReportApiService
    ) : GetNotification {
        return GetNotification(
            service = service
        )
    }
}