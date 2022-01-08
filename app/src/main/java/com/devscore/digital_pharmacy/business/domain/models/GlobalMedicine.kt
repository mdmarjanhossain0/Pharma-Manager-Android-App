package com.devscore.digital_pharmacy.business.domain.models

data class GlobalMedicine (
    var id : Int,
    var brand_name : String?,
    var sku : String?,
    var darNumber : String?,
    var mrNumber : String?,
    var generic : String?,
    var indication : String?,
    var symptom : String?,
    var strength : String?,
    var description : String?,
    var mrp : Float?,
    var purchases_price : Float?,
    var manufacture : String?,
    var kind : String?,
    var form : String?,
    var createdAt : String?,
    var updatedAt : String?
)