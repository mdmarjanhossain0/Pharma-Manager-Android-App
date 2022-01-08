package com.devscore.digital_pharmacy.business.domain.models

data class SelectMedicineUnit(
    val unit : MedicineUnits,
    val isSelect : Boolean = false
)