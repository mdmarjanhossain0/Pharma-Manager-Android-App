package com.appbytes.pharma_manager.business.domain.models

import com.appbytes.pharma_manager.business.datasource.cache.customer.CustomerEntity
import com.appbytes.pharma_manager.business.datasource.cache.customer.FailureCustomerEntity

data class Customer (
    var pk : Int? = -1,
    var room_id : Long? = -1,
    var name : String,
    var email : String?,
    var mobile : String,
    var whatsapp : String?,
    var facebook : String?,
    var imo : String?,
    var address : String?,
    var date_of_birth : String?,
    var loyalty_point : Int = 0,
    var created_at : String? = null,
    var updated_at : String? = null,
    var total_balance : Float = 0f,
    var due_balance : Float = 0f
)


fun Customer.toCustomerEntity() : CustomerEntity {
    return CustomerEntity(
        pk = pk!!,
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

fun Customer.toCustomerFailureEntity() : FailureCustomerEntity {
    return FailureCustomerEntity(
        name = name,
        email = email,
        mobile = mobile,
        whatsapp = whatsapp,
        facebook = facebook,
        imo = imo,
        address = address,
        date_of_birth = date_of_birth,
        loyalty_point = loyalty_point,
        total_balance = total_balance,
        due_balance = due_balance
    )
}

fun Customer.toCreateCustomer() : CreateCustomer {
    return CreateCustomer(
        name = name,
        email = email,
        mobile = mobile,
        whatsapp = whatsapp,
        facebook = facebook,
        imo = imo,
        address = address,
        date_of_birth = date_of_birth
    )
}