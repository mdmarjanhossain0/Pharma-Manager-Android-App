package com.devscore.digital_pharmacy.presentation.session

import com.devscore.digital_pharmacy.business.domain.models.AuthToken


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
