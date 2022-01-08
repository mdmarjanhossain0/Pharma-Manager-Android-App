package com.devscore.digital_pharmacy.business.domain.models

data class PurchasesCart (
    var medicine : LocalMedicine?,
    var purchasesUnit : MedicineUnits?,
    var quantity : Int?,
    var amount : Float
)