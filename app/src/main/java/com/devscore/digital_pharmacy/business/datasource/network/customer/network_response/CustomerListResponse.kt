package com.devscore.digital_pharmacy.business.datasource.network.customer.network_response

import com.devscore.digital_pharmacy.business.datasource.network.supplier.SupplierDto
import com.devscore.digital_pharmacy.business.datasource.network.supplier.toSupplier
import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.Supplier
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