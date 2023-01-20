package com.appbytes.pharma_manager.business.domain.models

data class CreateReceive (
    var date : String,
    var customer : Int?,
    var vendor : Int?,
    var type : String,
    var total_amount : Float,
    var balance : Float,
    var remarks : String
)



fun CreateReceive.toReceive() : Receive {
    return Receive(
        date = date,
        customer = customer,
        vendor = vendor,
        type = type,
        total_amount = total_amount,
        balance = balance,
        remarks = remarks,
    )
}