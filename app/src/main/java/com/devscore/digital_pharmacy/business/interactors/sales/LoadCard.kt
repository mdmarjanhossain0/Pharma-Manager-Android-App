package com.devscore.digital_pharmacy.business.interactors.sales

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
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