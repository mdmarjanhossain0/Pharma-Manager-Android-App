package com.devscore.digital_pharmacy.business.domain.models

data class SalesReturnOrder (
    var order : SalesOrder,
    var medicineList : List<LocalMedicine>?
        )