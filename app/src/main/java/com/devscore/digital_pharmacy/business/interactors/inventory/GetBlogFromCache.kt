package com.devscore.digital_pharmacy.business.interactors.inventory

//import com.devscore.digital_pharmacy.business.datasource.cache.inventory.global.GlobalMedicineDao
//import com.devscore.digital_pharmacy.business.datasource.cache.inventory.toGlobalMedicine
//import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
//import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
//import com.devscore.digital_pharmacy.business.domain.util.DataState
//import com.devscore.digital_pharmacy.business.domain.util.ErrorHandling.Companion.ERROR_MEDICINE_UNABLE_TO_RETRIEVE
//import com.devscore.digital_pharmacy.business.domain.util.MessageType
//import com.devscore.digital_pharmacy.business.domain.util.Response
//import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.flow
//
//class GetGlobalMedicineFromCache(
//    private val cache: GlobalMedicineDao
//) {
//
//    fun execute(
//        pk: Int,
//    ): Flow<DataState<GlobalMedicine>> = flow{
//        emit(DataState.loading<GlobalMedicine>())
//        val blogPost = cache.getGlobalMedicine(pk)?.toGlobalMedicine()
//
//        if(blogPost != null){
//            emit(DataState.data(response = null, data = blogPost))
//        }
//        else{
//            emit(DataState.error<GlobalMedicine>(
//                response = Response(
//                    message = ERROR_MEDICINE_UNABLE_TO_RETRIEVE,
//                    uiComponentType = UIComponentType.Dialog(),
//                    messageType = MessageType.Error()
//                )
//            ))
//        }
//    }.catch { e ->
//        emit(handleUseCaseException(e))
//    }
//}



















