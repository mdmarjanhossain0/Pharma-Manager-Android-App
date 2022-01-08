package com.devscore.digital_pharmacy.di.inventory

import com.devscore.digital_pharmacy.business.datasource.cache.AppDatabase
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.GlobalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.interactors.inventory.global.SearchGlobalMedicine
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