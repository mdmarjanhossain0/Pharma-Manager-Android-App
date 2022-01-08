package com.devscore.digital_pharmacy.di.cashregister

import com.devscore.digital_pharmacy.business.datasource.cache.AppDatabase
import com.devscore.digital_pharmacy.business.datasource.cache.cashregister.PaymentDao
import com.devscore.digital_pharmacy.business.datasource.cache.cashregister.ReceiveDao
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.SupplierDao
import com.devscore.digital_pharmacy.business.datasource.network.cashregister.CashRegisterApiService
import com.devscore.digital_pharmacy.business.datasource.network.supplier.SupplierApiService
import com.devscore.digital_pharmacy.business.interactors.cashregister.CreateFailurePaymentInteractor
import com.devscore.digital_pharmacy.business.interactors.cashregister.CreateFailureReceiveInteractor
import com.devscore.digital_pharmacy.business.interactors.cashregister.CreatePaymentInteractor
import com.devscore.digital_pharmacy.business.interactors.cashregister.CreateReceiveInteractor
import com.devscore.digital_pharmacy.business.interactors.supplier.CreateSupplierInteractor
import com.devscore.digital_pharmacy.business.interactors.supplier.SearchSupplier
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