package com.devscore.digital_pharmacy.business.datasource.network.inventory

class InventoryUtils {

    companion object {
        private val TAG: String = "AppDebug"

        // values
        const val ORDER_ASC: String = ""
        const val ORDER_DESC: String = "-"
        const val FILTER_USERNAME = "username"
        const val FILTER_DATE_UPDATED = "date_updated"

        val ORDER_BY_ASC_DATE_UPDATED = ORDER_ASC + FILTER_DATE_UPDATED
        val ORDER_BY_DESC_DATE_UPDATED = ORDER_DESC + FILTER_DATE_UPDATED
        val ORDER_BY_ASC_USERNAME = ORDER_ASC + FILTER_USERNAME
        val ORDER_BY_DESC_USERNAME = ORDER_DESC + FILTER_USERNAME



        const val BRAND_NAME = "brand_name"
        const val GENERIC = "generic"
        const val INDICATION = "indication"
        const val SYMPTOM = "symptom"
    }
}