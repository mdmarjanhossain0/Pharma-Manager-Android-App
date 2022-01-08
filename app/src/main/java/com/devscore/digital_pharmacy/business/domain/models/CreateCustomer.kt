package com.devscore.digital_pharmacy.business.domain.models


data class CreateCustomer (
    var name : String,
    var email : String?,
    var mobile : String,
    var whatsapp : String?,
    var facebook : String?,
    var imo : String?,
    var address : String?,
    var date_of_birth : String?
)


fun CreateCustomer.toCustomer() : Customer {
    return Customer(
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