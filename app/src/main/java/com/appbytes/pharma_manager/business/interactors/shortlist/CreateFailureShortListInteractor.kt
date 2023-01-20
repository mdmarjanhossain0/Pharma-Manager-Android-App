package com.appbytes.pharma_manager.business.interactors.shortlist

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.LocalMedicineDao
import com.appbytes.pharma_manager.business.datasource.cache.shortlist.ShortListDao
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.shortlist.ShortListApiService
import com.appbytes.pharma_manager.business.datasource.network.shortlist.network_response.toShortList
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.ShortList
import com.appbytes.pharma_manager.business.domain.models.toShortListEntity
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.RequestBody

class CreateFailureShortListInteractor (
    private val service : ShortListApiService,
    private val cache : ShortListDao,
    private val localMedicineDao : LocalMedicineDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?
    ): Flow<DataState<ShortList?>> = flow {

        emit(DataState.loading<ShortList?>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }




        val failureData = cache.getFailureShortList()
        for (item in failureData) {


            try{

                Log.d(TAG, "Call Api Section")
                val id = RequestBody.create(
                    MediaType.parse("text/plain"),
                    item.id.toString()
                )
                val medicine = service.addShortList(
                    "Token ${authToken.token}",
                    id
                ).toShortList()


                try{
                    cache.insertShortList(medicine.toShortListEntity())
                    cache.deleteFailureShortList(item.room_id!!)
                }catch (e: Exception){
                    e.printStackTrace()
                }
/*                emit(
                    DataState.data(response = Response(
                        message = "Successfully Add Short List.",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Success()
                    ), data = medicine))*/


            } catch (e: Exception){
                e.printStackTrace()
            }
        }
        emit(
            DataState.data(response = null))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}