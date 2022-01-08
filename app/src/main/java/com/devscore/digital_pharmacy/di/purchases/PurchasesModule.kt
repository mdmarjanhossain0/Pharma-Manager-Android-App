package com.devscore.digital_pharmacy.di.purchases

import android.content.Context
import com.devscore.digital_pharmacy.business.datasource.cache.AppDatabase
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.PurchasesDao
import com.devscore.digital_pharmacy.business.datasource.network.purchases.PurchasesApiService
import com.devscore.digital_pharmacy.business.interactors.purchases.*
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
object PurchasesModule {

    @Singleton
    @Provides
    fun providePurchasesDao (
        database: AppDatabase
    ) : PurchasesDao {
        return database.getPurchasesDao()
    }


    @Singleton
    @Provides
    fun providePurchasesApiService (retrofitBuilder: Retrofit.Builder): PurchasesApiService {
        return retrofitBuilder
            .build()
            .create(PurchasesApiService::class.java)
    }


    @Singleton
    @Provides
    fun providePurchasesSearch (
        service : PurchasesApiService,
        cache : PurchasesDao
    ) : SearchPurchasesOrder {
        return SearchPurchasesOrder(
            service = service,
            cache = cache
        )
    }

    @Singleton
    @Provides
    fun provideCreatePurchasesOrder (
        service : PurchasesApiService,
        cache : PurchasesDao,
        @ApplicationContext applicationContext : Context
    ) : CreatePurchasesOrderInteractor {
        return CreatePurchasesOrderInteractor(
            service = service,
            cache = cache,
            context = applicationContext
        )
    }

    @Singleton
    @Provides
    fun provideFailureCreatePurchasesOrder (
        service : PurchasesApiService,
        cache : PurchasesDao,
        @ApplicationContext applicationContext : Context
    ) : CreateFailurePurchasesOrderInteractor {
        return CreateFailurePurchasesOrderInteractor(
            service = service,
            cache = cache,
            context = applicationContext
        )
    }

    @Singleton
    @Provides
    fun providePurchasesCompleted (
        service : PurchasesApiService,
        cache : PurchasesDao
    ) : PurchasesCompleted {
        return PurchasesCompleted(
            service = service,
            cache = cache
        )
    }






    @Singleton
    @Provides
    fun providePurchasesOrderDetails (
        service : PurchasesApiService,
        cache : PurchasesDao,
        localCache : LocalMedicineDao
    ) : PurchasesOrderDetailsInteractor {
        return PurchasesOrderDetailsInteractor(
            service = service,
            cache = cache,
            localCache = localCache
        )
    }


    @Singleton
    @Provides
    fun providePurchasesReturnInteractor (
        service : PurchasesApiService
    ) : PurchasesReturnInteractor {
        return PurchasesReturnInteractor(
            service = service
        )
    }





    @Singleton
    @Provides
    fun provideDeletePurchasesOrder (
        service : PurchasesApiService,
        cache : PurchasesDao
    ) : DeletePurchasesOrderInteractor {
        return DeletePurchasesOrderInteractor(
            service = service,
            cache = cache
        )
    }
}