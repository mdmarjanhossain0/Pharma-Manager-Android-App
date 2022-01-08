package com.devscore.digital_pharmacy.business.interactors.cashregister

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.cashregister.PaymentDao
import com.devscore.digital_pharmacy.business.datasource.cache.cashregister.toPayment
import com.devscore.digital_pharmacy.business.datasource.network.cashregister.CashRegisterApiService
import com.devscore.digital_pharmacy.business.datasource.network.cashregister.network_response.toPayment
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateFailurePaymentInteractor (
    private val service : CashRegisterApiService,
    private val cache : PaymentDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?
    ): Flow<DataState<Payment>> = flow {

        emit(DataState.loading<Payment>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }





        val payments = cache.getFailurePayments()

        for (item in payments) {
            val createPayment = item.toPayment().toCreatePayment()



            if (createPayment.customer == -1) {
                createPayment.customer = null
            }

            if (createPayment.vendor == -1) {
                createPayment.vendor = null
            }

            try{
                Log.d(TAG, "Call Api Section")
                val payment = service.createPayment(
                    "Token ${authToken.token}",
                    createPayment
                ).toPayment()


                try{
                    Log.d(TAG, "Payment Cache")
                    Log.d(TAG, "Payment Interactor " + item.room_id)
                    cache.insertPayment(payment.toPaymentEntity())
                    cache.deleteFailurePayment(item.room_id!!)
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