package com.appbytes.pharma_manager.business.domain.util

class SuccessHandling {

    companion object{

        const val RESPONSE_PASSWORD_UPDATE_SUCCESS = "successfully changed password"
        const val RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE = "Done checking for previously authenticated user."
        const val RESPONSE_NO_PERMISSION_TO_EDIT = "You don't have permission to edit that."
        const val RESPONSE_HAS_PERMISSION_TO_EDIT = "You have permission to edit that."


        const val SUCCESS_MEDICINE_DOES_NOT_EXIST_IN_CACHE = "Medicine does not exist in the cache."
        const val SUCCESS_MEDICINE_EXISTS_ON_SERVER = "Medicine exists on the server and in the cache."

        const val SUCCESS_ACCOUNT_UPDATED = "Account update success"
        const val SUCCESS_PASSWORD_UPDATED = "successfully changed password"

        const val SUCCESS_LOGOUT = "Logout success."


        // Inventory
        const val SUCCESS_ADD_MEDICINE = "successfully add new medicine."
        const val SUCCESS_DISPENSE_MEDICINE = "successfully dispense medicine"
        const val SUCCESS_UPDATE_MEDICINE = "successfully update the medicine"

        // Supplier
        const val RESPONSE_ADD_SUPPLIER = "successfully add new supplier"


        const val SYNC_FINISH = "Sync Finish..."


    }
}