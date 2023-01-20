package com.appbytes.pharma_manager.di.account

import com.appbytes.pharma_manager.business.datasource.cache.AppDatabase
import com.appbytes.pharma_manager.business.datasource.cache.account.AccountDao
import com.appbytes.pharma_manager.business.datasource.cache.account.EmployeeDao
import com.appbytes.pharma_manager.business.datasource.cache.auth.AuthTokenDao
import com.appbytes.pharma_manager.business.datasource.datastore.AppDataStore
import com.appbytes.pharma_manager.business.datasource.network.account.AccountApiService
import com.appbytes.pharma_manager.business.datasource.network.auth.AuthService
import com.appbytes.pharma_manager.business.interactors.account.*
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
object AccountModule{

    @Singleton
    @Provides
    fun provideAccountApiService(retrofitBuilder: Retrofit.Builder): AccountApiService {
        return retrofitBuilder
            .build()
            .create(AccountApiService::class.java)
    }



    @Singleton
    @Provides
    fun provideEmployeeDao(database : AppDatabase): EmployeeDao {
        return database.getEmployeeDao()
    }


    @Singleton
    @Provides
    fun provideCreateEmployee (
        service : AccountApiService,
        cache : EmployeeDao
    ): CreateEmployee {
        return CreateEmployee(
            service = service,
            cache = cache
        )
    }

    @Singleton
    @Provides
    fun provideUpdateEmployee (
        service : AccountApiService,
        cache : EmployeeDao
    ): UpdateEmployee {
        return UpdateEmployee(
            service = service,
            cache = cache
        )
    }



    @Singleton
    @Provides
    fun provideEmployeeList (
        service : AccountApiService,
        cache : EmployeeDao
    ): EmployeeList {
        return EmployeeList(
            service = service,
            cache = cache
        )
    }


    @Singleton
    @Provides
    fun provideGetAccount (
        service : AuthService,
        cache : AccountDao,
        authTokenDao: AuthTokenDao,
        appDataStore : AppDataStore
    ): GetAccount {
        return GetAccount(
            service = service,
            cache = cache,
            authTokenDao = authTokenDao,
            appDataStore
        )
    }


    @Singleton
    @Provides
    fun provideUpdatePassword (
        service : AccountApiService
    ): UpdatePassword {
        return UpdatePassword(
            service = service
        )
    }




    @Singleton
    @Provides
    fun provideAccountUpdate (
        service : AccountApiService,
        cache : AccountDao
    ): AccountUpdate {
        return AccountUpdate(
            service = service,
            accountDao = cache
        )
    }


    @Singleton
    @Provides
    fun provideGetEmployee (
        service : AccountApiService,
        cache : EmployeeDao
    ): GetEmployee {
        return GetEmployee(
            service = service,
            cache = cache
        )
    }
}