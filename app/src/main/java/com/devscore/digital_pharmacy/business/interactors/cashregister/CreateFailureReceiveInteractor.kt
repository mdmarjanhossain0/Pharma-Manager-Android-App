package com.devscore.digital_pharmacy.business.interactors.cashregister

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.cashregister.ReceiveDao
import com.devscore.digital_pharmacy.business.datasource.cache.cashregister.toReceive
import com.devscore.digital_pharmacy.business.datasource.network.cashregister.CashRegisterApiService
import com.devscore.digital_pharmacy.business.datasource.network.cashregister.network_response.toReceive
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateFailureReceiveInteractor (
    private val service : CashRegisterApiService,
    private val cache : ReceiveDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?
    ): Flow<DataState<Receive>> = flow {

        emit(DataState.loading<Receive>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        val receives = cache.getFailureReceives()



        for (item in receives) {
            val createReceive = item.toReceive().toCreateReceive()
            if (createReceive.customer == -1) {
                createReceive.customer = null
            }

            if (createReceive.vendor == -1) {
                createReceive.vendor = null
            }

            try{
                Log.d(TAG, "Call Api Section")
                val receive = service.createReceive(
                    "Token ${authToken.token}",
                    createReceive
                ).toReceive()


                try{
                    Log.d(TAG, "Receive Cache")
                    Log.d(TAG, "Receive Interactor " + item.room_id)
                    cache.insertReceive(receive.toReceiveEntity())
                    cache.deleteFailureReceive(item.room_id!!)
                }catch (e: Exception){
                    e.printStackTrace()

                }


            } catch (e: Exception){
                e.printStackTrace()
            }
        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}