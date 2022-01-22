package com.devscore.digital_pharmacy.di.sales

import android.content.Context
import com.devscore.digital_pharmacy.business.datasource.cache.AppDatabase
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.interactors.sales.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import javax.inject.Singleton

@FlowPreview
@Module
@InstallIn(SingletonComponent::class)
object SalesModule {

    @Singleton
    @Provides
    fun provideSalesDao(
        database: AppDatabase
    ) : SalesDao {
        return database.getSalesDao()
    }


    @Singleton
    @Provides
    fun provideSalesApiService (retrofitBuilder: Retrofit.Builder): SalesApiService {
        return retrofitBuilder
            .build()
            .create(SalesApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideSearchSales (
        service : SalesApiService,
        cache : SalesDao,
        localMedicineDao: LocalMedicineDao
    ) : SearchSalesOder {
        return SearchSalesOder(
            service = service,
            cache = cache,
            localMedicineDao = localMedicineDao
        )
    }

    @Singleton
    @Provides
    fun provideCreateSalesOrderInteractor (
        service : SalesApiService,
        cache : SalesDao,
        localMedicineDao: LocalMedicineDao
    ) : CreateSalesOderInteractor {
        return CreateSalesOderInteractor(
            service = service,
            cache = cache,
            localMedicineDao = localMedicineDao
        )
    }

    @Singleton
    @Provides
    fun provideFailureCreateSalesOrderInteractor (
        service : SalesApiService,
        cache : SalesDao,
        @ApplicationContext applicationContext: Context
    ) : CreateFailureSalesInteractor {
        return CreateFailureSalesInteractor(
            service = service,
            cache = cache,
            context = applicationContext
        )
    }


    @Singleton
    @Provides
    fun provideSalesCompleted (
        service : SalesApiService,
        cache : SalesDao
    ) : SalesCompleted {
        return SalesCompleted(
            service = service,
            cache = cache
        )
    }



    @Singleton
    @Provides
    fun provideSalesOrderDetails (
        service : SalesApiService,
        cache : SalesDao,
        localCache : LocalMedicineDao
    ) : SalesOrderDetailsInteractor {
        return SalesOrderDetailsInteractor(
            service = service,
            cache = cache,
            localCache = localCache
        )
    }


    @Singleton
    @Provides
    fun provideSalesReturnInteractor (
        service : SalesApiService
    ) : SalesReturnInteractor {
        return SalesReturnInteractor(
            service = service
        )
    }

    @Singleton
    @Provides
    fun provideDeleteSalesOrder (
        service : SalesApiService,
        cache : SalesDao
    ) : DeleteSalesOrderInteractor {
        return DeleteSalesOrderInteractor(
            service = service,
            cache = cache
        )
    }

    @Singleton
    @Provides
    fun provideSalesOrderLocalDetailsInteractor (
        service : SalesApiService,
        cache : SalesDao
    ) : SalesOrderLocalDetailsInteractor {
        return SalesOrderLocalDetailsInteractor(
            service = service,
            cache = cache
        )
    }
}