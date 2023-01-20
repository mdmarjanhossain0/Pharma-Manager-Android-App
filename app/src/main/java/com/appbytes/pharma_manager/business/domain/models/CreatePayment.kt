package com.appbytes.pharma_manager.business.domain.models


data class CreatePayment (
    var date : String,
    var customer : Int?,
    var vendor : Int?,
    var type : String,
    var total_amount : Float,
    var balance : Float,
    var remarks : String
)


fun CreatePayment.toPayment() : Payment {
    return Payment(
        date = date,
        customer = customer,
        vendor = vendor,
        type = type,
        total_amount = total_amount,
        balance = balance,
        remarks = remarks,
    )
}