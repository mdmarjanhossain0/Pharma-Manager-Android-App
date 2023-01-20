package com.appbytes.pharma_manager.business.domain.models

data class Report (
    var pk : Int,
    var amount : Float,
    var type : String,
    var details : String?,
    var remark : String?,
    var created_at : String
)