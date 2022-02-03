package com.devscore.digital_pharmacy.business.interactors.report

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineEntity
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineUnitEntity
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.inventory.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.network.report.ReportApiService
import com.devscore.digital_pharmacy.business.datasource.network.report.network_response.toReport
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.Report
import com.devscore.digital_pharmacy.business.domain.util.*
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchReport(
    private val service : ReportApiService
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query : String? = null,
        start : String? = null,
        end : String? = null,
        page: Int
    ): Flow<DataState<List<Report>>> = flow {
        emit(DataState.loading<List<Report>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
            Log.d(TAG, "Call Api Section")
            Log.d(TAG, query?.toLowerCase() + " " + start + " " + end)
            val reportResponse = service.searchReport(
                "Token ${authToken.token}",
                query = query?.toLowerCase(),
                start = start,
                end = end,
                page = page
            )

            if (reportResponse.error == "Your are not owner") {
                emit(
                    DataState.error<List<Report>>(
                        response = Response(
                            message = "Your are not owner",
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Error()
                        )
                    )
                )
                return@flow
            }
            val report = reportResponse.results.map {
                Log.d(TAG, "looping toLocalMedicine")
                it.toReport()
            }
            emit(DataState.data(response = null, data = report))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}