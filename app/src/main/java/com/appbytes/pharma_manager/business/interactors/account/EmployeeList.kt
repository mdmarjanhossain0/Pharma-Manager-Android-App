package com.appbytes.pharma_manager.business.interactors.account

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.account.EmployeeDao
import com.appbytes.pharma_manager.business.datasource.cache.account.toEmployee
import com.appbytes.pharma_manager.business.datasource.cache.account.toEmployeeEntity
import com.appbytes.pharma_manager.business.datasource.network.account.AccountApiService
import com.appbytes.pharma_manager.business.datasource.network.account.network_response.toEmployee
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.Employee
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class EmployeeList (
    private val service : AccountApiService,
    private val cache : EmployeeDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        page: Int
    ): Flow<DataState<List<Employee>>> = flow {
        emit(DataState.loading<List<Employee>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try{
            Log.d(TAG, "Call Api Section")
            val employees = service.getEmployeeList(
                "Token ${authToken.token}"
            ).results.map {
                Log.d(TAG, it.toString())
                Log.d(TAG, "looping Customer")
                it.toEmployee()
            }


            for (item in employees) {
                cache.insertEmployee(item.toEmployeeEntity())
            }

        }catch (e: Exception){
            e.printStackTrace()
            emit(
                DataState.error<List<Employee>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        val state = cache.getEmployeeList().map { it.toEmployee() }





        emit(DataState.data(response = null, data = state))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}