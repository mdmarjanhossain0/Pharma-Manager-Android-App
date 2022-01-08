package com.devscore.digital_pharmacy.business.interactors.customer

import android.content.Context
import android.util.Log
import androidx.work.*
import com.devscore.digital_pharmacy.business.datasource.cache.customer.CustomerDao
import com.devscore.digital_pharmacy.business.datasource.network.customer.CustomerApiService
import com.devscore.digital_pharmacy.business.datasource.network.customer.network_response.toCustomer
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.presentation.util.CustomerWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateFailureCustomerInteractor (
    private val service : CustomerApiService,
    private val cache : CustomerDao,
    private val context: Context
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        customers: List<Customer>
    ): Flow<DataState<Customer>> = flow {

        emit(DataState.loading<Customer>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        for (createCustomer in customers) {
            try{
                Log.d(TAG, "Call Api Section")
                val customer = service.createCustomer(
                    "Token ${authToken.token}",
                    createCustomer.toCreateCustomer()
                ).toCustomer()


                try{
                    cache.insertCustomer(customer.toCustomerEntity())
                }catch (e: Exception){
                    e.printStackTrace()
                }


                try{
                    cache.deleteFailureCustomer(createCustomer.room_id!!)
                }catch (e: Exception){
                    e.printStackTrace()
                }


//                emit(
//                    DataState.data(response = Response(
//                        message = "Successfully Uploaded.",
//                        uiComponentType = UIComponentType.Dialog(),
//                        messageType = MessageType.Success()
//                    ), data = customer))
//
//                return@flow


            } catch (e: Exception){
                e.printStackTrace()

                /*try{
                    val draftCustomer = createCustomer.copy(
                        room_id = null
                    )
                    cache.insertFailureCustomer(draftCustomer.toCustomerFailureEntity())
                } catch (e: Exception){
                    e.printStackTrace()
                }*/
            }
        }

//        emit(
//            DataState.data(response = Response(
//                message = "Create a draft customer. Please be careful and don't uninstall or log out",
//                uiComponentType = UIComponentType.None(),
//                messageType = MessageType.Error()
//            ), data = customers[0]))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}