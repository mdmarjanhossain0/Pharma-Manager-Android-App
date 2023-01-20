package com.appbytes.pharma_manager.business.datasource.network.customer.network_response

import com.appbytes.pharma_manager.business.domain.models.Customer
import com.google.gson.annotations.SerializedName

data class CustomerListResponse (

    @SerializedName("count") var count : Int?,
    @SerializedName("next") var next : String?,
    @SerializedName("previous") var previous : String?,
    @SerializedName("results") var results : List<CustomerDto>,

    @SerializedName("detail")
    var detail: String?
)


fun CustomerListResponse.toList() : List<Customer> {
    val list : MutableList<Customer> = mutableListOf()
    for (dto in results){
        list.add(
            dto.toCustomer()
        )
    }
    return list
}