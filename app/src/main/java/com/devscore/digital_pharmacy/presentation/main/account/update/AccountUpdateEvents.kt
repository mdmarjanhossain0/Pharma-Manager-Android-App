package com.devscore.digital_pharmacy.presentation.main.account.update

import com.devscore.digital_pharmacy.business.domain.models.Account
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class AccountUpdateEvents {


    object Logout : AccountUpdateEvents()

    object GetProfile : AccountUpdateEvents()

    data class UpdateQuery(val query: String): AccountUpdateEvents()

    data class AddAccountUpdate(
        val username : String,
        val mobile : String,
        val address : String,
        val profile_picture : String,
        val license_key : String
        ): AccountUpdateEvents()

    data class UpdateImage(val image : String?) : AccountUpdateEvents()

    data class Error(val stateMessage: StateMessage): AccountUpdateEvents()

    object OnRemoveHeadFromQueue: AccountUpdateEvents()
}