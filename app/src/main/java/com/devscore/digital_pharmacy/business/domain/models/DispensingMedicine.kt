package com.devscore.digital_pharmacy.business.domain.models

data class DispensingMedicine(
    var localMedicine: LocalMedicine,
    var dispensingQuantity : Int = 1
)