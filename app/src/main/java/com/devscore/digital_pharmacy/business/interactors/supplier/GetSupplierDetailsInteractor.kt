package com.devscore.digital_pharmacy.business.interactors.supplier

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.SupplierDao
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.toSupplier
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.supplier.SupplierApiService
import com.devscore.digital_pharmacy.business.datasource.network.supplier.network_response.toSupplier
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
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