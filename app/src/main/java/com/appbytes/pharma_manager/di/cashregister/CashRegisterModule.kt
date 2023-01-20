package com.appbytes.pharma_manager.di.cashregister

import com.appbytes.pharma_manager.business.datasource.cache.AppDatabase
import com.appbytes.pharma_manager.business.datasource.cache.cashregister.PaymentDao
import com.appbytes.pharma_manager.business.datasource.cache.cashregister.ReceiveDao
import com.appbytes.pharma_manager.business.datasource.network.cashregister.CashRegisterApiService
import com.appbytes.pharma_manager.business.interactors.cashregister.CreateFailurePaymentInteractor
import com.appbytes.pharma_manager.business.interactors.cashregister.CreateFailureReceiveInteractor
import com.appbytes.pharma_manager.business.interactors.cashregister.CreatePaymentInteractor
import com.appbytes.pharma_manager.business.interactors.cashregister.CreateReceiveInteractor
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
object CashRegisterModule {

    @Singleton
    @Provides
    fun provideReceiveDao (
        database: AppDatabase
    ) : ReceiveDao {
        return database.getReceiveDao()
    }


    @Singleton
    @Provides
    fun providePaymentDao (
        database: AppDatabase
    ) : PaymentDao {
        return database.getPaymentDao()
    }


    @Singleton
    @Provides
    fun provideSupplierApiService(retrofitBuilder: Retrofit.Builder): CashRegisterApiService {
        return retrofitBuilder
            .build()
            .create(CashRegisterApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideCreateReceiveInteractor (
        service : CashRegisterApiService,
        cache : ReceiveDao
    ) : CreateReceiveInteractor {
        return CreateReceiveInteractor(
            service = service,
            cache = cache
        )
    }


    @Singleton
    @Provides
    fun provideCreatePaymentInteractor (
        service : CashRegisterApiService,
        cache : PaymentDao
    ) : CreatePaymentInteractor {
        return CreatePaymentInteractor(
            service = service,
            cache = cache
        )
    }




    @Singleton
    @Provides
    fun provideFailureCreateReceive (
        service : CashRegisterApiService,
        cache : ReceiveDao
    ) : CreateFailureReceiveInteractor {
        return CreateFailureReceiveInteractor(
            service = service,
            cache = cache
        )
    }


    @Singleton
    @Provides
    fun provideFailureCreatePayment (
        service : CashRegisterApiService,
        cache : PaymentDao
    ) : CreateFailurePaymentInteractor {
        return CreateFailurePaymentInteractor(
            service = service,
            cache = cache
        )
    }
}