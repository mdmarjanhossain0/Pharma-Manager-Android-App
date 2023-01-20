package com.appbytes.pharma_manager.business.interactors.shortlist

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.LocalMedicineDao
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.toLocalMedicine
import com.appbytes.pharma_manager.business.datasource.cache.shortlist.ShortListDao
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.shortlist.ShortListApiService
import com.appbytes.pharma_manager.business.datasource.network.shortlist.network_response.toShortList
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.RequestBody

class CreateShortList (
    private val service : ShortListApiService,
    private val cache : ShortListDao,
    private val localMedicineDao : LocalMedicineDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        medicine_id : Int
    ): Flow<DataState<ShortList>> = flow {

        emit(DataState.loading<ShortList>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try{
            Log.d(TAG, "Call Api Section")
            val id = RequestBody.create(
                MediaType.parse("text/plain"),
                medicine_id.toString()
            )
            val medicine = service.addShortList(
                "Token ${authToken.token}",
                id
            ).toShortList()


            try{
                cache.insertShortList(medicine.toShortListEntity())
            }catch (e: Exception){
                e.printStackTrace()
            }
            emit(
                DataState.data(response = Response(
                    message = "Successfully Add Short List.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = medicine))


        } catch (e: Exception){
            e.printStackTrace()

            try{
                val medicine = localMedicineDao.getMedicineById(medicine_id)?.toLocalMedicine()
                val id = cache.insertFailureShortList(medicine?.toFailureShortListEntity()!!)
                Log.d(TAG, "id " + id.toString())
            } catch (e: Exception){
                e.printStackTrace()
            }


            emit(
                DataState.error<ShortList>(
                    response = Response(
                        message = e.message,
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}