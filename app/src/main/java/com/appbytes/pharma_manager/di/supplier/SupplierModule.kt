package com.appbytes.pharma_manager.di.supplier

import android.content.Context
import com.appbytes.pharma_manager.business.datasource.cache.AppDatabase
import com.appbytes.pharma_manager.business.datasource.cache.purchases.PurchasesDao
import com.appbytes.pharma_manager.business.datasource.cache.supplier.SupplierDao
import com.appbytes.pharma_manager.business.datasource.network.supplier.SupplierApiService
import com.appbytes.pharma_manager.business.interactors.supplier.*
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
object SupplierModule {

    @Singleton
    @Provides
    fun provideSupplierDao(
        database: AppDatabase
    ) : SupplierDao {
        return database.getSupplierDao()
    }


    @Singleton
    @Provides
    fun provideSupplierApiService(retrofitBuilder: Retrofit.Builder): SupplierApiService {
        return retrofitBuilder
            .build()
            .create(SupplierApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideSearchSupplier (
        service : SupplierApiService,
        cache : SupplierDao
    ) : SearchSupplier {
        return SearchSupplier(
            service = service,
            cache = cache
        )
    }

    @Singleton
    @Provides
    fun provideCreateSupplierInteractor (
        service : SupplierApiService,
        cache : SupplierDao
    ) : CreateSupplierInteractor {
        return CreateSupplierInteractor(
            service = service,
            cache = cache
        )
    }




    @Singleton
    @Provides
    fun provideUpdateSupplier (
        service : SupplierApiService,
        cache : SupplierDao
    ) : UpdateSupplierInteractor {
        return UpdateSupplierInteractor(
            service = service,
            cache = cache
        )
    }



    @Singleton
    @Provides
    fun provideFailureCreateSupplierInteractor (
        service : SupplierApiService,
        cache : SupplierDao,
        @ApplicationContext applicationContext : Context
    ) : CreateFailureSuppllierInteractor {
        return CreateFailureSuppllierInteractor(
            service = service,
            cache = cache,
            context = applicationContext
        )
    }


    @Singleton
    @Provides
    fun provideSupplerPreviousOrderInteractor (
        service : SupplierApiService,
        cache : PurchasesDao
    ) : SupplierPreviousOrderInteractor {
        return SupplierPreviousOrderInteractor(
            service = service,
            cache = cache
        )
    }

    @Singleton
    @Provides
    fun provideGetSupplierDetails (
        service : SupplierApiService,
        cache : SupplierDao
    ) : GetSupplierDetailsInteractor {
        return GetSupplierDetailsInteractor(
            service = service,
            cache = cache
        )
    }
}