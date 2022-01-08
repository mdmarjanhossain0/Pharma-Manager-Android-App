package com.devscore.digital_pharmacy.business.domain.models

import com.google.gson.annotations.SerializedName

data class Report (
    var pk : Int,
    var amount : Float,
    var type : String,
    var details : String?,
    var remark : String?,
    var created_at : String
)