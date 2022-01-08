package com.devscore.digital_pharmacy.business.interactors.report

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesDetailsMonth
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.report.ReportApiService
import com.devscore.digital_pharmacy.business.datasource.network.report.network_response.toSalesDetailsMonth
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetMonthDetailsSales(
    private val service : ReportApiService,
    private val cache : SalesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?
    ): Flow<DataState<SalesDetailsMonth>> = flow {
        emit(DataState.loading<SalesDetailsMonth>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try{
            Log.d(TAG, "Call Api Section")
            val result = service.monthDetailsSales(
                "Token ${authToken.token}"
            )

            Log.d(TAG, result.toString())

            val details = result.toSalesDetailsMonth()

                try{
                    cache.insertSalesDetailMonth(details.toSalesDetailsMonthEntity())
                }catch (e: Exception){
                    e.printStackTrace()
                }
        }
        catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.error<SalesDetailsMonth>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        val successList = cache.getSalesDetails().toSalesDetailsMonth()





        emit(DataState.data(response = null, data = successList))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}