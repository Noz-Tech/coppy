package org.noztech.coppy.navigation

import kotlinx.serialization.Serializable

sealed class GuestRoutes {
    @Serializable
    object Welcome : GuestRoutes()

    @Serializable
    object Auth : GuestRoutes()
}

sealed class AuthRoutes {
    @Serializable
    object Home : AuthRoutes()

    @Serializable
    object Group : AuthRoutes()

    @Serializable
    data class CreateList(val id: Long? = null) : AuthRoutes()

    @Serializable
    data class EntryDetail(val id: Long) : AuthRoutes()

    @Serializable
    object Settings : AuthRoutes()
}
