package com.appbytes.pharma_manager.di.shortlist

import com.appbytes.pharma_manager.business.datasource.cache.AppDatabase
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.LocalMedicineDao
import com.appbytes.pharma_manager.business.datasource.cache.shortlist.ShortListDao
import com.appbytes.pharma_manager.business.datasource.network.shortlist.ShortListApiService
import com.appbytes.pharma_manager.business.interactors.inventory.local.DeleteShortListInteractor
import com.appbytes.pharma_manager.business.interactors.shortlist.CreateFailureShortListInteractor
import com.appbytes.pharma_manager.business.interactors.shortlist.CreateShortList
import com.appbytes.pharma_manager.business.interactors.shortlist.SearchShortList
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
object ShortListModule {

    @Singleton
    @Provides
    fun provideShortListDao(
        database: AppDatabase
    ) : ShortListDao {
        return database.getShortListDao()
    }


    @Singleton
    @Provides
    fun provideShortListApiService (retrofitBuilder: Retrofit.Builder): ShortListApiService {
        return retrofitBuilder
            .build()
            .create(ShortListApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideSearchShortList (
        service : ShortListApiService,
        cache : ShortListDao,
        localMedicineDao : LocalMedicineDao
    ) : SearchShortList {
        return SearchShortList(
            service = service,
            cache = cache,
            localMedicineDao = localMedicineDao
        )
    }

    @Singleton
    @Provides
    fun provideCreateShortList (
        service : ShortListApiService,
        cache : ShortListDao,
        localMedicineDao : LocalMedicineDao
    ) : CreateShortList {
        return CreateShortList(
            service = service,
            cache = cache,
            localMedicineDao = localMedicineDao
        )
    }



    @Singleton
    @Provides
    fun provideDeleteShortList (
        service : ShortListApiService,
        cache : ShortListDao
    ) : DeleteShortListInteractor {
        return DeleteShortListInteractor(
            service = service,
            cache = cache
        )
    }




    @Singleton
    @Provides
    fun provideFailureCreateShortList (
        service : ShortListApiService,
        cache : ShortListDao,
        localMedicineDao : LocalMedicineDao
    ) : CreateFailureShortListInteractor {
        return CreateFailureShortListInteractor(
            service = service,
            cache = cache,
            localMedicineDao = localMedicineDao
        )
    }
}