package com.devscore.digital_pharmacy.business.domain.models

data class PurchasesReturnOrder (
    var order : PurchasesOrder,
    var medicineList : List<LocalMedicine>?
)