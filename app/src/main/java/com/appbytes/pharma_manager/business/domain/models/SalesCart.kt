package com.appbytes.pharma_manager.business.domain.models

data class SalesCart (
    var medicine : LocalMedicine?,
    var salesUnit : MedicineUnits?,
    var quantity : Int?,
    var amount : Float?
        )