package com.appbytes.pharma_manager.di.customer

import android.content.Context
import com.appbytes.pharma_manager.business.datasource.cache.AppDatabase
import com.appbytes.pharma_manager.business.datasource.cache.customer.CustomerDao
import com.appbytes.pharma_manager.business.datasource.cache.sales.SalesDao
import com.appbytes.pharma_manager.business.datasource.network.customer.CustomerApiService
import com.appbytes.pharma_manager.business.interactors.customer.*
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
object CustomerModule {

    @Singleton
    @Provides
    fun provideCustomerDao (
        database: AppDatabase
    ) : CustomerDao {
        return database.getCustomerDao()
    }


    @Singleton
    @Provides
    fun provideCustomerApiService (retrofitBuilder: Retrofit.Builder): CustomerApiService {
        return retrofitBuilder
            .build()
            .create(CustomerApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideSearchCustomer (
        service : CustomerApiService,
        cache : CustomerDao
    ) : SearchCustomer {
        return SearchCustomer(
            service = service,
            cache = cache
        )
    }

    @Singleton
    @Provides
    fun provideCustomerInteractor (
        service: CustomerApiService,
        cache: CustomerDao
    ) : CreateCustomerInteractor {
        return CreateCustomerInteractor(
            service = service,
            cache = cache
        )
    }

    @Singleton
    @Provides
    fun provideUpdateCustomer (
        service: CustomerApiService,
        cache: CustomerDao
    ) : UpdateCustomerInteractor {
        return UpdateCustomerInteractor(
            service = service,
            cache = cache
        )
    }


    @Singleton
    @Provides
    fun provideFailureCustomerInteractor (
        service: CustomerApiService,
        cache: CustomerDao,
        @ApplicationContext applicationContext : Context
    ) : CreateFailureCustomerInteractor {
        return CreateFailureCustomerInteractor(
            service = service,
            cache = cache,
            context = applicationContext
        )
    }

    @Singleton
    @Provides
    fun provideCustomerPreviousOrdersInteractor (
        service: CustomerApiService,
        cache: SalesDao
    ) : CustomerPreviousOrderInteractor {
        return CustomerPreviousOrderInteractor(
            service = service,
            cache = cache
        )
    }

    @Singleton
    @Provides
    fun provideCustomerDetails (
        service: CustomerApiService,
        cache: CustomerDao
    ) : GetCustomerDetails {
        return GetCustomerDetails(
            service = service,
            cache = cache
        )
    }
}