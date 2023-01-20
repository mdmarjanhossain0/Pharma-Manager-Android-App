package com.appbytes.pharma_manager.business.interactors.purchases

import android.content.Context
import android.util.Log
import androidx.work.*
import com.appbytes.pharma_manager.business.datasource.cache.purchases.PurchasesDao
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.purchases.PurchasesApiService
import com.appbytes.pharma_manager.business.datasource.network.purchases.network_response.toPurchasesOrder
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateFailurePurchasesOrderInteractor (
    private val service : PurchasesApiService,
    private val cache : PurchasesDao,
    private val context: Context
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        orders: List<PurchasesOrder>
    ): Flow<DataState<PurchasesOrder>> = flow {

        emit(DataState.loading<PurchasesOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        for (createPurchasesOder in orders) {
            if (createPurchasesOder.vendor == -1) {
                createPurchasesOder.vendor = null
            }

            try{
                Log.d(TAG, "Call Api Section")
                val purchasesOrder = service.createPurchasesOder(
                    "Token ${authToken.token}",
                    createPurchasesOder.toCreatePurchasesOrder()
                ).toPurchasesOrder()


                try{
                    cache.insertPurchasesOrder(purchasesOrder.toPurchasesOrderEntity())
                    for (medicine in purchasesOrder.toPurchasesOrderMedicines()) {
                        cache.insertPurchasesOrderMedicine(medicine)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }

                try{
                    cache.deleteFailurePurchasesOrder(createPurchasesOder.room_id!!)
                }catch (e: Exception){
                    e.printStackTrace()
                }



            } catch (e: Exception){
                e.printStackTrace()

                /*try{
                    val order = createPurchasesOder
                    Log.d(TAG, "Order " + order.toString())
                    val room_id = cache.insertFailurePurchasesOrder(order.toFailurePurchasesOrderEntity())
                    Log.d(TAG, "Room id" + room_id.toString())
                    val draft = order.copy(
                        room_id = room_id
                    )
                    for (failureMedicine in draft.toFailurePurchasesOrderMedicineEntity()) {
                        val id = cache.insertFailurePurchasesOrderMedicine(failureMedicine)
                        Log.d(TAG, "id " + id.toString())
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                }*/
            }
        }


//        emit(
//            DataState.data(response = Response(
//                message = "Create a draft order. Please be careful and don't uninstall or log out",
//                uiComponentType = UIComponentType.None(),
//                messageType = MessageType.Error()
//            ), data = orders[0]))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}