package com.devscore.digital_pharmacy.business.interactors.account

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.account.EmployeeDao
import com.devscore.digital_pharmacy.business.datasource.cache.account.toEmployee
import com.devscore.digital_pharmacy.business.datasource.cache.account.toEmployeeEntity
import com.devscore.digital_pharmacy.business.datasource.network.account.AccountApiService
import com.devscore.digital_pharmacy.business.datasource.network.account.network_response.toEmployee
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.Employee
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetEmployee (
    private val service : AccountApiService,
    private val cache : EmployeeDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int
    ): Flow<DataState<Employee>> = flow {
        emit(DataState.loading<Employee>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try{
            Log.d(TAG, "Call Api Section")
            val employee = service.getEmployee(
                "Token ${authToken.token}",
                pk = pk
            ).toEmployee()

            cache.insertEmployee(employee = employee.toEmployeeEntity())

        }catch (e: Exception){
            e.printStackTrace()
        }

        val state = cache.getEmployee(pk).toEmployee()


        emit(DataState.data(response = null, data = state))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}