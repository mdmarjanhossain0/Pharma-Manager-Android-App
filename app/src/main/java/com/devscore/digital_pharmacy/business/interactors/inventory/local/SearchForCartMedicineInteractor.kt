package com.devscore.digital_pharmacy.business.interactors.inventory.local

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.*
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.inventory.searchLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.network.inventory.toLocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchForCartMedicineInteractor(
    private val service: InventoryApiService,
    private val cache: LocalMedicineDao,
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        action : String,
        page: Int
    ): Flow<DataState<List<LocalMedicine>>> = flow {
        emit(DataState.loading<List<LocalMedicine>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

//        val success = cache.searchLocalMedicine(
//            query = query,
//            action = action,
//            page = page
//        ).map { it.toLocalMedicine() }
//
//        emit(DataState.data(response = null, data = success))

        try{
            val medicines = service.searchLocalMedicine(
                "Token ${authToken.token}",
                query = query,
                action = action,
                page = page
            ).results.map {
                Log.d(TAG, "looping toLocalMedicine")
                it.toLocalMedicine()
            }

            for(medicine in medicines){
                try{
                    Log.d(TAG, "Caching size" + medicines.size.toString())
                    cache.insertLocalMedicine(medicine.toLocalMedicineEntity())
                    for (unit in medicine.toLocalMedicineUnitEntity()) {
                        cache.insertLocalMedicineUnit(unit)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(
                DataState.error<List<LocalMedicine>>(
                    response = Response(
                        message = "Unable to update the cache for network exception.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        val localMedicine = cache.searchLocalMedicine(
            query = query,
            action = action,
            page = page
        ).map { it.toLocalMedicine() }





        emit(DataState.data(response = null, data = localMedicine))
    }.catch { e ->
        emit(com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException(e))
    }
}