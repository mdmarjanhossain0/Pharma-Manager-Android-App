package com.devscore.digital_pharmacy.business.domain.models

data class Notification (
    var brand_name : String,
    var id : Int,
    var type : String,
    var message : String,
    var stock : Float,
    var quantity : Float,
    var exp_date : String?
        )