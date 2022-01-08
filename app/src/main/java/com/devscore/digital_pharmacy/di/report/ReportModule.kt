package com.devscore.digital_pharmacy.di.report

import com.devscore.digital_pharmacy.business.datasource.cache.AppDatabase
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.report.ReportApiService
import com.devscore.digital_pharmacy.business.interactors.inventory.local.SearchLocalMedicine
import com.devscore.digital_pharmacy.business.interactors.report.GetMonthDetailsSales
import com.devscore.digital_pharmacy.business.interactors.report.GetNotification
import com.devscore.digital_pharmacy.business.interactors.report.SearchReport
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