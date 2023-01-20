package com.appbytes.pharma_manager.business.interactors.supplier

import com.appbytes.pharma_manager.business.datasource.cache.supplier.SupplierDao
import com.appbytes.pharma_manager.business.datasource.cache.supplier.toSupplier
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.supplier.SupplierApiService
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetSupplierDetailsInteractor (
    private val service : SupplierApiService,
    private val cache : SupplierDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int
    ): Flow<DataState<Supplier>> = flow {

        emit(DataState.loading<Supplier>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        val supplier = cache.getSupplier(pk = pk)?.toSupplier()

        emit(DataState.data(response = null, data = supplier))


    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}