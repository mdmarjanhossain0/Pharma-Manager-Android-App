package com.devscore.digital_pharmacy.business.interactors.cashregister

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.cashregister.PaymentDao
import com.devscore.digital_pharmacy.business.datasource.cache.cashregister.ReceiveDao
import com.devscore.digital_pharmacy.business.datasource.network.cashregister.CashRegisterApiService
import com.devscore.digital_pharmacy.business.datasource.network.cashregister.network_response.toPayment
import com.devscore.digital_pharmacy.business.datasource.network.cashregister.network_response.toReceive
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreatePaymentInteractor (
    private val service : CashRegisterApiService,
    private val cache : PaymentDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        createPayment : CreatePayment
    ): Flow<DataState<Payment>> = flow {

        emit(DataState.loading<Payment>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

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
                cache.insertPayment(payment.toPaymentEntity())
            }catch (e: Exception){
                e.printStackTrace()

            }

            emit(
                DataState.data(response = Response(
                    message = "Successfully Uploaded.",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Success()
                ), data = payment))
            return@flow


        } catch (e: Exception){
            e.printStackTrace()

            try{
                cache.insertFailurePayment(createPayment.toPayment().toFailurePayment())
            } catch (e: Exception){
                e.printStackTrace()
            }


            emit(
                DataState.error<Payment>(
                    response = Response(
                        message = "Unable to create Sales Oder. Please be careful and don't uninstall or log out",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            )
            return@flow
        }



        val stateFailure = createPayment.toPayment()


        emit(
            DataState.data(response = Response(
                message = "Unable to create supplier. Please be careful and don't uninstall or log out",
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Error()
            ), data = stateFailure))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}