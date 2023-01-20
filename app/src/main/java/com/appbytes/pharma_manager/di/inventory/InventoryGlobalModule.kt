package com.appbytes.pharma_manager.di.inventory

import com.appbytes.pharma_manager.business.datasource.cache.AppDatabase
import com.appbytes.pharma_manager.business.datasource.cache.inventory.global.GlobalMedicineDao
import com.appbytes.pharma_manager.business.datasource.network.inventory.InventoryApiService
import com.appbytes.pharma_manager.business.interactors.inventory.global.SearchGlobalMedicine
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
object InventoryGlobalModule {


    @Singleton
    @Provides
    fun provideInventoryService(retrofitBuilder: Retrofit.Builder): InventoryApiService {
        return retrofitBuilder
            .build()
            .create(InventoryApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideGlobalMedicineDao(
        database: AppDatabase
    ) : GlobalMedicineDao {
        return database.getGlobalMedicineDao()
    }


    @Singleton
    @Provides
    fun provideSearchGlobalMedicine(
        service: InventoryApiService,
        globalMedicineDao: GlobalMedicineDao
    ) : SearchGlobalMedicine {
        return SearchGlobalMedicine(
            service = service,
            cache = globalMedicineDao
        )
    }
}