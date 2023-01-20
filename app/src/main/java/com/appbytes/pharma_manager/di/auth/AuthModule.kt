package com.appbytes.pharma_manager.di.auth

import android.content.Context
import com.appbytes.pharma_manager.business.datasource.cache.account.AccountDao
import com.appbytes.pharma_manager.business.datasource.cache.auth.AuthTokenDao
import com.appbytes.pharma_manager.business.datasource.datastore.AppDataStore
import com.appbytes.pharma_manager.business.datasource.network.auth.AuthService
import com.appbytes.pharma_manager.business.interactors.auth.Login
import com.appbytes.pharma_manager.business.interactors.auth.Register
import com.appbytes.pharma_manager.business.interactors.auth.SendOtp
import com.appbytes.pharma_manager.business.interactors.session.CheckPreviousAuthUser
import com.appbytes.pharma_manager.business.interactors.session.Logout
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
object AuthModule{

    @Singleton
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): AuthService {
        return retrofitBuilder
            .build()
            .create(AuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideCheckPrevAuthUser(
        accountDao: AccountDao,
        authTokenDao: AuthTokenDao,
    ): CheckPreviousAuthUser {
        return CheckPreviousAuthUser(
            accountDao,
            authTokenDao
        )
    }

    @Singleton
    @Provides
    fun provideLogin(
        service: AuthService,
        accountDao: AccountDao,
        authTokenDao: AuthTokenDao,
        appDataStoreManager: AppDataStore,
        @ApplicationContext context : Context
    ): Login {
        return Login(
            service,
            accountDao,
            authTokenDao,
            appDataStoreManager,
            context
        )
    }

    @Singleton
    @Provides
    fun provideLogout(
        authTokenDao: AuthTokenDao,
    ): Logout {
        return Logout(authTokenDao)
    }

    @Singleton
    @Provides
    fun provideRegister(
        service: AuthService,
        accountDao: AccountDao,
        authTokenDao: AuthTokenDao,
        appDataStoreManager: AppDataStore,
    ): Register {
        return Register(
            service,
            accountDao,
            authTokenDao,
            appDataStoreManager
        )
    }


    @Singleton
    @Provides
    fun provideSendOtp(
        service: AuthService
    ): SendOtp {
        return SendOtp(
            service
        )
    }
}









