package com.devscore.digital_pharmacy.business.datasource.network.customer.network_response

import com.devscore.digital_pharmacy.business.domain.models.Customer

data class CustomerCreateResponse (
    var pk : Int,
    var name : String,
    var email : String?,
    var mobile : String,
    var whatsapp : String?,
    var facebook : String?,
    var imo : String?,
    var address : String?,
    var date_of_birth : String?,
    var loyalty_point : Int,
    var created_at : String?,
    var updated_at : String?,
    var total_balance : Float,
    var due_balance : Float,


    var response : String?,
    var errorMessage : String?
)


fun CustomerCreateResponse.toCustomer() : Customer {
    return Customer(
        pk = pk,
        name = name,
        email = email,
        mobile = mobile,
        whatsapp = whatsapp,
        facebook = facebook,
        imo = imo,
        address = address,
        date_of_birth = date_of_birth,
        loyalty_point = loyalty_point,
        created_at = created_at,
        updated_at = updated_at,
        total_balance = total_balance,
        due_balance = due_balance
    )
}