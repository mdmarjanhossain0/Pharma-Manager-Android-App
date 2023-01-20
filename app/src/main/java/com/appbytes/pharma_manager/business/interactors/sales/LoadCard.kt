package com.appbytes.pharma_manager.business.interactors.sales

import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.LocalMedicineDao
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.toLocalMedicine
import com.appbytes.pharma_manager.business.datasource.cache.sales.SalesDao
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.sales.SalesApiService
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class LoadCard(
    private val service : SalesApiService,
    private val cache : SalesDao,
    private val localMedicineDao: LocalMedicineDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
    ): Flow<DataState<List<SalesCart>>> = flow {
        emit(DataState.loading<List<SalesCart>>())








        try {
            val cards = cache.getAllCard()
            val salesCard : MutableList<SalesCart> = mutableListOf<SalesCart>()
            for (card in cards) {
                val medicine = localMedicineDao.getLocalMedicine(card.medicine)
                val unit = medicine?.toLocalMedicine()?.units?.find { it.id == card.salesUnit }
                val data = SalesCart(
                    medicine = medicine?.toLocalMedicine(),
                    salesUnit = unit,
                    quantity = card.quantity,
                    amount = card.amount
                )
            }
        }
        catch (e : Exception) {
            
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}