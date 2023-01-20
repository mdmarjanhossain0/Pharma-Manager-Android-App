package com.appbytes.pharma_manager.business.domain.models

data class SalesReturnOrder (
    var order : SalesOrder,
    var medicineList : List<LocalMedicine>?
        )