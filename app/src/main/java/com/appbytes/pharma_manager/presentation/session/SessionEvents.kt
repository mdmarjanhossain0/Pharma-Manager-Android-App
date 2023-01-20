package com.appbytes.pharma_manager.presentation.session

import com.appbytes.pharma_manager.business.domain.models.AuthToken


sealed class SessionEvents {

    object Logout: SessionEvents()

    data class Login(
        val authToken: AuthToken
    ): SessionEvents()

    data class CheckPreviousAuthUser(
        val email: String
    ): SessionEvents()

    object OnRemoveHeadFromQueue: SessionEvents()

}
