package com.devscore.digital_pharmacy.business.interactors.purchases

import android.content.Context
import android.util.Log
import androidx.work.*
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.PurchasesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.purchases.PurchasesApiService
import com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response.toPurchasesOrder
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.presentation.util.PurchasesWorker
import com.devscore.digital_pharmacy.presentation.util.SalesWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreatePurchasesOrderInteractor (
    private val service : PurchasesApiService,
    private val cache : PurchasesDao,
    private val context: Context
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        createPurchasesOder: CreatePurchasesOder
    ): Flow<DataState<PurchasesOrder>> = flow {

        emit(DataState.loading<PurchasesOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        if (createPurchasesOder.vendor == -1) {
            createPurchasesOder.vendor = null
        }

        try{
            Log.d(TAG, "Call Api Section")
            val purchasesOrder = service.createPurchasesOder(
                "Token ${authToken.token}",
                createPurchasesOder
            ).toPurchasesOrder()


            try{
                cache.insertPurchasesOrder(purchasesOrder.toPurchasesOrderEntity())
                for (medicine in purchasesOrder.toPurchasesOrderMedicines()) {
                    cache.insertPurchasesOrderMedicine(medicine)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

            emit(
                DataState.data(response = Response(
                    message = "Successfully Uploaded.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = purchasesOrder))
            return@flow


        } catch (e: Exception){
            e.printStackTrace()

            try{
                val order = createPurchasesOder.toPurchasesOrder()
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
            }
        }



        val purchasesOrder = createPurchasesOder.toPurchasesOrder()


        emit(
            DataState.data(response = Response(
                message = "Create a draft order. Please be careful and don't uninstall or log out",
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Error()
            ), data = purchasesOrder))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}