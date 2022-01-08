package com.devscore.digital_pharmacy.business.datasource.network.account.network_response

import com.devscore.digital_pharmacy.business.datasource.network.inventory.LocalMedicineDto
import com.devscore.digital_pharmacy.business.datasource.network.inventory.toLocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.Employee
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
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