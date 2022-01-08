package com.devscore.digital_pharmacy.business.domain.models

data class SalesCart (
    var medicine : LocalMedicine?,
    var salesUnit : MedicineUnits?,
    var quantity : Int?,
    var amount : Float?
        )