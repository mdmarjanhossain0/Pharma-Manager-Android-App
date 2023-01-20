package com.appbytes.pharma_manager.business.domain.models

data class DispensingMedicine(
    var localMedicine: LocalMedicine,
    var dispensingQuantity : Int = 1
)