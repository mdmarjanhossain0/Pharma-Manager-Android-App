package com.appbytes.pharma_manager.business.interactors.account

import com.appbytes.pharma_manager.business.datasource.cache.account.EmployeeDao
import com.appbytes.pharma_manager.business.datasource.cache.account.toEmployeeEntity
import com.appbytes.pharma_manager.business.datasource.network.account.AccountApiService
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.Employee
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class UpdateEmployee (
    private val service : AccountApiService,
    private val cache : EmployeeDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int,
        email: String,
        username: String,
        mobile : String,
        address : String,
        role : String?,
        is_active : Boolean
    ): Flow<DataState<Employee>> = flow {
        emit(DataState.loading<Employee>())
        val registerResponse = service.updateEmployee(
            "Token ${authToken?.token}",
            pk = pk,
            email = email,
            username = username,
            mobile = mobile,
            address = address,
            role = role,
            is_active = is_active
        )
        // Incorrect login credentials counts as a 200 response from server, so need to handle that
        if(registerResponse.response.equals(ErrorHandling.GENERIC_AUTH_ERROR)){
            throw Exception(registerResponse.errorMessage)
        }

        if(registerResponse.response.equals(ErrorHandling.YOU_ARE_NOT_EMPLOYEE)){
            throw Exception(registerResponse.errorMessage)
        }


        val employee = Employee(
            registerResponse.pk,
            registerResponse.email,
            registerResponse.username,
            registerResponse.profile_picture,
//                registerResponse.business_name,
            registerResponse.mobile,
            registerResponse.license_key,
            registerResponse.address,
            registerResponse.is_employee,
            registerResponse.role,
            registerResponse.is_active
        )
        // cache account information
        cache.insertEmployee(
            employee.toEmployeeEntity()
        )
        emit(
            DataState.data(data = employee, response = Response(
            message = "Successfully Update User.",
            uiComponentType = UIComponentType.Toast(),
            messageType = MessageType.Success()
        )
            ))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}