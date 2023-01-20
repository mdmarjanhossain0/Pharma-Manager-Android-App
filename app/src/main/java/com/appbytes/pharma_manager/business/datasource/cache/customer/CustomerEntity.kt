package com.appbytes.pharma_manager.business.datasource.cache.customer

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appbytes.pharma_manager.business.domain.models.Customer

@Entity(tableName = "AppClientCustomer")
data class CustomerEntity (

    @PrimaryKey(autoGenerate = false)
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
    var due_balance : Float
)


fun CustomerEntity.toCustomer() : Customer {
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