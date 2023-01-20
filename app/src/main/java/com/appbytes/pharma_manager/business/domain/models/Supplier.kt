package com.appbytes.pharma_manager.business.domain.models

import com.appbytes.pharma_manager.business.datasource.cache.supplier.FailureSupplierEntity
import com.appbytes.pharma_manager.business.datasource.cache.supplier.SupplierEntity

data class Supplier (
    var pk : Int? = null,
    var room_id : Long? = null,
    var company_name : String,
    var agent_name : String,
    var email : String?,
    var mobile : String,
    var whatsapp : String?,
    var facebook : String?,
    var imo : String?,
    var address : String?,
    var total_balance : Float = 0f,
    var due_balance : Float = 0f,
    var created_at : String? = null,
    var updated_at : String? = null,
)


fun Supplier.toSupplierEntity() : SupplierEntity {
    return SupplierEntity(
        pk = pk!!,
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
        created_at = created_at,
        updated_at = updated_at
    )
}


fun Supplier.toFailureSupplierEntity() : FailureSupplierEntity {
    return FailureSupplierEntity(
        company_name = company_name,
        agent_name = agent_name,
        email = email,
        mobile = mobile,
        whatsapp = whatsapp,
        facebook = facebook,
        imo = imo,
        address = address,
        total_balance = total_balance,
        due_balance = due_balance
    )
}



fun Supplier.toCreateSupplier() : CreateSupplier {
    return CreateSupplier(
        company_name = company_name,
        agent_name = agent_name,
        email = email,
        mobile = mobile,
        whatsapp = whatsapp,
        facebook = facebook,
        imo = imo,
        address = address,
        due_balance = due_balance,
        total_balance = total_balance
    )
}