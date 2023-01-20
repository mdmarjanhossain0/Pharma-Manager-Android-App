package com.appbytes.pharma_manager.presentation.main.account.profie

import com.appbytes.pharma_manager.business.domain.models.Account
import com.appbytes.pharma_manager.business.domain.util.StateMessage

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