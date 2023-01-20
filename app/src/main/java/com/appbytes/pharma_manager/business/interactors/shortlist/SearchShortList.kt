package com.appbytes.pharma_manager.business.interactors.shortlist

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.*
import com.appbytes.pharma_manager.business.datasource.cache.shortlist.ShortListDao
import com.appbytes.pharma_manager.business.datasource.network.ExtractHTTPException
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.shortlist.ShortListApiService
import com.appbytes.pharma_manager.business.datasource.network.shortlist.network_response.toShortList
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class SearchShortList (
    private val service : ShortListApiService,
    private val cache : ShortListDao,
    private val localMedicineDao : LocalMedicineDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        filter : String,
        page: Int
    ): Flow<DataState<List<ShortList>>> = flow {
        emit(DataState.loading<List<ShortList>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }


        Log.d(TAG, "Filter " + filter + "Filter Data")

        try{
            Log.d(TAG, "Call Api Section")
            val shortList = service.searchShortList(
                "Token ${authToken.token}",
                query = query,
                filter = filter,
                page = page
            ).results.map {
                Log.d(TAG, "looping Customer")
                it.toShortList()
            }

            Log.d(TAG, shortList.toString())
            for(item in shortList){
                localMedicineDao.insertLocalMedicine(item.medicine.toLocalMedicineEntity())
                for (unit in item.medicine.toLocalMedicineUnitEntity()) {
                    localMedicineDao.insertLocalMedicineUnit(unit)
                }


                try{
                    Log.d(TAG, "Caching size" + shortList.size.toString())
                    Log.d(TAG, item.toString())
                    cache.insertShortList(item.toShortListEntity())
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
                            emit(DataState.loading<List<ShortList>>(isLoading = false))
                            ExtractHTTPException.getInstance().unauthorized()
                            return@flow
                        }
                    }
                }
            }
            emit(
                DataState.error<List<ShortList>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        val successData = cache.searchShortList(
            query = query,
            filter = filter,
            page = page
        )

        Log.d(TAG, "Success Short List " + successData.size.toString() + successData.toString())


        val successList = mutableListOf<ShortList>()

        for (item in successData) {
            val localMedicine = localMedicineDao.getMedicineById(item.id)?.toLocalMedicine()
            successList.add(
                ShortList(
                    medicine = localMedicine!!,
                    pk = item.pk
                )
            )
        }


        val  failureList = mutableListOf<ShortList>()
        val failureData = cache.searchFailureShortList(
            query = query,
            filter = filter
        )
        Log.d(TAG, "SHort list " + failureData.size + " " + failureData.toString())
        for (item in failureData) {
            val failureLocalMedicine = localMedicineDao.getLocalMedicine(item.id)?.toLocalMedicine()
            failureList.add(
                ShortList(
                    medicine = failureLocalMedicine!!,
                    room_id = item.room_id
                )
            )
        }






        emit(DataState.data(response = null, data = marge(successList, failureList)))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}

fun marge(successList: List<ShortList>, failureList : List<ShortList>) : List<ShortList> {
    val allMedicine  = mutableListOf<ShortList>()
    allMedicine.addAll(failureList)
    allMedicine.addAll(successList)
    return allMedicine
}