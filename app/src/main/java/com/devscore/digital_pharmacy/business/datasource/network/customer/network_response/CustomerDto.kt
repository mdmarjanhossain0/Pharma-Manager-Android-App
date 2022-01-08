package com.devscore.digital_pharmacy.business.datasource.network.customer.network_response

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.google.gson.annotations.SerializedName

data class CustomerDto (

    @SerializedName("id")
    var pk : Int,

    @SerializedName("name")
    var name : String,

    @SerializedName("email")
    var email : String?,

    @SerializedName("mobile")
    var mobile : String,

    @SerializedName("whatsapp")
    var whatsapp : String?,

    @SerializedName("facebook")
    var facebook : String?,

    @SerializedName("imo")
    var imo : String?,

    @SerializedName("address")
    var address : String?,

    @SerializedName("total_balance")
    var total_balance : Float,

    @SerializedName("due_balance")
    var due_balance : Float,

    @SerializedName("date_of_birth")
    var date_of_birth : String?,

    @SerializedName("loyalty_point")
    var loyalty_point : Int,

    @SerializedName("created_at")
    var created_at : String?,

    @SerializedName("updated_at")
    var updated_at : String?,
)

fun CustomerDto.toCustomer() : Customer {
    return Customer(
        pk = pk,
        name = name,
        email = email,
        mobile = mobile,
        whatsapp = whatsapp,
        facebook = facebook,
        imo = imo,
        address = address,
        total_balance = total_balance,
        due_balance = due_balance,
        date_of_birth = date_of_birth,
        loyalty_point = loyalty_point,
        created_at = created_at,
        updated_at = updated_at
    )
}