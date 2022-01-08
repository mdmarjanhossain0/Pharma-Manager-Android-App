package com.devscore.digital_pharmacy.presentation.main.account.profie

import com.devscore.digital_pharmacy.business.domain.models.Account
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.presentation.inventory.local.LocalMedicineEvents

sealed class AccountEvents {


    object Logout : AccountEvents()

    object GetProfile : AccountEvents()

    object UpdateProfile : AccountEvents()

    data class UpdateQuery(val query: String): AccountEvents()

    data class AddAccount(val account : Account): AccountEvents()


    data class ChangePassword(
        val currentPassword: String,
        val newPassword: String,
        val confirmNewPassword: String,
    ): AccountEvents()

    data class OnUpdateCurrentPassword(
        val currentPassword: String
    ): AccountEvents()

    data class OnUpdateNewPassword(
        val newPassword: String
    ): AccountEvents()

    data class OnUpdateConfirmNewPassword(
        val confirmNewPassword: String
    ): AccountEvents()

    object OnPasswordChanged: AccountEvents()

    data class Error(val stateMessage: StateMessage): AccountEvents()

    object OnRemoveHeadFromQueue: AccountEvents()
}