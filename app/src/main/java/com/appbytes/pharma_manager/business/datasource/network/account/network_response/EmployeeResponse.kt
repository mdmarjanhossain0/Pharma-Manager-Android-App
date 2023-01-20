package com.appbytes.pharma_manager.business.datasource.network.account.network_response

import com.appbytes.pharma_manager.business.domain.models.Employee
import com.google.gson.annotations.SerializedName

data class EmployeeResponse (

    @SerializedName("count") var count : Int?,
    @SerializedName("next") var next : String?,
    @SerializedName("previous") var previous : String?,
    @SerializedName("results") var results : List<EmployeeDto>

)

fun EmployeeResponse.toList() : List<Employee> {
    val list : MutableList<Employee> = mutableListOf()
    for (dto in results!!) {
        list.add(
            dto.toEmployee()
        )
    }
    return list
}