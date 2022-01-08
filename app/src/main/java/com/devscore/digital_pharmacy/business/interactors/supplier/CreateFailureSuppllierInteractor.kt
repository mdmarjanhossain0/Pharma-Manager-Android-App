package com.devscore.digital_pharmacy.business.interactors.supplier

import android.content.Context
import android.util.Log
import androidx.work.*
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.SupplierDao
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.supplier.SupplierApiService
import com.devscore.digital_pharmacy.business.datasource.network.supplier.network_response.toSupplier
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.presentation.util.SupplierWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateFailureSuppllierInteractor (
    private val service : SupplierApiService,
    private val cache : SupplierDao,
    private val context: Context
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        suppliers: List<Supplier>
    ): Flow<DataState<Supplier>> = flow {

        emit(DataState.loading<Supplier>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        for (createSupplier in suppliers) {
            try{
                Log.d(TAG, "Call Api Section")
                val supplier = service.createSupplier(
                    "Token ${authToken.token}",
                    createSupplier.toCreateSupplier()
                ).toSupplier()


                Log.d(TAG, "CreateSupplierInteractior " + supplier.toString())

                try{
                    cache.insertSupplier(supplier.toSupplierEntity())
                }catch (e: Exception){
                    e.printStackTrace()
                }




                try{
                    cache.deleteFailureSupplier(createSupplier.room_id!!)
                }catch (e: Exception){
                    e.printStackTrace()
                }

//                emit(
//                    DataState.data(response = Response(
//                        message = "Successfully Uploaded.",
//                        uiComponentType = UIComponentType.Dialog(),
//                        messageType = MessageType.Success()
//                    ), data = supplier))
//                return@flow


            }



            catch (e: Exception){
                e.printStackTrace()

                /*try{
                    val draftSupplier = createSupplier.copy(
                        room_id = null
                    )
                    cache.insertFailureSupplier(draftSupplier.toFailureSupplierEntity())
                } catch (e: Exception){
                    e.printStackTrace()
                }*/
            }
        }

//        emit(
//            DataState.data(response = Response(
//                message = "Create a draft supplier. Please be careful and don't uninstall or log out",
//                uiComponentType = UIComponentType.Dialog(),
//                messageType = MessageType.Error()
//            ), data = suppliers[0]))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}