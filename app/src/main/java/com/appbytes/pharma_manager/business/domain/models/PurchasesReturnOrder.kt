package com.appbytes.pharma_manager.business.domain.models

data class PurchasesReturnOrder (
    var order : PurchasesOrder,
    var medicineList : List<LocalMedicine>?
)