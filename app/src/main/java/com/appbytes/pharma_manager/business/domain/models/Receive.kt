package com.appbytes.pharma_manager.business.domain.models

import com.appbytes.pharma_manager.business.datasource.cache.cashregister.FailureReceiveEntity
import com.appbytes.pharma_manager.business.datasource.cache.cashregister.ReceiveEntity

data class Receive (
    var pk : Int? = null,
    var room_id : Long? = null,
    var date : String,
    var customer : Int?,
    var vendor : Int?,
    var type : String,
    var total_amount : Float,
    var balance : Float,
    var remarks : String?,
    var created_at : String? = null,
    var updated_at : String? = null,
    var customer_name : String? = null,
    var vendor_name : String? = null
)



fun Receive.toReceiveEntity() : ReceiveEntity {
    return ReceiveEntity(
        pk = pk!!,
        date = date,
        customer = customer,
        vendor = vendor,
        type = type,
        total_amount = total_amount,
        balance = balance,
        remarks = remarks!!,
        created_at = created_at!!,
        updated_at = updated_at!!,
        customer_name = customer_name,
        vendor_name = vendor_name
    )
}


fun Receive.toFailureReceive() : FailureReceiveEntity {
    return FailureReceiveEntity(
        date = date,
        customer = customer,
        vendor = vendor,
        type = type,
        total_amount = total_amount,
        balance = balance,
        remarks = remarks!!
    )
}


fun Receive.toCreateReceive() : CreateReceive {
    return CreateReceive(
        date = date,
        customer = customer,
        vendor = vendor,
        type = type,
        total_amount = total_amount,
        balance = balance,
        remarks = remarks!!
    )
}