package com.appbytes.pharma_manager.business.datasource.cache.cashregister

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appbytes.pharma_manager.business.domain.models.Receive


@Entity(tableName = "FailureReceive")
data class FailureReceiveEntity (

    @PrimaryKey(autoGenerate = true)
    var room_id : Long? = null,
    var date : String,
    var customer : Int?,
    var vendor : Int?,
    var type : String,
    var total_amount : Float,
    var balance : Float,
    var remarks : String
)


fun FailureReceiveEntity.toReceive() : Receive {
    return Receive(
        room_id = room_id,
        date = date,
        customer = customer,
        vendor = vendor,
        type = type,
        total_amount = total_amount,
        balance = balance,
        remarks = remarks
    )
}