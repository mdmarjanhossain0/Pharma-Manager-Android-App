package com.devscore.digital_pharmacy.business.interactors.supplier

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.SupplierDao
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.toSupplier
import com.devscore.digital_pharmacy.business.datasource.network.ExtractHTTPException
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.supplier.SupplierApiService
import com.devscore.digital_pharmacy.business.datasource.network.supplier.toSupplier
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.models.toSupplierEntity
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class SearchSupplier (
    private val service : SupplierApiService,
    private val cache : SupplierDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        page: Int
    ): Flow<DataState<List<Supplier>>> = flow {
        emit(DataState.loading<List<Supplier>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        val supplier = cache.searchAllSupplier(
            query = query,
            page = page
        ).map { it.toSupplier() }

        val failureSupplier = cache.searchAllFailureSupplier(
            query = query,
        ).map {
            it.toSupplier()
        }
        emit(DataState.data(response = null, data = marge(supplier, failureSupplier)))

        try{
            Log.d(TAG, "Call Api Section")
            val suppliersResponse = service.searchSupplier(
                "Token ${authToken.token}",
                query = query,
                page = page
            )
            Log.d(TAG, suppliersResponse.toString())

            val suppliers = suppliersResponse.results.map {
                Log.d(TAG, "looping Supplier")
                it.toSupplier()
            }

            Log.d(TAG, suppliers.toString())
            for(supplier in suppliers){
                try{
                    Log.d(TAG, "Caching size" + suppliers.size.toString())
                    Log.d(TAG, supplier.toString())
                    cache.insertSupplier(supplier.toSupplierEntity())
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            when (e) {
                is HttpException -> {
                    when (e.code()) {
                        401 ->{
                            Log.d(TAG, "401 Unauthorized " + e.response()?.errorBody().toString())
                            emit(DataState.loading<List<Supplier>>(isLoading = false))
                            ExtractHTTPException.getInstance().unauthorized()
                            return@flow
                        }
                    }
                }
            }
            emit(
                DataState.error<List<Supplier>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        val supplierList = cache.searchAllSupplier(
            query = query,
            page = page
        ).map { it.toSupplier() }

        val failureSupplierList = cache.searchAllFailureSupplier(
            query = query,
        ).map {
            it.toSupplier()
        }





        emit(DataState.data(response = null, data = marge(supplierList, failureSupplierList)))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}

fun marge(supplierList: List<Supplier>, failureSupplierLIst : List<Supplier>) : List<Supplier> {
    val allMedicine  = mutableListOf<Supplier>()
    allMedicine.addAll(supplierList)
    allMedicine.addAll(failureSupplierLIst)
    return allMedicine
}
