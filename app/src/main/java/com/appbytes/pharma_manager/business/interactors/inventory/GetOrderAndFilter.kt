package com.appbytes.pharma_manager.business.interactors.inventory

//class GetOrderAndFilter(
//    private val appDataStoreManager: AppDataStore
//) {
//    fun execute(): Flow<DataState<OrderAndFilter>> = flow {
//        emit(DataState.loading<OrderAndFilter>())
//        val filter = appDataStoreManager.readValue(DataStoreKeys.BLOG_FILTER)?.let { filter ->
//            getFilterFromValue(filter)
//        }?: getFilterFromValue(BlogFilterOptions.DATE_UPDATED.value)
//        val order = appDataStoreManager.readValue(DataStoreKeys.BLOG_ORDER)?.let { order ->
//            getOrderFromValue(order)
//        }?: getOrderFromValue(BlogOrderOptions.DESC.value)
//        emit(DataState.data(
//            response = null,
//            data = OrderAndFilter(order = order, filter = filter)
//        ))
//    }.catch { e ->
//        emit(handleUseCaseException(e))
//    }
//}










