package com.appbytes.pharma_manager.di.inventory

import android.content.Context
import com.appbytes.pharma_manager.business.datasource.cache.inventory.global.GlobalMedicineDao
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.LocalMedicineDao
import com.appbytes.pharma_manager.business.datasource.network.inventory.InventoryApiService
import com.appbytes.pharma_manager.business.interactors.inventory.*
import com.appbytes.pharma_manager.business.interactors.inventory.local.AddFailureMedicineInteractor
import com.appbytes.pharma_manager.business.interactors.inventory.local.AddMedicineInteractor
import com.appbytes.pharma_manager.business.interactors.inventory.local.FetchLocalMedicineData
import com.appbytes.pharma_manager.business.interactors.inventory.local.UpdateMedicineInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@Module
@InstallIn(SingletonComponent::class)
object InventoryAddMedicineModule {


    @Singleton
    @Provides
    fun provideAddMedicineInteractior(
        service: InventoryApiService,
        localMedicineDao : LocalMedicineDao,
        @ApplicationContext applicationContext: Context
    ) : AddMedicineInteractor {
        return AddMedicineInteractor(
            service = service,
            cache = localMedicineDao,
            applicationContext
        )
    }

    @Singleton
    @Provides
    fun provideFailureAddMedicineInteractior(
        service: InventoryApiService,
        localMedicineDao : LocalMedicineDao,
        @ApplicationContext applicationContext: Context
    ) : AddFailureMedicineInteractor {
        return AddFailureMedicineInteractor(
            service = service,
            cache = localMedicineDao,
            applicationContext
        )
    }

    @Singleton
    @Provides
    fun provideFetchGlobalMedicineData (
        service: InventoryApiService,
        cache : GlobalMedicineDao
    ) : FetchGlobalMedicineData {
        return FetchGlobalMedicineData(
            service = service,
            cache = cache,
        )
    }

    @Singleton
    @Provides
    fun provideUpdateMedicine (
        service: InventoryApiService,
        localMedicineDao : LocalMedicineDao,
        @ApplicationContext applicationContext: Context
    ) : UpdateMedicineInteractor {
        return UpdateMedicineInteractor(
            service = service,
            cache = localMedicineDao,
            applicationContext
        )
    }


    @Singleton
    @Provides
    fun provideLocalMedicineData (
        service: InventoryApiService,
        cache : LocalMedicineDao
    ) : FetchLocalMedicineData {
        return FetchLocalMedicineData(
            service = service,
            cache = cache,
        )
    }
}