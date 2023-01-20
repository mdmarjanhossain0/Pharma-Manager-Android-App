package com.appbytes.pharma_manager.business.domain.models

data class SelectMedicineUnit(
    val unit : MedicineUnits,
    val isSelect : Boolean = false
)