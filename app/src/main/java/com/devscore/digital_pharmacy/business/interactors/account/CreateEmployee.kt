package com.devscore.digital_pharmacy.business.interactors.account

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.account.EmployeeDao
import com.devscore.digital_pharmacy.business.datasource.cache.account.toEmployeeEntity
import com.devscore.digital_pharmacy.business.datasource.cache.account.toEntity
import com.devscore.digital_pharmacy.business.datasource.cache.auth.toEntity
import com.devscore.digital_pharmacy.business.datasource.cache.customer.CustomerDao
import com.devscore.digital_pharmacy.business.datasource.network.account.AccountApiService
import com.devscore.digital_pharmacy.business.datasource.network.customer.CustomerApiService
import com.devscore.digital_pharmacy.business.datasource.network.customer.network_response.toCustomer
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class CreateEmployee (
    private val service : AccountApiService,
    private val cache : EmployeeDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        email: String,
        username: String,
        password: String,
        confirmPassword: String,
        mobile : String,
        address : String,
        role : String
    ): Flow<DataState<Employee>> = flow {
        emit(DataState.loading<Employee>())
        val registerResponse = service.createEmployee(
            "Token ${authToken?.token}",
            email = email,
            username = username,
            password = password,
            password2 = confirmPassword,
            mobile = mobile,
            address = address,
            role = role
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
            registerResponse.profile_picture!!,
//                registerResponse.business_name,
            registerResponse.mobile,
            registerResponse.license_key,
            registerResponse.address,
            registerResponse.is_employee,
            registerResponse.role
        )
        // cache account information
        cache.insertEmployee(
            employee.toEmployeeEntity()
        )
        emit(DataState.data(data = employee, response = Response(
            message = "Successfully Add New User.",
            uiComponentType = UIComponentType.Dialog(),
            messageType = MessageType.Success()
        )))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}