package com.appbytes.pharma_manager.business.domain.models

data class PurchasesCart (
    var medicine : LocalMedicine?,
    var purchasesUnit : MedicineUnits?,
    var quantity : Int?,
    var amount : Float
)