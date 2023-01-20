package com.appbytes.pharma_manager.business.domain.models

data class CreateSupplier (
    var company_name : String,
    var agent_name : String,
    var email : String?,
    var mobile : String,
    var whatsapp : String?,
    var facebook : String?,
    var imo : String?,
    var address : String?,
    var total_balance : Float,
    var due_balance : Float
)


fun CreateSupplier.toSupplier() : Supplier {
    return Supplier(
        company_name = company_name,
        agent_name = agent_name,
        email = email,
        mobile = mobile,
        whatsapp = whatsapp,
        facebook = facebook,
        imo = imo,
        address = address,
        total_balance = total_balance,
        due_balance = due_balance,
    )
}